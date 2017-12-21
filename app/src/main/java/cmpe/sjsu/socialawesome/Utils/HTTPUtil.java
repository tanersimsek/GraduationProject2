package cmpe.sjsu.socialawesome.Utils;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cmpe.sjsu.socialawesome.R;
import cmpe.sjsu.socialawesome.models.PushMessageResquest;

/**
 * Created by lam on 5/12/17.
 */

public class HTTPUtil {
    private static final String PUSH_NOTIFICATION_URL = "https://us-central1-social-awesome.cloudfunctions.net/sendPushNotificationToDevices";
    private static final String EMAIL_URL = "https://us-central1-social-awesome.cloudfunctions.net/sendEmailToUser";
    private static RequestQueue smRequestQueue;

    public static void sendPushNotification(Context context, List<String> tokens, String title, String message, Map<String, String> data) {
        sendPushNotification(context, new PushMessageResquest(tokens, title, message, data), true);
    }

    public static void sendEmail(Context context, List<String> tokens, String title, String message) {
        sendPushNotification(context, new PushMessageResquest(tokens, title, message, null), false);
    }

    static void sendPushNotification(final Context context, PushMessageResquest resquest, final boolean isNotification) {
        if (smRequestQueue == null) {
            smRequestQueue = Volley.newRequestQueue(context);
        }

        try {
            final JSONObject jsonBody = new JSONObject(new Gson().toJson(resquest));
            smRequestQueue.add(new JsonObjectRequest(isNotification ? PUSH_NOTIFICATION_URL : EMAIL_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, isNotification ? context.getString(R.string.success_send_push) : context.getString(R.string.success_send_email), Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, isNotification ? context.getString(R.string.failure_send_push) : context.getString(R.string.failure_send_email), Toast.LENGTH_SHORT).show();
                }
            }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
