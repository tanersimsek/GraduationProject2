package cmpe.sjsu.socialawesome;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import static cmpe.sjsu.socialawesome.InMailDetailFragment.IN_MAIL_EMAIL_ADDRESS;

public class InMailActivity extends AppCompatActivity {
    public static final String ACTION_LIST = "open_list_message";
    public static final String ACTION_DETAIL = "open_new_message";
    public static final String ACTION_EXTRA = "open_new_message";
    public static final String BUNDLE_MESSAGE_ID = "bundle_message_id";
    public static final String IN_MAIL_ACTION = "action_in_mail";

    private SocialFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra(ACTION_EXTRA))) {
//            UserSummary otherUser = (UserSummary) getIntent().getSerializableExtra(BUNDLE_OTHER_USER);
            switch (getIntent().getStringExtra(ACTION_EXTRA)) {
                case ACTION_LIST:
                    mFragment = new InMailListFragment();
                    break;
                case ACTION_DETAIL:
                    mFragment = new InMailDetailFragment();
                    break;
                default:
                    break;
            }

            if (getIntent().getStringExtra(BUNDLE_MESSAGE_ID) != null) {
                Bundle bundle = new Bundle(1);
                bundle.putString(BUNDLE_MESSAGE_ID, getIntent().getStringExtra(BUNDLE_MESSAGE_ID));
                mFragment.setArguments(bundle);
            }

            if (getIntent().getStringExtra(IN_MAIL_EMAIL_ADDRESS) != null) {
                Bundle bundle = new Bundle(1);
                bundle.putString(IN_MAIL_EMAIL_ADDRESS, getIntent().getStringExtra(IN_MAIL_EMAIL_ADDRESS));
                mFragment.setArguments(bundle);
            }

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.container, mFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
