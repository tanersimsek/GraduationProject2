package cmpe.sjsu.socialawesome;

import android.support.v4.app.Fragment;

/**
 * Created by lam on 4/28/17.
 */

public abstract class SocialFragment extends Fragment {
    protected static String mTitle;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void onRefresh() {

    }
}
