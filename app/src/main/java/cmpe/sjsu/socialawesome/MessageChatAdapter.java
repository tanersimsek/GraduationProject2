package cmpe.sjsu.socialawesome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cmpe.sjsu.socialawesome.Utils.DbUtils;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.SingleMessage;
import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;

/**
 * Created by lam on 5/19/17.
 */

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.ViewHolder> {
    private List<SingleMessage> mMessages;
    private UserIDMap mUser;
    private User mCurrentUser;

    public MessageChatAdapter(List<SingleMessage> messages, UserIDMap userIDMap) {
        mMessages = messages;
        mUser = userIDMap;
        mCurrentUser = UserAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 1 ? R.layout.private_chat_message : R.layout.private_chat_message_self, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SingleMessage message = mMessages.get(position);
        DbUtils.executeById(holder.mTextContent.getContext(), mUser.id, new DbUtils.OnQueryDbListener() {
            @Override
            public void execute(User user) {
                holder.mTextContent.setText(message.message);
                holder.mUserName.setText(message.isSelf ? mCurrentUser.email : user.email);

                String photoURL = message.isSelf ? UserAuth.getInstance().getCurrentUser().profilePhotoURL : user.profilePhotoURL;
                if (photoURL != null) {
                    Picasso.with(holder.mUserImage.getContext()).
                            load(photoURL).into(holder.mUserImage);
                } else {
                    String defaultURL = holder.mUserImage.getContext().getResources().getString(R.string.default_profile_pic);
                    Picasso.with(holder.mUserImage.getContext()).load(defaultURL).into(holder.mUserImage);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).isSelf ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserImage;
        public TextView mUserName;
        public TextView mTextContent;

        public ViewHolder(View view) {
            super(view);
            mUserImage = (ImageView) view.findViewById(R.id.userImage);
            mUserName = (TextView) view.findViewById(R.id.userName);
            mTextContent = (TextView) view.findViewById(R.id.message_content);

            mUserImage.setImageBitmap(null);
        }
    }
}
