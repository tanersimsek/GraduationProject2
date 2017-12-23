package cmpe.sjsu.socialawesome.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cmpe.sjsu.socialawesome.models.Group;



import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

/**
 * Created by taner on 12/23/17.
 */

public class DbUtilsGroups {
    public static final String TAG = DbUtilsGroups.class.getSimpleName();
    public static final String Groups_TABLE = "Groups";
    public static void executeById(final Context context, final String id, @NonNull final OnQueryDbListener listener) {
        final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(Groups_TABLE);
        Query query = userTableRef.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = null;

                if (dataSnapshot.getValue() == null) {
                    Log.e(TAG, "Fail to get user from id " + id);
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        group = postSnapshot.getValue(Group.class);
                    }
                }
                listener.execute(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

 /*   public static void executeByEmail(final Context context, final String email, @NonNull final OnQueryDbListener listener) {
        final DatabaseReference userTableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
        Query query = userTableRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = null;

                if (dataSnapshot.getValue() == null) {
                    Log.e(TAG, "Fail to get user from email " + email);
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        user = postSnapshot.getValue(User.class);
                    }
                }
                listener.execute(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
*/
    public interface OnQueryDbListener {
        void execute(Group group);
    }
}
