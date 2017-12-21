package cmpe.sjsu.socialawesome.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by lam on 4/27/17.
 */
@IgnoreExtraProperties
public class User implements Serializable {
    public static final String TOKEN = "token";
    public static final String PRIVATE_MESSAGE = "privateMessage";
    public static final String IN_MAIL = "inMail";
    public static final String FRIEND_LIST = "friends";
    public static final String WAITING_FRIEND_LIST = "waitingFriends";
    public static final String FOLOWING_FRIEND_LIST = "followingFriends";
    public static final String FOLLOWER_LIST = "follower";
    public static final String PENDING_FRIEND_LIST = "pendingFriends";
    public String id;
    public String email;
    public String first_name;
    public String last_name;
    public String nickname;
    public String profilePhotoURL;
    public String location;
    public String profession;
    public String about_me;
    public String interest;
    public String token;
    public String unique_id;

    // 0 -- disable
    // 1 -- friends only view
    // 2 -- public
    public int status;

    public boolean notification;

    public boolean pushNotification;

    // List of friend already establish
    public Map<String, UserIDMap> friends;

    // List of friend this user sent invitation to
    public Map<String, UserIDMap> waitingFriends;

    // List of friend this user sent invitation to
    public Map<String, UserIDMap> followingFriends;

    // List of person who follow me
    public Map<String, UserIDMap> follower;

    // List of friends that sent invitation to this user, pending friend request
    public Map<String, UserIDMap> pendingFriends;

    public Map<String, PrivateMessage> privateMessage;
    public Map<String, InMailMessage> inMail;
}
