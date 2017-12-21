package cmpe.sjsu.socialawesome.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;
import cmpe.sjsu.socialawesome.models.UserSummary;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;
import static cmpe.sjsu.socialawesome.models.User.FOLLOWER_LIST;
import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.PENDING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;


/**
 * Created by bing on 5/11/17.
 */

public class FriendUtils {
    private static UserSummary mSummary = new UserSummary();

    //add friend by email
//        FriendUtils.addFriendByEmail(getActivity(),"lam.tran@sjsu.edu");
//        FriendUtils.addFriendByEmail(getActivity(),"sheilashi0112@gmail.com");
    public static void addFriendByEmail(final Context context, final String email) {

        final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
        Query query = userTableRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    new Thread(new Runnable() {
                        public void run() {
                            emailFriendRequest(context, email, UserAuth.getInstance().getCurrentUser());
                        }
                    }).start();
                    Toast.makeText(context, "Succes: friend request sent to " + email, Toast.LENGTH_SHORT).show();
                    //TODO: add email to waiting friend list
                    UserIDMap emailId = new UserIDMap();
                    emailId.id = email;
                    userTableRef.child(UserAuth.getInstance().getCurrentUser().id).child(WAITING_FRIEND_LIST).push().setValue(emailId);
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
//                        getUserSummary(user);
                        addFriend(context, 0, user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void emailFriendRequest(final Context context, final String email, final User user) {
        Mail m = new Mail("bingtest0112@gmail.com", "01120112");
        String[] toArr = {email};
        m.setTo(toArr);
        m.setFrom("bingtest0112@gmail.com");
        m.setSubject("Invitation from " + user.first_name + " " + user.last_name);
        m.setBody("Your friend " + user.first_name + " " + user.last_name + " is inviting you to join our app, SocialAwesome!");
        try {
            if (m.send()) {
//                Toast.makeText(context, "Email was sent successfully.", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(context, "Email was not sent.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
//            Toast.makeText(context, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("MailApp", "Could not send email", e);
        }
    }

    //type: 0-friend, 1-follow
    public static void addFriend(final Context context, final int type, final String id) {
        final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
        Query query = userTableRef.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(context, "Error: Id did not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        addFriend(context, type, user);
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
    public static void addFriend(final Context context, int type, final User user) {
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
        if (user.id.equals(UserAuth.getInstance().getCurrentUser().id)) {
            Toast.makeText(context, "Error: You can't follow or be friend with yourself!", Toast.LENGTH_SHORT).show();
        } else {
            if (user.status == 1) {
                final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
                final DatabaseReference currentUserRef = userTableRef.child(UserAuth.getInstance().getCurrentUser().id);
                final DatabaseReference currentUserFollowRef = currentUserRef.child(nodeSent);
                final DatabaseReference followerRef = userTableRef.child(user.id).child(nodeReceive);
                final String receiveName = user.first_name + user.last_name;
                final String dialogDuFinal = dialogDuplicate;
                final String dialogSuFinal = dialogSuccess;

                if (type == 1) {
                    currentUserRef.child(FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(user.id).exists()) {
                                Toast.makeText(context, "Error: " + "You are already friend with " + receiveName + ", no need to follow" + "!", Toast.LENGTH_SHORT).show();
                            } else {
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
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
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
                }
            } else {
                Toast.makeText(context, "Error: " + dialogPrivate + "!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //accept friend request
//    FriendUtils.unFollowFriend(getActivity(), 0, "NFJKWuqd15MaVWYZYsm0lD9ve5J3");
    //type: 0-friend, 1-follow
    public static void unFollowFriend(final Context context, final int type, final String id) {
        final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
        Query query = userTableRef.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(context, "Error: Id did not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        unFollowFriend(context, type, user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //type: 0-friend, 1-follow
    public static void unFollowFriend(final Context context, int type, final User user) {
        String nodeSent = null;
        String nodeReceive = null;
        String dialogSuccess = null;
        String dialogError = null;
        switch (type) {
            case 0:
                nodeSent = PENDING_FRIEND_LIST;
                nodeReceive = WAITING_FRIEND_LIST;
                dialogError = "You never received friend request from ";
                dialogSuccess = "You are now friend with ";
                break;
            case 1:
                nodeSent = FOLOWING_FRIEND_LIST;
                nodeReceive = FOLLOWER_LIST;
                dialogError = "You never followed ";
                dialogSuccess = "You are now unfollowing ";
                break;
            default:
        }

        final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
        final DatabaseReference currentUserRef = userTableRef.child(UserAuth.getInstance().getCurrentUser().id);
        //my following, my pending
        final DatabaseReference currentUserFollowRef = currentUserRef.child(nodeSent);
        //other follower, other waiting
        final DatabaseReference followerRef = userTableRef.child(user.id).child(nodeReceive);
        final String receiveName = user.first_name + user.last_name;
        final String dialogSuFinal = dialogSuccess;
        final String dialogErrorFinal = dialogError;
        final int functionType = type;

        currentUserFollowRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.id).exists()) {
                    currentUserFollowRef.child(user.id).removeValue();
                    followerRef.child(UserAuth.getInstance().getCurrentUser().id).removeValue();
                    if (functionType == 0) {
                        UserIDMap idMap = new UserIDMap();
                        idMap.id = user.id;
                        currentUserRef.child(FRIEND_LIST).child(user.id).setValue(idMap);
                        userTableRef.child(user.id).child(FRIEND_LIST).child(UserAuth.getInstance().getCurrentUser().id).setValue(UserAuth.getInstance().getCurrentUserIdMap());
                    }
                    Toast.makeText(context, "Success: " + dialogSuFinal + receiveName + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error: " + dialogErrorFinal + receiveName + "!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
