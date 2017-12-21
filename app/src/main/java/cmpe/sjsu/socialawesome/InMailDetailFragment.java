package cmpe.sjsu.socialawesome;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cmpe.sjsu.socialawesome.Utils.DbUtils;
import cmpe.sjsu.socialawesome.Utils.HTTPUtil;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.InMailMessage;
import cmpe.sjsu.socialawesome.models.PushMessageContent;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.InMailActivity.BUNDLE_MESSAGE_ID;
import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

/**
 * Created by lam on 4/28/17.
 */

public class InMailDetailFragment extends SocialFragment {
    public static final String STRING_IN_MAIL_KEY = "string_inmail_key";
    public static final String IN_MAIL_SUBJECT = "IN_MAIL_SUBJECT";
    public static final String IN_MAIL_MESSAGE = "IN_MAIL_MESSAGE";
    public static final String IN_MAIL_EMAIL_ADDRESS = "IN_MAIL_EMAIL_ADDRESS";
    final DatabaseReference mSelfRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE)
            .child(UserAuth.getInstance().getCurrentUser().id).child(User.IN_MAIL);
    private View mUserNameEt;
    private View mSubjectEt;
    private View mContentEt;
    private ImageView mUserImage;
    private Button mSendButton;
    private boolean mIsNewChat = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        @LayoutRes int layout;
        if (getArguments() != null && getArguments().getString(BUNDLE_MESSAGE_ID) != null) {
            mIsNewChat = false;
            layout = R.layout.in_mail_detail_fragment_read_only;
        } else {
            layout = R.layout.in_mail_detail_fragment;
        }
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserNameEt = view.findViewById(R.id.userName);
        mContentEt = view.findViewById(R.id.content_et);
        mSubjectEt = view.findViewById(R.id.subject_et);

        if (getArguments() != null && getArguments().getString(BUNDLE_MESSAGE_ID) != null) {
            String inMailKey = getArguments().getString(BUNDLE_MESSAGE_ID);
            mUserImage = (ImageView) view.findViewById(R.id.userImage);
            mIsNewChat = false;
            loadChat(inMailKey);
        }

        if (mIsNewChat) {
            mSendButton = (Button) view.findViewById(R.id.send_btn);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(((EditText) mUserNameEt).getText().toString()) &&
                            !TextUtils.isEmpty(((EditText) mSubjectEt).getText().toString()) &&
                            !TextUtils.isEmpty(((EditText) mContentEt).getText().toString()))
                        addNewChat(((EditText) mUserNameEt).getText().toString(), ((EditText) mSubjectEt).getText().toString(), ((EditText) mContentEt).getText().toString());
                }
            });
            if(getArguments() != null && getArguments().getString(IN_MAIL_EMAIL_ADDRESS) != null) {
                ((EditText) mUserNameEt).setText(getArguments().getString(IN_MAIL_EMAIL_ADDRESS));
            }
        }
    }

    private void loadChat(String inMailKey) {
        if (inMailKey == null) {
            return;
        }

        mSelfRef.child(inMailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final InMailMessage message = dataSnapshot.getValue(InMailMessage.class);

                    DbUtils.executeById(getContext(), message.userId, new DbUtils.OnQueryDbListener() {
                        @Override
                        public void execute(User user) {
                            ((TextView) mUserNameEt).setText(user.first_name + " " + user.last_name);
                            ((TextView) mSubjectEt).setText(message.subject);
                            ((TextView) mContentEt).setText(message.content);
                            if (mUserImage != null) {
                                if (user.profilePhotoURL != null) {
                                    Picasso.with(mUserImage.getContext()).
                                            load(user.profilePhotoURL).into(mUserImage);
                                } else {
                                    String defaultURL = mUserImage.getContext().getResources().getString(R.string.default_profile_pic);
                                    Picasso.with(mUserImage.getContext()).load(defaultURL).into(mUserImage);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addNewChat(final String email, final String subject, final String content) {
        DbUtils.executeByEmail(getContext(), email, new DbUtils.OnQueryDbListener() {
            @Override
            public void execute(User user) {
                if (user == null) {
                    Toast.makeText(getContext(), "Not a valid email, please try again", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ts = System.currentTimeMillis() + "";
                String key = mSelfRef.push().getKey();
                mSelfRef.child(key).setValue(newInMailMessage(key, user.id, subject, content, ts, true));

                DatabaseReference otherRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(user.id).child(User.IN_MAIL);
                key = otherRef.push().getKey();
                otherRef.child(key).setValue(newInMailMessage(key, user.id, subject, content, ts, false));

                Map<String, String> data = new HashMap<>();
                data.put(IN_MAIL_SUBJECT, subject);
                data.put(IN_MAIL_MESSAGE, content);
                data.put(IN_MAIL_EMAIL_ADDRESS, user.email);
                data.put(PushMessageContent.ACTION_PUSH_MESSAGE, InMailActivity.IN_MAIL_ACTION);
                HTTPUtil.sendPushNotification(getContext(), Arrays.asList(user.token), getString(R.string.title_inmail), getString(R.string.message_inmail
                        , UserAuth.getInstance().getCurrentUser().first_name + " " + UserAuth.getInstance().getCurrentUser().last_name), data);
            }
        });
    }

    private InMailMessage newInMailMessage(String id, String userId, String subject, String content, String ts, boolean self) {
        InMailMessage message = new InMailMessage();
        message.id = id;
        message.userId = userId;
        message.subject = subject;
        message.content = content;
        message.lastTimeStamp = DateFormat.getDateTimeInstance().format(new Date());
        message.self = true;

        return message;
    }
}