package cmpe.sjsu.socialawesome;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import cmpe.sjsu.socialawesome.models.UserIDMap;
import cmpe.sjsu.socialawesome.models.UserSummary;

public class PrivateMessageActivity extends AppCompatActivity {
    public static final String ACTION_LIST = "open_list_message";
    public static final String ACTION_DETAIL = "open_new_message";
    public static final String ACTION_EXTRA = "open_new_message";
    public static final String BUNDLE_OTHER_USER = "bundle_other_user";

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
            UserIDMap otherUser = (UserIDMap) getIntent().getSerializableExtra(BUNDLE_OTHER_USER);
            switch (getIntent().getStringExtra(ACTION_EXTRA)) {
                case ACTION_LIST:
                    mFragment = new PrivateMessageListFragment();
                    break;
                case ACTION_DETAIL:
                    mFragment = new PrivateMessageChatFragment();
                    break;
                default:
                    break;
            }

            Bundle bundle = new Bundle(1);
            bundle.putSerializable(PrivateMessageChatFragment.OTHER_USER_BUNDLE, otherUser);
            mFragment.setArguments(bundle);

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
