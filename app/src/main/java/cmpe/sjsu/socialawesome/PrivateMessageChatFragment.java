package cmpe.sjsu.socialawesome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.PrivateMessage;
import cmpe.sjsu.socialawesome.models.SingleMessage;
import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

/**
 * Created by lam on 4/28/17.
 */

public class PrivateMessageChatFragment extends SocialFragment {
    public static final String OTHER_USER_BUNDLE = "other_user_bundle";
    final DatabaseReference mSelfRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE)
            .child(UserAuth.getInstance().getCurrentUser().id).child(User.PRIVATE_MESSAGE);
    DatabaseReference mOtherRef;
    private boolean isMessageReady;
    private RecyclerView mListView;
    private EditText mEditText;
    private Button mSendButton;
    private List<SingleMessage> messages = new ArrayList<>();
    private MessageChatAdapter mAdapter;
    private UserIDMap mOtherUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.private_message_chat, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (RecyclerView) view.findViewById(R.id.message_list);
        mListView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(llm);
        mEditText = (EditText) view.findViewById(R.id.message_et);
        mSendButton = (Button) view.findViewById(R.id.send_btn);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSendButton.setEnabled(s.length() > 0 && isMessageReady);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditText.getText().toString();
                addNewChat(message);
                mEditText.setText("");
            }
        });

        if (getArguments().getSerializable(OTHER_USER_BUNDLE) instanceof UserIDMap) {
            mOtherUser = (UserIDMap) getArguments().getSerializable(OTHER_USER_BUNDLE);
            mOtherRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE)
                    .child(mOtherUser.id).child(User.PRIVATE_MESSAGE);
            mAdapter = new MessageChatAdapter(messages, mOtherUser);
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadChat();
    }

    private void loadChat() {
        mSelfRef.child(mOtherUser.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.child(PrivateMessage.MESSAGES).getChildren()) {
                    SingleMessage message = messageSnapshot.getValue(SingleMessage.class);
                    messages.add(message);
                }
                mAdapter.notifyDataSetChanged();
                mListView.scrollToPosition(messages.size() - 1);
                isMessageReady = true;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addNewChat(String message) {
        SingleMessage newMessage = new SingleMessage(message, true);
        mSelfRef.child(mOtherUser.id).child(PrivateMessage.MESSAGES).push().setValue(newMessage);
        if (mOtherRef != null)
            mOtherRef.child(UserAuth.getInstance().getCurrentUser().id).child(PrivateMessage.MESSAGES).push().setValue(new SingleMessage(message, false));
    }
}
