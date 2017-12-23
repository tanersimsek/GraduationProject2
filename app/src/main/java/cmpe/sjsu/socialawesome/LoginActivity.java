package cmpe.sjsu.socialawesome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import cmpe.sjsu.socialawesome.Utils.TokenBroadcastReceiver;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.PushMessageContent;
import cmpe.sjsu.socialawesome.models.User;

public class LoginActivity extends AppCompatActivity {

    public static final String USERS_TABLE = "users";
    private static final String TAG = LoginActivity.class.toString();
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private FirebaseAuth mAuth;
    //private Button mNextBtn;
    private Button mSubmitBtn;
    private Button mCreateBtn;
    /*private Button mCreateAccountTv;
    //private Button mVerifyAccount;
    private Button mResendVerification;
    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mUniqueId;
    private EditText verifyEt;*/
    private boolean mIsLogin = true;
    private String mCustomToken;
    private TokenBroadcastReceiver mTokenReceiver;
    private ProgressDialog mProgressDialog;

    private void launchMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        if (intent != null) {
            if (UserAuth.getInstance().getCurrentUser() != null && InMailActivity.IN_MAIL_ACTION.equals(intent.getStringExtra(PushMessageContent.ACTION_PUSH_MESSAGE))) {
                Intent intent1 = new Intent(this, InMailActivity.class);
                intent1.putExtra(InMailActivity.ACTION_EXTRA, InMailActivity.ACTION_LIST);
                startActivity(intent1);
                return;
            }
        }

        if (UserAuth.getInstance().getCurrentUser() != null) {
            launchMainActivity();
        }
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        //Buttons

        mSubmitBtn = (Button) findViewById(R.id.btnLogin);
        mCreateBtn = (Button) findViewById(R.id.btnSave);

        //mVerifyAccount = (Button) findViewById(R.id.verify_account);

        //Fields
        mEmailEt = (EditText) findViewById(R.id.etxtEmail);
        mPasswordEt = (EditText) findViewById(R.id.etxtPassword);




        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validate()) {

                    Toast.makeText(LoginActivity.this, "Gerekli alanları doldurun.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }else {

                       // mProgressDialog.show();
                    signIn(mEmailEt.getText().toString(), mPasswordEt.getText().toString());
                }
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent2 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent2);

            }
        });




    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                successLogin(mAuth.getCurrentUser());

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                //TODO: Display failure message
                                mProgressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    private boolean validate() {
        //validate that the field is completed
        boolean valid = true;

        String email = mEmailEt.getText().toString();
        if (TextUtils.isEmpty(email)) {

            mEmailEt.setError("Required.");
            valid = false;
        } else {
            mEmailEt.setError(null);
        }

        String password = mPasswordEt.getText().toString();
        if (TextUtils.isEmpty(password)) {

            mPasswordEt.setError("Required.");
            valid = false;
        } else {
            mPasswordEt.setError(null);
        }

        return valid;
    }

    private void successLogin(final FirebaseUser fbUser) {
        final String token = FirebaseInstanceId.getInstance().getToken();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
        if (fbUser.isEmailVerified()) {

            ref.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null && token != null && !token.equals(user.token)) {
                            user.token = token;
                            ref.child(fbUser.getUid()).child("token").setValue(token);
                        }
                        mProgressDialog.dismiss();
                        UserAuth.getInstance().setCurrentUser(user);
                        launchMainActivity();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            Toast.makeText(LoginActivity.this, "Account is not Verified",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
