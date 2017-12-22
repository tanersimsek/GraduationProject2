package cmpe.sjsu.socialawesome.models;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by taner on 12/21/17.
 */

public class Group  implements Serializable {

    public String group_name;
    public static final String stringAll = "All";
    public static final String stringMine = "Mine";

    public int status;
    public boolean notification;

    public boolean pushNotification;


    public String token;
    public Map<String, UserIDMap> members;

}
