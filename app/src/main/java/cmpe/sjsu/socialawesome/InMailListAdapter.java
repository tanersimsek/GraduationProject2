package cmpe.sjsu.socialawesome;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import cmpe.sjsu.socialawesome.Utils.DbUtils;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.InMailMessage;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

/**
 * Created by lam on 5/19/17.
 */

public class InMailListAdapter extends RecyclerView.Adapter<InMailListAdapter.ViewHolder> {
    final DatabaseReference mSelfRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE)
            .child(UserAuth.getInstance().getCurrentUser().id).child(User.IN_MAIL);

    private List<InMailMessage> mailMessages;
    private OnInMailMessageChangeListener mChangeListener;
    private OnInMailMessageClickListener mListener;

    public InMailListAdapter(List<InMailMessage> messages, OnInMailMessageClickListener listener, OnInMailMessageChangeListener changeListener) {
        mailMessages = messages;
        mListener = listener;
        mChangeListener = changeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.private_list_in_mail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final InMailMessage message = mailMessages.get(position);
        DbUtils.executeById(holder.mRootView.getContext(), message.userId, new DbUtils.OnQueryDbListener() {
            @Override
            public void execute(User user) {
                holder.mUserNameEt.setText(user.email);
                holder.mSubjectEt.setText(message.subject);
                holder.mTimestampEt.setText(message.lastTimeStamp);
                holder.mSendRcv.setText(message.self ? holder.mRootView.getContext().getString(R.string.sent) : holder.mRootView.getContext().getString(R.string.received));

                if (user.profilePhotoURL != null) {
                    Picasso.with(holder.mUserImage.getContext()).load(user.profilePhotoURL).into(holder.mUserImage);
                } else {
                    String defaultURL = holder.mUserImage.getContext().getResources().getString(R.string.default_profile_pic);
                    Picasso.with(holder.mUserImage.getContext()).load(defaultURL).into(holder.mUserImage);
                }
            }
        });

        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClicked(message.id);
                }
            }
        });

        holder.mRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(holder.mRootView.getContext())
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_in_mail_message)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelfRef.child(message.id).removeValue();
                                if (mChangeListener != null) {
                                    mChangeListener.onChanged();
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mailMessages.size();
    }

    interface OnInMailMessageClickListener {
        void onClicked(String messageId);
    }

    interface OnInMailMessageChangeListener {
        void onChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserImage;
        public TextView mUserNameEt;
        public TextView mSubjectEt;
        public TextView mTimestampEt;
        public TextView mSendRcv;
        public View mRootView;

        public ViewHolder(View view) {
            super(view);
            mRootView = view;
            mUserImage = (ImageView) view.findViewById(R.id.userImage);
            mUserNameEt = (TextView) view.findViewById(R.id.userName_et);
            mSubjectEt = (TextView) view.findViewById(R.id.subject_et);
            mTimestampEt = (TextView) view.findViewById(R.id.timestamp_et);
            mSendRcv = (TextView) view.findViewById(R.id.sent_rcv);
        }
    }
}
