package cmpe.sjsu.socialawesome.services;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import cmpe.sjsu.socialawesome.StartActivity;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.User;

/**
 * Created by lam on 5/11/17.
 */

public class SocialInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = SocialInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        final String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "new Token is: " + token);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(StartActivity.USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;
                if (dataSnapshot.child(User.TOKEN) == null || !dataSnapshot.child(User.TOKEN).equals(token)) {
                    UserAuth.getInstance().getCurrentUser().token = token;
                    Map<String, Object> map = new HashMap<>();
                    map.put(User.TOKEN, FirebaseInstanceId.getInstance().getToken());
                    ref.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
