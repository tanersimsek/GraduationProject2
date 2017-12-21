package cmpe.sjsu.socialawesome.push_notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import cmpe.sjsu.socialawesome.Utils.UserAuth;

/**
 * Created by lam on 5/11/17.
 */

public class SocialInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = SocialInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "new Token is: " + token);

        UserAuth.getInstance().getCurrentUser().token = token;
    }
}
