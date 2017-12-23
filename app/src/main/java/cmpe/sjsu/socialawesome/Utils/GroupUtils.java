package cmpe.sjsu.socialawesome.Utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cmpe.sjsu.socialawesome.models.Group;
import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;
import static cmpe.sjsu.socialawesome.models.User.FOLLOWER_LIST;
import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.PENDING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.Group.memberslist;

/**
 * Created by taner on 12/23/17.
 */

public class GroupUtils {
    public static final String Groups_TABLE = "Groups";
    public static void joingroup(final Context context, final int type, final String id) {
        final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(Groups_TABLE);
        Query query = userTableRef.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(context, "Error: Id did not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Group group = postSnapshot.getValue(Group.class);
                        joingroup(context, type, group);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    //add friend and follow on profile
//        UserSummary mySummary = UserAuth.getInstance().getCurrentUserSummary();
//        FriendUtils.addFriend(getActivity(), 1, mySummary);
    //type: 0-friend, 1-follow
    public static void joingroup(final Context context, int type, final Group group) {
        String nodeSent = null;
        String nodeReceive = null;
        String dialogSuccess = null;
        String dialogDuplicate = null;
        String dialogPrivate = null;
        switch (type) {
            case 0:
                nodeSent = WAITING_FRIEND_LIST;
                nodeReceive = PENDING_FRIEND_LIST;
                dialogDuplicate = "You already sent friend request to ";
                dialogSuccess = "Friend request sent to ";
                dialogPrivate = "You can't add user whose profile is not public as friend!";
                break;
            case 1:
                nodeSent = FOLOWING_FRIEND_LIST;
                nodeReceive = FOLLOWER_LIST;
                dialogDuplicate = "You already followed ";
                dialogSuccess = "You are now following ";
                dialogPrivate = "You can't follow user whose profile is not public!";

                //TODO: you are already friend with
                break;
            default:
        }
    /*    if (user.id.equals(UserAuth.getInstance().getCurrentUser().id)) {
            Toast.makeText(context, "Error: You can't follow or be friend with yourself!", Toast.LENGTH_SHORT).show();
        } else {*/
            //if (user.status == 1) {
                final DatabaseReference groupTableRef = FirebaseDatabase.getInstance().getReference().child(Groups_TABLE);
                final DatabaseReference memberlistesiref = groupTableRef.child(group.id).child(Group.memberslist);
            //    final DatabaseReference currentUserFollowRef = currentUserRef.child(nodeSent);
               // final DatabaseReference followerRef = userTableRef.child(user.id).child(nodeReceive);
                final String receiveName = group.group_name;
                final String dialogDuFinal = dialogDuplicate;
                final String dialogSuFinal = dialogSuccess;


                   memberlistesiref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(UserAuth.getInstance().getCurrentUser().id).exists()) {
                                Toast.makeText(context, "Error: " + "Zaten " + receiveName + " grubunun üyesiniz" + "!", Toast.LENGTH_SHORT).show();
                            } else {
                                memberlistesiref.child(UserAuth.getInstance().getCurrentUser().id).child("Uye Durumu").setValue("Üye");
                                Toast.makeText(context, "Artık "+receiveName+" Grubunun üyesiniz", Toast.LENGTH_SHORT).show();

                              /*  memberlistesiref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(user.id).exists()) {
                                            Toast.makeText(context, "Error: " + dialogDuFinal + receiveName + "!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            UserIDMap idMap = new UserIDMap();
                                            idMap.id = user.id;
                                            currentUserFollowRef.child(user.id).setValue(idMap);
                                            followerRef.child(UserAuth.getInstance().getCurrentUser().id).setValue(UserAuth.getCurrentUserIdMap());
                                            Toast.makeText(context, "Success: " + dialogSuFinal + receiveName + "!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });*/
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                /* else {
                    currentUserFollowRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(user.id).exists()) {
                                Toast.makeText(context, "Error: " + dialogDuFinal + receiveName + "!", Toast.LENGTH_SHORT).show();
                            } else {
                                UserIDMap idMap = new UserIDMap();
                                idMap.id = user.id;
                                currentUserFollowRef.child(user.id).setValue(idMap);
                                followerRef.child(UserAuth.getInstance().getCurrentUser().id).setValue(UserAuth.getCurrentUserIdMap());
                                Toast.makeText(context, "Success: " + dialogSuFinal + receiveName + "!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }*/
                /*
            } else {
                Toast.makeText(context, "Error: " + dialogPrivate + "!", Toast.LENGTH_SHORT).show();*/
            }
     //   }
    }

