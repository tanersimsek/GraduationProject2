package cmpe.sjsu.socialawesome.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by lam on 4/27/17.
 */
@IgnoreExtraProperties
public class InMailMessage implements Serializable {
    public static final String CONTENT = "content";
    public static final String SUBJECT = "subject";
    public String userId;
    public String lastTimeStamp;
    public String content;
    public String subject;
    public String id;
    public boolean self;
}
