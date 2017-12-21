package cmpe.sjsu.socialawesome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import cmpe.sjsu.socialawesome.Utils.DbUtils;
import cmpe.sjsu.socialawesome.Utils.FriendUtils;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;
import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;

/**
 * Created by bing on 5/13/17.
 */


public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {
    private List<String> entryList;
    private int mType = 0;
    private OnInfoUpdateListener mListener;

    interface OnInfoUpdateListener {
        void onInfoUpdate(boolean bool, String st);
    }
    public FriendListAdapter(List<String> entryList, OnInfoUpdateListener listener) {
        this.entryList = entryList;
        mListener = listener;
//        Collections.sort(this.entryList, new DateComparator());
    }


    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder holder, int position) {
        String entry = entryList.get(position);

        if (entry.contains("@") && mType == 1) {
            holder.vEmail.setText("Email:            " + entry);

        } else {
            DbUtils.executeById(holder.vEmail.getContext(), entry, new DbUtils.OnQueryDbListener() {
                @Override
                public void execute(final User user) {
                    holder.vName.setText(user.first_name + " " + user.last_name);
                    holder.vEmail.setText("Email:            " + user.email);
                    holder.vLocation.setText("Location:      " + user.location);

                    switch (mType) {
                        case 2:
                            holder.acceptReqBtn.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            holder.acceptReqBtn.setVisibility(View.VISIBLE);
                            holder.acceptReqBtn.setText("Follow");
                            holder.addSelFriendBtn.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            holder.acceptReqBtn.setVisibility(View.VISIBLE);
                            holder.acceptReqBtn.setText("Un Follow");
                        default:
                    }

                    if (mType == 3) {
                        FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                                .child(FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(user.id).exists()) {
                                    holder.addSelFriendBtn.setVisibility(View.GONE);
                                    holder.acceptReqBtn.setText("Friends");
                                    holder.acceptReqBtn.setClickable(false);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                                .child(WAITING_FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(user.id).exists()) {
                                    holder.addSelFriendBtn.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                                .child(FOLOWING_FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(user.id).exists()) {
                                    holder.acceptReqBtn.setText("Un-follow");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                    holder.acceptReqBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (mType) {
                                case 2:
                                    FriendUtils.unFollowFriend(holder.vEmail.getContext(), 0, user.id);
                                    holder.acceptReqBtn.setText("Accepted");
                                    holder.acceptReqBtn.setClickable(false);
                                    break;
                                case 3:
                                    FriendUtils.addFriend(holder.vEmail.getContext(), 1, user.id);
                                    holder.acceptReqBtn.setText("Following");
                                    holder.acceptReqBtn.setClickable(false);
                                    break;
                                case 4:
                                    FriendUtils.unFollowFriend(holder.vEmail.getContext(), 1, user.id);
                                    holder.acceptReqBtn.setText("Un-Followed");
                                    holder.acceptReqBtn.setClickable(false);
                                default:
                            }
                        }
                    });

                    holder.addSelFriendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FriendUtils.addFriend(holder.vEmail.getContext(), 0, user.id);
                            holder.addSelFriendBtn.setText("Adding");
                            holder.addSelFriendBtn.setClickable(false);
                        }
                    });

                    holder.mRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mListener != null) mListener.onInfoUpdate(true, user.id);
                        }
                    });
//                    if(user.profilePhotoURL != null){
//                        Picasso.with(holder.vEmail.getContext()).load(user.profilePhotoURL).into((holder.profilePicImageView));
//                    }
                    if (user.profilePhotoURL != null) {
                        Picasso.with(holder.vEmail.getContext()).load(user.profilePhotoURL).into(holder.profilePicImageView);
                    } else {
                        String defaultURL = holder.vEmail.getContext().getResources().getString(R.string.default_profile_pic);
                        Picasso.with(holder.vEmail.getContext()).load(defaultURL).into(holder.profilePicImageView);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vEmail;
        protected TextView vLocation;
        protected Button acceptReqBtn;
        protected Button addSelFriendBtn;
        protected View mRootView;
        protected ImageView profilePicImageView;

        public FriendViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.profileName);
            vEmail = (TextView) v.findViewById(R.id.profileEmail);
            vLocation = (TextView) v.findViewById(R.id.profileLocation);
            acceptReqBtn = (Button) v.findViewById(R.id.accept_request_button);
            addSelFriendBtn = (Button) v.findViewById(R.id.add_select_friend_button);
            profilePicImageView = (ImageView) v.findViewById(R.id.profileImage);
            mRootView = v;
        }
    }

    public void updateType(int type) {
        mType = type;
    }

}


