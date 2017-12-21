package cmpe.sjsu.socialawesome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import cmpe.sjsu.socialawesome.Utils.UserAuth;

public class MainActivity extends AppCompatActivity {
    public String otherUserId = null;
    public boolean isOtherUser = false;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private List<String> mDrawerListTitles = new ArrayList<>();
    private ActionBarDrawerToggle mDrawerToggle;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SocialFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerListTitles.add(getString(R.string.timeline));
        mDrawerListTitles.add(getString(R.string.profile));
        mDrawerListTitles.add(getString(R.string.friends));
        mDrawerListTitles.add(getString(R.string.title_inmail));
        mDrawerListTitles.add(getString(R.string.messenger));
        mDrawerListTitles.add("Gruplar");
        mDrawerListTitles.add(getString(R.string.setting));
        mDrawerListTitles.add(getString(R.string.signout));

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mDrawerListTitles));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SocialFragment fragment = null;
                String title = null;
                Intent intent = null;
                switch (position) {
                    case 0:
                        //Timeline
                        title = getString(R.string.timeline);
                        fragment = new TimeLineFragment();
                        break;
                    case 1:
                        //Profile
                        title = getString(R.string.profile);
                        isOtherUser = false;
                        fragment = new ProfileFragment();
                        break;
                    case 2:
                        //Friends
                        title = getString(R.string.friends);
                        fragment = new FriendFragment();
                        break;
                    case 3:
                        //In Mail List
                        intent = new Intent(MainActivity.this, InMailActivity.class);
                        intent.putExtra(InMailActivity.ACTION_EXTRA, InMailActivity.ACTION_LIST);
                        startActivity(intent);
                        return;
                    case 4:
                        intent = new Intent(MainActivity.this, PrivateMessageActivity.class);
                        intent.putExtra(PrivateMessageActivity.ACTION_EXTRA, PrivateMessageActivity.ACTION_LIST);
                        startActivity(intent);
                        return;
                    case 5:
                        //Gruplar
                        title = "Gruplar";
                        fragment = new GroupFragment();
                        break;
                    case 6:
                        //Setting
                        title = getString(R.string.setting);
                        fragment = new SettingFragment();
                        break;
                    case 7:
                        //Sign Out
                        signOut();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        return;
                    default:
                        break;
                }

                if (mCurrentFragment == null || !mCurrentFragment.getTitle().equals(title)) {
                    mCurrentFragment = fragment;
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.commit();
                    mDrawerLayout.closeDrawers();
                    setTitle(title);
                }
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (getSupportActionBar() == null) return;
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getSupportActionBar() == null) return;
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        String mTitle = getString(R.string.timeline);
        SocialFragment fragment = new TimeLineFragment();
        mCurrentFragment = fragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void switchFriendToProfileFrag(boolean isOtherU, String otherUserI) {
        isOtherUser = isOtherU;
        otherUserId = otherUserI;
        SocialFragment fragment = new ProfileFragment();
        mCurrentFragment = fragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view

        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        if (item.getItemId() == R.id.refresh) {
            if (mCurrentFragment != null) {
                mCurrentFragment.onRefresh();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void signOut() {
        UserAuth.getInstance().setCurrentUser(null);
        if (mAuthListener != null) {
            mAuth.signOut();
        }

    }
}
