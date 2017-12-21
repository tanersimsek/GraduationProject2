package cmpe.sjsu.socialawesome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cmpe.sjsu.socialawesome.Utils.HTTPUtil;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.adapters.TimeLineAdapter;
import cmpe.sjsu.socialawesome.models.Post;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

public class TimeLineFragment extends SocialFragment {
    public static int CREATE_POST = 21;
    public static int RESULT_OK = 1;
    public static String POST_CONTENT_KEY = "postContentKey";
    public static String POST_CONTENT_URL_KEY = "postContentURLKey";
    public static String FIREBASE_POST_KEY = "posts";
    public static String FIREBASE_FRIENDS_KEY = "friends";
    public static String FIREBASE_FOLLOWING_KEY = "followingFriends";
    public static String FIREBASE_FOLLOWER_KEY = "followers";
    private RecyclerView mTimelineListView;
    private FloatingActionButton mAddNewPostBtnView;
    private TimeLineAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Post> postList;
    private DatabaseReference currentUserRef;
    private DatabaseReference userTableRef;
    private ProgressDialog progress;
    private ArrayList<String> emails;

    //    public TimeLineFragment() {
//        mTitle = TimeLineFragment.class.getSimpleName();
//    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTitle = context.getString(R.string.timeline);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        mTimelineListView = (RecyclerView) view.findViewById(R.id.timelineListView);
        mAddNewPostBtnView = (FloatingActionButton) view.findViewById(R.id.addNewPostBtn);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progress = new ProgressDialog(getContext());
        progress.setCancelable(false);
        progress.show();
        userTableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE);
        currentUserRef = userTableRef.child(UserAuth.getInstance().getCurrentUser().id);
        mLayoutManager = new LinearLayoutManager(getContext());
        mTimelineListView.setLayoutManager(mLayoutManager);
        initPostListFromServer();

        mAddNewPostBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivityForResult(intent, CREATE_POST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_POST && resultCode == RESULT_OK) {
            String picURL = data.getStringExtra(POST_CONTENT_URL_KEY);
            Post newPost = new Post(UserAuth.getInstance().getCurrentUser(),
                    Calendar.getInstance().getTime().getTime(),
                    data.getStringExtra(POST_CONTENT_KEY), picURL);
            mAdapter.addNewPost(newPost);
            addPostToServer(newPost);
            sendEmailToFollowers(newPost);
        }
    }

    private void sendEmailToFollowers(final Post post) {
        userTableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emails = new ArrayList<String>();
                HashMap usersMap = (HashMap) dataSnapshot.getValue();
                HashMap currentUser = (HashMap) usersMap.get(UserAuth.getInstance().getCurrentUser().id);
                if (currentUser.get(FIREBASE_FRIENDS_KEY) != null) {
                    Iterator friendIterator = ((HashMap) currentUser.get(FIREBASE_FRIENDS_KEY)).entrySet().iterator();
                    while (friendIterator.hasNext()) {
                        Map.Entry friendEntry = (Map.Entry) friendIterator.next();
                        HashMap friendMap = (HashMap) usersMap.get(friendEntry.getKey().toString());
                        if ((Boolean) friendMap.get("notification") && (Boolean) friendMap.get("pushNotification")) {
                            emails.add((String) friendMap.get("email"));
                        }
                    }
                }
                if (currentUser.get(FIREBASE_FOLLOWER_KEY) != null) {
                    Iterator followIterator = ((HashMap) currentUser.get(FIREBASE_FOLLOWER_KEY)).entrySet().iterator();
                    while (followIterator.hasNext()) {
                        Map.Entry followEntry = (Map.Entry) followIterator.next();
                        HashMap followMap = (HashMap) usersMap.get(followEntry.getKey().toString());
                        if ((Boolean) followMap.get("notification") && (Boolean) followMap.get("pushNotification")) {
                            emails.add((String) followMap.get("email"));
                        }
                    }
                }
                HTTPUtil.sendEmail(getContext(), emails, "SocialAwesome: Someone you are following has a new post!",
                        post.getContentPost());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (progress != null) progress.dismiss();
    }

    private void addPostToServer(Post post) {
        String key = currentUserRef.child(FIREBASE_POST_KEY).push().getKey();
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + FIREBASE_POST_KEY + "/" + key, postValues);
        currentUserRef.updateChildren(childUpdates);
    }

    private void initPostListFromServer() {
        userTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                Iterator postIterator;

                HashMap usersMap = (HashMap) dataSnapshot.getValue();
                HashMap currentUser = (HashMap) usersMap.get(UserAuth.getInstance().getCurrentUser().id);
                if (currentUser.get(FIREBASE_POST_KEY) != null) {
                    postIterator = ((HashMap) currentUser.get(FIREBASE_POST_KEY)).entrySet().iterator();
                    while (postIterator.hasNext()) {
                        Map.Entry postEntry = (Map.Entry) postIterator.next();
                        HashMap postMap = (HashMap) postEntry.getValue();
                        Post post = new Post(UserAuth.getInstance().getCurrentUser(), (long) postMap.get("timestamp"),
                                (String) postMap.get("contentPost"), (String) postMap.get("contentPhotoURL"));
                        postList.add(post);
                    }
                }
                if (currentUser.get(FIREBASE_FRIENDS_KEY) != null) {
                    Iterator friendIterator = ((HashMap) currentUser.get(FIREBASE_FRIENDS_KEY)).entrySet().iterator();
                    while (friendIterator.hasNext()) {
                        Map.Entry friendEntry = (Map.Entry) friendIterator.next();
                        HashMap friendMap = (HashMap) usersMap.get(friendEntry.getKey().toString());
                        User friendUser = new User();
                        friendUser.first_name = (String) friendMap.get("first_name");
                        friendUser.last_name = (String) friendMap.get("last_name");
                        friendUser.profilePhotoURL = (String) friendMap.get("profilePhotoURL");
                        HashMap detailFriendMap = (HashMap) friendMap.get(FIREBASE_POST_KEY);
                        if (detailFriendMap != null) {
                            postIterator = (detailFriendMap).entrySet().iterator();
                            while (postIterator.hasNext()) {
                                Map.Entry postEntry = (Map.Entry) postIterator.next();
                                HashMap postMap = (HashMap) postEntry.getValue();
                                Post post = new Post(friendUser, (long) postMap.get("timestamp"),
                                        (String) postMap.get("contentPost"), (String) postMap.get("contentPhotoURL"));
                                postList.add(post);
                            }
                        }
                    }
                }
                if (currentUser.get(FIREBASE_FOLLOWING_KEY) != null) {
                    Iterator followIterator = ((HashMap) currentUser.get(FIREBASE_FOLLOWING_KEY)).entrySet().iterator();
                    while (followIterator.hasNext()) {
                        Map.Entry followEntry = (Map.Entry) followIterator.next();
                        HashMap followMap = (HashMap) usersMap.get(followEntry.getKey().toString());
                        User followUser = new User();
                        followUser.first_name = (String) followMap.get("first_name");
                        followUser.last_name = (String) followMap.get("last_name");
                        followUser.profilePhotoURL = (String) followMap.get("profilePhotoURL");
                        HashMap followerMap = (HashMap) followMap.get(FIREBASE_POST_KEY);
                        if (followerMap != null) {
                            postIterator = followerMap.entrySet().iterator();
                            while (postIterator.hasNext()) {
                                Map.Entry postEntry = (Map.Entry) postIterator.next();
                                HashMap postMap = (HashMap) postEntry.getValue();
                                Post post = new Post(followUser, (long) postMap.get("timestamp"),
                                        (String) postMap.get("contentPost"), (String) postMap.get("contentPhotoURL"));
                                postList.add(post);
                            }
                        }
                    }
                }
                Collections.sort(postList);
                mAdapter = new TimeLineAdapter(postList);
                mTimelineListView.setAdapter(mAdapter);
                progress.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRefresh() {
        initPostListFromServer();
    }
}
