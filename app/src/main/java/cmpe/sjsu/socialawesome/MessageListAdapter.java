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
import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;

/**
 * Created by lam on 5/19/17.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<UserIDMap> mUsers;
    private OnMessageChatClickListener mListener;

    public MessageListAdapter(List<UserIDMap> summaries, OnMessageChatClickListener listener) {
        mUsers = summaries;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.private_list_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserIDMap user = mUsers.get(position);
        DbUtils.executeById(holder.mRootView.getContext(), user.id, new DbUtils.OnQueryDbListener() {
            @Override
            public void execute(final User user) {
                if (user == null) return;
                if (user.profilePhotoURL != null) {
                    Picasso.with(holder.mUserImage.getContext()).
                            load(user.profilePhotoURL).into(holder.mUserImage);
                } else {
                    String defaultURL = holder.mUserImage.getContext().getResources().getString(R.string.default_profile_pic);
                    Picasso.with(holder.mUserImage.getContext()).load(defaultURL).into(holder.mUserImage);
                }
                holder.mUserName.setText(user.email);
                holder.mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onClicked(user);
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    interface OnMessageChatClickListener {
        void onClicked(User user);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserImage;
        public TextView mUserName;
        public View mRootView;

        public ViewHolder(View view) {
            super(view);
            mRootView = view;
            mUserImage = (ImageView) view.findViewById(R.id.userImage);
            mUserName = (TextView) view.findViewById(R.id.userName);
        }
    }
}
