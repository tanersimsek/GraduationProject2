package cmpe.sjsu.socialawesome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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

import cmpe.sjsu.socialawesome.Utils.TokenBroadcastReceiver;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.PushMessageContent;

public class RegisterActivity extends AppCompatActivity {

    public static final String USERS_TABLE = "users";
    private static final String TAG = StartActivity.class.toString();
    private EditText mEmailEt;
    private EditText mPasswordEt;
    private FirebaseAuth mAuth;
    private Button mNextBtn;
    private Button mSubmitBtn;
    private Button mCreateBtn;
    private Button mbacktologinBtn;
    private Button mCreateAccountTv;
    //private Button mVerifyAccount;
    private Button mResendVerification;
    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mUniqueId;
    private EditText verifyEt;
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
        setContentView(R.layout.activity_register);

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
        mCreateBtn = (Button) findViewById(R.id.btnSave);
        mbacktologinBtn = (Button) findViewById(R.id.backtologin);
        //mSubmitBtn = (Button) findViewById(R.id.submit);
       // mCreateAccountTv = (Button) findViewById(R.id.create_account);
        //mVerifyAccount = (Button) findViewById(R.id.verify_account);
        mResendVerification = (Button) findViewById(R.id.resend_verification);
        //Fields
        mEmailEt = (EditText) findViewById(R.id.email);
        mPasswordEt = (EditText) findViewById(R.id.password);
        mFirstNameEt = (EditText) findViewById(R.id.first_name);
        mLastNameEt = (EditText) findViewById(R.id.last_name);
        mUniqueId = (EditText) findViewById(R.id.uniqueSV);
        mUniqueId.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        verifyEt = (EditText) findViewById(R.id.verifySV);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {

                    Toast.makeText(RegisterActivity.this, "Gerekli alanları doldurun.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    mProgressDialog.show();
                    createAccount(mEmailEt.getText().toString(), mPasswordEt.getText().toString());
                    /*Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent1);*/
                }

            }
        });

        mbacktologinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent1);

            }
        });







    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validate()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //successLogin(mAuth.getCurrentUser());
                            sendEmailVerification();


                         /*   mVerifyAccount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendEmailVerification();
                                }

                            });*/

                            mResendVerification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendEmailVerification();
                                }
                            });

                            mCreateBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mProgressDialog.show();

                                        createAccount(mEmailEt.getText().toString(), mPasswordEt.getText().toString());

                                }
                            });




                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            //TODO: show error message onscreen instead of toast
                            Toast.makeText(RegisterActivity.this, "You already have an account",
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

    private void sendEmailVerification() {
        // Disable button
//        findViewById(R.id.verify_account).setEnabled(false);

// Send verification email
// [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
//                        findViewById(R.id.verify_account).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }
}
