package cmpe.sjsu.socialawesome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.UUID;

import cmpe.sjsu.socialawesome.models.Group;

public class AddGroupActivity extends AppCompatActivity {

    private EditText grupadi;

    int controlgrup=0;
    public static final String Groups_TABLE = "Groups";
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
grupadi=(EditText)findViewById(R.id.edittextgroupname);
        button=(Button)findViewById(R.id.buttoncreategroup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });

    }
    public void createGroup()
    {
        //   linearLayout1.setVisibility(View.VISIBLE);

controlgrup=0;
        final String token = FirebaseInstanceId.getInstance().getToken();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Groups_TABLE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                UUID uuid = UUID.randomUUID();
                String uuidString = uuid.toString();
                String control= grupadi.getText().toString();

            //    if (dataSnapshot.getValue() != null) {

                    Group group = dataSnapshot.getValue(Group.class);
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String s = hashMap.get("group_name").toString();

                    if (s.equals(control)) {
                        Toast.makeText(AddGroupActivity.this, "Hacı bunu almışlar başka dene istersen",
                                Toast.LENGTH_SHORT).show();
                        controlgrup = 1;
                        // group.token = token;
                        // ref.child(uuidString).child("token").setValue(token);
                    }
                }if(controlgrup==0) {
                    final Group group2 = new Group();

                    group2.group_name = grupadi.getText().toString();

                    group2.status = 1;
                    group2.notification = true;
                    group2.token = token;
                    group2.pushNotification = true;
                    ref.child(uuidString).setValue(group2);
                    //  mProgressDialog.dismiss();
                    // UserAuth.getInstance().setCurrentUser(user);
                    //  nicknameUI();

              /*      mNextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            user.unique_id = mUniqueId.getText().toString();

                            if (TextUtils.isEmpty(user.unique_id)) {
                                mUniqueId.setError("Required.");
                                return;
                            } else {
                                ref.orderByChild("unique_id").equalTo(user.unique_id).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            Toast.makeText(StartActivity.this, "Username Is Already Taken",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            ref.child(fbUser.getUid()).setValue(user);
                                            UserAuth.getInstance().setCurrentUser(user);
                                            launchMainActivity();
                                            Log.d(TAG, "Successfully create user in db");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }


                        }
                    });*/
                    Toast.makeText(AddGroupActivity.this, "Aferin bi grubun var",
                            Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
