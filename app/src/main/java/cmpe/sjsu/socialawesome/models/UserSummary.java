package cmpe.sjsu.socialawesome.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lam on 4/27/17.
 */
@IgnoreExtraProperties
public class UserSummary implements Serializable {
    public String id;
    public String email;
    public String first_name;
    public String last_name;
    public String profilePhotoURL;
    public int status; //this
    public String nick_name;
    public String location;
    public String profession;
    public String interest;
    public String about;

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("email", email);
        map.put("first_name", first_name);
        map.put("last_name", last_name);
        map.put("profilePhotoURL", profilePhotoURL);

        return map;
    }
}
