package cmpe.sjsu.socialawesome.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bing on 5/23/17.
 */

@IgnoreExtraProperties
public class UserIDMap implements Serializable {
    public String id;

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        return map;
    }
}
