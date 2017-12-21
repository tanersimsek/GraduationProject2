package cmpe.sjsu.socialawesome.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by lam on 4/27/17.
 */
@IgnoreExtraProperties
public class PrivateMessage implements Serializable{
    public static final String MESSAGES = "messages";
    public String id;
    public String lastTimeStamp;
    public String title;
    public Map<String, SingleMessage> messages;
}
