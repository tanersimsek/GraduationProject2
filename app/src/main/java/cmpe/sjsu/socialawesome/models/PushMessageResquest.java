package cmpe.sjsu.socialawesome.models;

import java.util.List;
import java.util.Map;

/**
 * Created by lam on 5/11/17.
 */

public class PushMessageResquest {
    public List<String> tokens;
    public PushMessageContent payload;

    public PushMessageResquest(List<String> tokens, String title, String message, Map<String, String> data) {

        this.tokens = tokens;
        payload = new PushMessageContent(title, message, data);
    }
}
