package cmpe.sjsu.socialawesome.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lam on 4/27/17.
 */
@IgnoreExtraProperties
public class Post implements Comparable<Post>{
    public User user;
    public String contentPost;
    public String contentPhotoURL;
    public long timestamp;

    public Post(User user, long timestamp, String contentPost, String contentPhotoURL) {
        this.user = user;
        this.contentPost = contentPost;
        this.contentPhotoURL = contentPhotoURL;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("contentPost", contentPost);
        result.put("contentPhotoURL", contentPhotoURL);
        result.put("timestamp", timestamp);

        return result;
    }

    public User getUser() {
        return user;
    }

    public String getAuthorName() {
        return user.first_name + " " + user.last_name;
    }

    public String getContentPost() {
        return contentPost;
    }

    public String getContentPhotoURL() {
        return contentPhotoURL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(@NonNull Post post) {
        return (int)(post.getTimestamp() - timestamp);
    }
}
