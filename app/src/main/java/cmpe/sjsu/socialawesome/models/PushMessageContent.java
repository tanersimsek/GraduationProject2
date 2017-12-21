package cmpe.sjsu.socialawesome.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lam on 5/12/17.
 */

public class PushMessageContent {
    public static final String TITLE_PUSH_MESSAGE = "title";
    public static final String BODY_PUSH_MESSAGE = "body";
    public static final String ACTION_PUSH_MESSAGE = "action";
    public Map<String, String> data;

    public PushMessageContent(String title, String message, Map<String, String> data) {
        if (data == null) data = new HashMap<>();
        data.put(TITLE_PUSH_MESSAGE, title);
        data.put(BODY_PUSH_MESSAGE, message);
        this.data = data;
    }
}
