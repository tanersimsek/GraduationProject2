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
import cmpe.sjsu.socialawesome.Utils.DbUtilsGroups;
import cmpe.sjsu.socialawesome.Utils.FriendUtils;
import cmpe.sjsu.socialawesome.Utils.GroupUtils;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.Group;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;
import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.Group.memberslist;

/**
 * Created by taner on 12/23/17.
 */

public class GroupListAdapter  extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {
    private List<String> entryList;
    private int mType = 0;
    private OnInfoUpdateListener mListener;
    public static final String Groups_TABLE = "Groups";
    interface OnInfoUpdateListener {
        void onInfoUpdate();
    }
    public GroupListAdapter(List<String> entryList) {
        this.entryList = entryList;
  //      mListener = listener;
        int c=5;
//        Collections.sort(this.entryList, new DateComparator());
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardgroup, parent, false);
        return new GroupViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final GroupViewHolder holder, int position) {
        String entry = entryList.get(position);
/*Email e ihtiyaç duyulmadığı için kapattım */
      /*  if (entry.contains("@") && mType == 1) {
            holder.vEmail.setText("Email:            " + entry);

        } else {*/
            DbUtilsGroups.executeById(holder.vName.getContext(), entry, new DbUtilsGroups.OnQueryDbListener() {
                @Override
                public void execute(final Group group) {
                    holder.vName.setText(group.group_name);
                    holder.addSelFriendBtn.setText("Join");
                    holder.addSelFriendBtn.setVisibility(View.VISIBLE);
               //     holder.vEmail.setText("Email:            " + user.email);
              //      holder.vLocation.setText("Location:      " + user.location);

               /*     switch (mType) {
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
*/
                    if (mType == 3) {
                        FirebaseDatabase.getInstance().getReference().child(Groups_TABLE)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(group.id).exists()) {
                             /*       holder.addSelFriendBtn.setVisibility(View.GONE);
                                    holder.acceptReqBtn.setText("Friends");
                                    holder.acceptReqBtn.setClickable(false);*/
                                    holder.addSelFriendBtn.setText("Join");
                             //       holder.addSelFriendBtn.setClickable(false);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                      /*  FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
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
                        });*/
                     /*   FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
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
                        });*/
                    }

                /*    holder.acceptReqBtn.setOnClickListener(new View.OnClickListener() {
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
                    });*/

                    holder.addSelFriendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GroupUtils.joingroup(holder.vName.getContext(), 0, group.id);
                            holder.addSelFriendBtn.setText("Katıldınız");
                            holder.addSelFriendBtn.setClickable(false);
                        }
                    });

                /*    holder.mRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mListener != null) mListener.onInfoUpdate();
                        }
                    });

                    */
//                    if(user.profilePhotoURL != null){
//                        Picasso.with(holder.vEmail.getContext()).load(user.profilePhotoURL).into((holder.profilePicImageView));
//                    }



             /* Fotoya ihtiyaç olmadığı için aşağısı kapandı*/
                /*    if (user.profilePhotoURL != null) {
                        Picasso.with(holder.vEmail.getContext()).load(user.profilePhotoURL).into(holder.profilePicImageView);
                    } else {
                        String defaultURL = holder.vEmail.getContext().getResources().getString(R.string.default_profile_pic);
                        Picasso.with(holder.vEmail.getContext()).load(defaultURL).into(holder.profilePicImageView);
                    }*/





                }
            });
        //}
    }






    @Override
    public int getItemCount() {
        return entryList.size();
    }
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vEmail;
        protected TextView vLocation;
        protected Button acceptReqBtn;
        protected Button addSelFriendBtn;
        protected View mRootView;
        protected ImageView profilePicImageView;

        public GroupViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.card_view_GroupName);
         //   vEmail = (TextView) v.findViewById(R.id.profileEmail);
          //  vLocation = (TextView) v.findViewById(R.id.profileLocation);
        //    acceptReqBtn = (Button) v.findViewById(R.id.accept_request_button);
            addSelFriendBtn = (Button) v.findViewById(R.id.join_select_group_button);
           // profilePicImageView = (ImageView) v.findViewById(R.id.profileImage);
            mRootView = v;
        }
    }

    public void updateType(int type) {
        mType = type;
    }


}
