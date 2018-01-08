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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import cmpe.sjsu.socialawesome.Utils.HTTPUtil;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.adapters.TimeLineAdapter;
import cmpe.sjsu.socialawesome.apriori.AprioriFrequentItemsetGenerator;
import cmpe.sjsu.socialawesome.models.Post;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

public class TimeLineFragment extends SocialFragment {
    public static int CREATE_POST = 21;
    public static int RESULT_OK = 1;
    public static String POST_CONTENT_KEY = "postContentKey";
    public static String tur1="tur1";
    public static String tur2="tur2";
    public static String tur3="tur3";
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
    private DatabaseReference TableRef2;
    private DatabaseReference userTableRef;
    private DatabaseReference PostsRef;
    private ProgressDialog progress;
    private ArrayList<String> emails;
    public List<String> bosuserlist;
    private AprioriFrequentItemsetGenerator<String> generator =
            new AprioriFrequentItemsetGenerator<>();
    private List<Set<String>> itemsetList = new ArrayList<>();
    public List<String> begeniIDler,begeniIDler2;
    public    int begenisayisi=0;
    public String tur11,tur22,tur33;
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

        PostsRef=FirebaseDatabase.getInstance().getReference().child(FIREBASE_POST_KEY);
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
            tur11=data.getStringExtra(tur1);
            tur22=data.getStringExtra(tur2);
            tur33=data.getStringExtra(tur3);
            UUID idOne = UUID.randomUUID();
            String idpost=idOne.toString();
            begenisayisi=0;
            Post newPost = new Post(idpost,UserAuth.getInstance().getCurrentUser(),
                    Calendar.getInstance().getTime().getTime(),
                    data.getStringExtra(POST_CONTENT_KEY), picURL,bosuserlist,begenisayisi);
            mAdapter.addNewPost(newPost);
            addPostToServer(newPost);
            //sendEmailToFollowers(newPost);
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
        String key2=PostsRef.push().getKey();
        post.ID=key;
        post.tur1=tur11;
        post.tur2=tur22;
        post.tur3=tur33;

        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + FIREBASE_POST_KEY + "/" + key, postValues);
        currentUserRef.updateChildren(childUpdates);
        PostsRef.updateChildren(childUpdates);
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
                        begeniIDler=bosuserlist;
                     //   if( postMap.get("begeniler")!=null){

                            final String IDdeneme=(String) postMap.get("ID");

              //   begeniIDler=getBegeniIDler(IDdeneme,UserAuth.getInstance().getCurrentUser().id);
                            final long timestap=(long) postMap.get("timestamp");
                            final String contentPost=(String) postMap.get("contentPost");
                            final String contentPhotoURL=(String) postMap.get("contentPhotoURL");

                  /*      String s1 =(String) postMap.get("tur1");
                        String s2=(String) postMap.get("tur2");
                        String s3=(String) postMap.get("tur3");
                        Log.d(TAG, "Test2 = " );
                        if(s1.equals("diger")&&s2.equals("diger"))
                        {   itemsetList.add(new HashSet<>(Arrays.asList(s3)));}
                        else if(s1.equals("diger")&&s3.equals("diger"))
                        {   itemsetList.add(new HashSet<>(Arrays.asList(s2)));}
                        else if(s3.equals("diger")&&s2.equals("diger"))
                        {   itemsetList.add(new HashSet<>(Arrays.asList(s1)));}
                        else if(s1.equals("diger"))
                        {   itemsetList.add(new HashSet<>(Arrays.asList(s2,s3)));}
                        else  if(s2.equals("diger"))
                        {   itemsetList.add(new HashSet<>(Arrays.asList(s1,s3)));}
                        else  if(s3.equals("diger"))
                        {   itemsetList.add(new HashSet<>(Arrays.asList(s1,s2)));}
                        else  if(s1.equals("diger")&&s2.equals("diger")&&s3.equals("diger"))
                        { String hatasiz="kul"; }
                        else{
                            itemsetList.add(new HashSet<>(Arrays.asList(s1,s2,s3)));
                        }
*/



                        begenisayisi=0;
                        if(postMap.get("begeniler")!=null)
                        {
                            String count=postMap.get("begeniler").toString();
                            String[] countryLines = count.split("=");
                            int countryLineCount = countryLines.length;


                         for(int i=2;i<=countryLineCount;i=i+2)
                         {
                            String temp=countryLines[i].substring(0,27);
                            begenisayisi++;
                          //  begeniIDler2.add(temp);

                         }
                        }
                      //  DatabaseReference TableRef;
                     //   begenisayisi=begeniIDler2.size();
                         //  TableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id).child("posts").child(IDdeneme).child("begeniler");
                     /*       TableRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    //   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    //  mGroupList = new ArrayList<>();


                                    for (DataSnapshot ds : dataSnapshot2.getChildren()) {
                                        HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                                        String s = hashMap.get("begeniID").toString();
                                        begenisayisi++;
                                      //  Post post = ds.getValue(Post.class);
                                        //     begeniIDler.add(s);
                                      //  mAdapter.notifyDataSetChanged();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {


                                }
                            });*/
                        //DatabaseReference TableRef2;

                        TableRef2 = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id).child("posts").child(IDdeneme);
                        TableRef2.child("begenisayisi").setValue(begenisayisi);


                      /*  for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                            String s = ds.getKey();
                            begeniIDler.add(s);
                        }*/
                       // } if get.begeniler nullun kapanışıydı


                        Post post = new Post(IDdeneme,UserAuth.getInstance().getCurrentUser(), timestap,
                                contentPost, contentPhotoURL,begeniIDler,begenisayisi);
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
                        friendUser.id=(String)friendMap.get("id");
                        HashMap detailFriendMap = (HashMap) friendMap.get(FIREBASE_POST_KEY);
                        if (detailFriendMap != null) {
                            postIterator = (detailFriendMap).entrySet().iterator();
                            while (postIterator.hasNext()) {
                                Map.Entry postEntry = (Map.Entry) postIterator.next();
                                HashMap postMap = (HashMap) postEntry.getValue();

                                begeniIDler=bosuserlist;
                             /*   for(DataSnapshot ds:dataSnapshot.getChildren()) {
                                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                                    String s = hashMap.get("begeniID").toString();
                                    begeniIDler.add(s);
                                }*/


                                begenisayisi=0;
                                if(postMap.get("begeniler")!=null)
                                {
                                    String count=postMap.get("begeniler").toString();
                                    String[] countryLines = count.split("=");
                                    int countryLineCount = countryLines.length;


                                    for(int i=2;i<=countryLineCount;i=i+2)
                                    {
                                        String temp=countryLines[i].substring(0,27);
                                        begenisayisi++;
                                        //  begeniIDler2.add(temp);

                                    }
                                }
                                DatabaseReference TableRef;
                                Post post = new Post((String) postMap.get("ID"),friendUser, (long) postMap.get("timestamp"),
                                        (String) postMap.get("contentPost"), (String) postMap.get("contentPhotoURL"),begeniIDler,begenisayisi);
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
                                begeniIDler=bosuserlist;
                             /*   for(DataSnapshot ds:dataSnapshot.getChildren()) {
                                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                                    String s = hashMap.get("begeniID").toString();
                                    begeniIDler.add(s);
                                }*/


                                begenisayisi=0;
                                if(postMap.get("begeniler")!=null)
                                {
                                    String count=postMap.get("begeniler").toString();
                                    String[] countryLines = count.split("=");
                                    int countryLineCount = countryLines.length;


                                    for(int i=2;i<=countryLineCount;i=i+2)
                                    {
                                        String temp=countryLines[i].substring(0,27);
                                        begenisayisi++;
                                        //  begeniIDler2.add(temp);

                                    }
                                }
                                DatabaseReference TableRef;
                                Post post = new Post((String) postMap.get("ID"),followUser, (long) postMap.get("timestamp"),
                                        (String) postMap.get("contentPost"), (String) postMap.get("contentPhotoURL"),begeniIDler,begenisayisi);

                                postList.add(post);
                            }
                        }
                    }
                }

            /*    FrequentItemsetData<String> data = generator.generate(itemsetList, 0.2);
                int i = 1;
                Set<String> Oneri = null,Oneri2=null,Oneri3=null;

                double max1=0,max2=0,max3=0,oran;
                for (Set<String> itemset : data.getFrequentItemsetList()) {

                    //Oneri=itemset;
//Oneri2= itemsetList.get(i);
                    // Oneri=itemset;
                    oran=data.getSupport(itemset);
                    i++;
                    if(oran>max1) {
                        max1 = oran;
                        Oneri=itemset;
                    }
                    else if(oran>max2){
                        Oneri2=itemset;
                        max2=oran;}
                    else  if(oran>max3){
                        Oneri3=itemset;
                        max3=oran;}
                }

                // int sayi=Oneri.size();
                UserAuth.getInstance().getCurrentUser().Oneri1=Oneri.toString();
                UserAuth.getInstance().getCurrentUser().Oneri2=Oneri2.toString();
                UserAuth.getInstance().getCurrentUser().Oneri3=Oneri3.toString();
                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                String userid=mAuth.getCurrentUser().getUid().toString();

                TableRef2.child("Oneri1").setValue(Oneri.toString());
                TableRef2.child("Oneri2").setValue(Oneri2.toString());
                TableRef2.child("Oneri3").setValue(Oneri3.toString());
                TableRef2=FirebaseDatabase.getInstance().getReference().child("users").child(userid);*/
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

    public List<String> getBegeniIDler(String ID,String userID){
//final List<String> begenilerreturn;



        return begeniIDler;
    }
    @Override
    public void onRefresh() {
        initPostListFromServer();
    }
}
