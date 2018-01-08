package cmpe.sjsu.socialawesome;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.adapters.TimeLineAdapter;
import cmpe.sjsu.socialawesome.apriori.AprioriFrequentItemsetGenerator;
import cmpe.sjsu.socialawesome.apriori.FrequentItemsetData;
import cmpe.sjsu.socialawesome.models.Post;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.models.Post.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends SocialFragment {

    private RecyclerView mExploreListView;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TimeLineAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Post> postList=new ArrayList<>();
    public ProgressDialog progress;
    public static String FIREBASE_POST_KEY = "posts";
    public  int begenisayisi;
    private DatabaseReference TableRef2;
    private DatabaseReference PostTableRef;
    public List<String> begeniIDler,begeniIDler2;
    private DatabaseReference userTableRef;
    private DatabaseReference TableRef;
    private boolean flag,flag2;

    private List<Set<String>> itemsetList = new ArrayList<>();

    private String controluser;
    private String useridfb;
    private User userfb;
   /* public ExploreFragment() {
        // Required empty public constructor
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTitle ="Keşfet";

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_explore, container, false);
        OneriList();
        mExploreListView = (RecyclerView) view.findViewById(R.id.exploreListView);
      //  progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        progress = new ProgressDialog(getContext());
        progress.setCancelable(false);
        //progress.show();
        userTableRef = FirebaseDatabase.getInstance().getReference().child("users");
        PostTableRef=FirebaseDatabase.getInstance().getReference().child("posts");
        initPostListFromServer();

        mLayoutManager = new LinearLayoutManager(getContext());
        mExploreListView.setLayoutManager(mLayoutManager);

       // Collections.sort(postList);
       // mAdapter = new TimeLineAdapter(postList);
       // mExploreListView.setAdapter(mAdapter);

        return view;
    }

 /*   @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }*/
/* private Thread setKronometre(){
     return new Thread(new Runnable() {
         public void run() {
             while (progressStatus < 60) {


                 progressStatus += 1;
                 // yeni değeri ekranda göster ve progressBar'a set et.


                     public void run() {
                         progressBar.setProgress(progressStatus);

                     }
                 });

                 try {
                     // Sleep for 1 second.
                     // Just to display the progress slowly
                     Thread.sleep(1000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
         }
     });
 }*/
    private void initPostListFromServer()
    {

        PostTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                Iterator postIterator;

               HashMap usersMap = (HashMap) dataSnapshot.getValue();
               // HashMap currentUser = (HashMap) usersMap.get(UserAuth.getInstance().getCurrentUser().id);
             /*   if (currentUser.get(FIREBASE_POST_KEY) != null) {*/
                    postIterator = ((HashMap) usersMap.get(FIREBASE_POST_KEY)).entrySet().iterator();
                    while (postIterator.hasNext()) {
                        Map.Entry postEntry = (Map.Entry) postIterator.next();
                        HashMap postMap = (HashMap) postEntry.getValue();
                      //  begeniIDler=bosuserlist;
                        //   if( postMap.get("begeniler")!=null){
 flag=false;
                        final String IDdeneme=(String) postMap.get("ID");
                       String tur1= UserAuth.getInstance().getCurrentUser().Oneri1;
                        String tur2= UserAuth.getInstance().getCurrentUser().Oneri2;
                        String tur3= UserAuth.getInstance().getCurrentUser().Oneri3;
                        String fbtur1=(String) postMap.get("tur1");
                        String fbtur2=(String) postMap.get("tur2");
                        String fbtur3=(String) postMap.get("tur3");
                        String[] turbolme= tur1.split(", ");
                        String[] turbolme2= tur2.split(", ");
                        String[] turbolme3= tur3.split(", ");
                       int sayi= turbolme.length;
                       for(int i=0;i<sayi;i++)
                       {
                           int sayi2=turbolme[i].length();
                           String yenitur=turbolme[i].substring(1,sayi2-1);
                           Log.d(TAG, "Test2 = " );
                           if(yenitur.equals(fbtur1)||yenitur.equals(fbtur2)||yenitur.equals(fbtur3))
                           {
                               flag=true;
                           }
                       }
                        int sayi22= turbolme.length;
                        for(int i=0;i<sayi22;i++)
                        {
                            int sayi2=turbolme[i].length();
                            String yenitur=turbolme[i].substring(1,sayi2-1);
                            Log.d(TAG, "Test2 = " );
                            if(yenitur.equals(fbtur1)||yenitur.equals(fbtur2)||yenitur.equals(fbtur3))
                            {
                                flag=true;
                            }

                        }
                        int sayi3= turbolme.length;
                        for(int i=0;i<sayi3;i++)
                        {
                            int sayi2=turbolme[i].length();
                            String yenitur=turbolme[i].substring(1,sayi2-1);
                            Log.d(TAG, "Test2 = " );
                            if(yenitur.equals(fbtur1)||yenitur.equals(fbtur2)||yenitur.equals(fbtur3))
                            {
                                flag=true;
                            }
                        }
                        //   begeniIDler=getBegeniIDler(IDdeneme,UserAuth.getInstance().getCurrentUser().id);
                        final long timestap=(long) postMap.get("timestamp");
                        final String contentPost=(String) postMap.get("contentPost");
                        final String contentPhotoURL=(String) postMap.get("contentPhotoURL");
                        final String fistname=(String) postMap.get("user_firstname");
                        final String lastname=(String) postMap.get("user_lastname");
 useridfb=(String)postMap.get("userid");

                        begenisayisi=0;
                        Log.d(TAG, "Test2 = " );
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

                       /* DatabaseReference TableRef2;
                        TableRef2 = FirebaseDatabase.getInstance().getReference().child(FIREBASE_POST_KEY).child("posts").child(IDdeneme);

                        TableRef2.child("begenisayisi").setValue(begenisayisi);*/


                        Log.d(TAG, "Test2 = " );
                        flag2=true;
 controluser=UserAuth.getInstance().getCurrentUser().id;
if(controluser.equals(useridfb))
{
    userfb=UserAuth.getInstance().getCurrentUser();
    flag2=false;

}
else{

    userTableRef.orderByChild("id").equalTo(useridfb).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
             userfb=dataSnapshot.getValue(User.class);
        }



        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    });
}
User user=new User();
        user.id=useridfb;
        user.first_name=fistname;
        user.last_name=lastname;
if(flag==true&&flag2==true)
{ Post post = new Post(IDdeneme,user, timestap,
                                contentPost, contentPhotoURL,begeniIDler,begenisayisi);
                        postList.add(post);}
                    }

             //}

               Collections.sort(postList);
                mAdapter = new TimeLineAdapter(postList);
                mExploreListView.setAdapter(mAdapter);
                //progress.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void OneriList(){


        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userid=mAuth.getCurrentUser().getUid().toString();


        TableRef = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("posts");
        TableRef2=FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        TableRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                //  mGroupList = new ArrayList<>();


                 AprioriFrequentItemsetGenerator<String> generator =
                        new AprioriFrequentItemsetGenerator<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String s1 = hashMap.get("tur1").toString();
                    String s2=hashMap.get("tur2").toString();
                    String s3=hashMap.get("tur3").toString();
                 //   Log.d(TAG, "Test2 = " );
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
                    //begeniler2.add(s);
                }
                FrequentItemsetData<String>   data = generator.generate(itemsetList, 0.2);
               //  i = 1;
 Set<String> Oneri = null,Oneri2=null,Oneri3=null;

double max1=0,max2=0,max3=0,oran;
                for (Set<String> itemset : data.getFrequentItemsetList()) {
                  /*  System.out.printf("%2d: %9s, support: %1.1f\n",
                            i++,
                            itemset,
                            data.getSupport(itemset));*/
                  //Oneri=itemset;
//Oneri2= itemsetList.get(i);
                   // Oneri=itemset;
                    oran=data.getSupport(itemset);
                  //  i++;
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
                TableRef2.child("Oneri1").setValue(Oneri.toString());
                TableRef2.child("Oneri2").setValue(Oneri2.toString());
                TableRef2.child("Oneri3").setValue(Oneri3.toString());
                int controledecem=0;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
/*
        itemsetList.add(new HashSet<>(Arrays.asList("a", "b")));
        itemsetList.add(new HashSet<>(Arrays.asList("b", "c", "d")));
        itemsetList.add(new HashSet<>(Arrays.asList("a", "c", "d", "e")));
        itemsetList.add(new HashSet<>(Arrays.asList("a", "d", "e")));
        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "c")));

        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "c", "d")));
        itemsetList.add(new HashSet<>(Arrays.asList("a")));
        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "c")));
        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "d")));
        itemsetList.add(new HashSet<>(Arrays.asList("b", "c", "e")));

        FrequentItemsetData<String> data = generator.generate(itemsetList, 0.2);
        int i = 1;

        for (Set<String> itemset : data.getFrequentItemsetList()) {
            System.out.printf("%2d: %9s, support: %1.1f\n",
                    i++,
                    itemset,
                    data.getSupport(itemset));
        }*/
    }
    @Override
    public void onPause() {
        super.onPause();
        if (progress != null) progress.dismiss();
    }
    @Override
    public void onRefresh() {
        initPostListFromServer();
    }

}
