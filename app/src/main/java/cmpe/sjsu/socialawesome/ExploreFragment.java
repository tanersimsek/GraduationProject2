package cmpe.sjsu.socialawesome;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
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

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends SocialFragment {

    private RecyclerView mTimelineListView;

    private TimeLineAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Post> postList;
    private ProgressDialog progress;
    public static String FIREBASE_POST_KEY = "posts";
    private  int begenisayisi;
    public List<String> begeniIDler,begeniIDler2;
    private DatabaseReference userTableRef;
    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_explore, container, false);
        OneriList();
        mTimelineListView = (RecyclerView) view.findViewById(R.id.expolereListView);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progress = new ProgressDialog(getContext());
        progress.setCancelable(false);
        progress.show();
        userTableRef=FirebaseDatabase.getInstance().getReference().child(FIREBASE_POST_KEY).child(FIREBASE_POST_KEY);
        mLayoutManager = new LinearLayoutManager(getContext());
        mTimelineListView.setLayoutManager(mLayoutManager);
        initPostListFromServer();

    }

    public void initPostListFromServer()
    {
        userTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                Iterator postIterator;

               HashMap usersMap = (HashMap) dataSnapshot.getValue();
              /*  HashMap currentUser = (HashMap) usersMap.get(UserAuth.getInstance().getCurrentUser().id);
                if (currentUser.get(FIREBASE_POST_KEY) != null) {*/
                    postIterator = ((HashMap) usersMap.get(FIREBASE_POST_KEY)).entrySet().iterator();
                    while (postIterator.hasNext()) {
                        Map.Entry postEntry = (Map.Entry) postIterator.next();
                        HashMap postMap = (HashMap) postEntry.getValue();
                      //  begeniIDler=bosuserlist;
                        //   if( postMap.get("begeniler")!=null){

                        final String IDdeneme=(String) postMap.get("ID");
                       String tur1= UserAuth.getInstance().getCurrentUser().Oneri1;
                        String tur2= UserAuth.getInstance().getCurrentUser().Oneri2;
                        String tur3= UserAuth.getInstance().getCurrentUser().Oneri3;
                        String[] turbolme= tur1.split(",");
                        //   begeniIDler=getBegeniIDler(IDdeneme,UserAuth.getInstance().getCurrentUser().id);
                        final long timestap=(long) postMap.get("timestamp");
                        final String contentPost=(String) postMap.get("contentPost");
                        final String contentPhotoURL=(String) postMap.get("contentPhotoURL");

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

                        DatabaseReference TableRef2;
                        TableRef2 = FirebaseDatabase.getInstance().getReference().child(FIREBASE_POST_KEY).child("posts").child(IDdeneme);

                        TableRef2.child("begenisayisi").setValue(begenisayisi);





                        Post post = new Post(IDdeneme,UserAuth.getInstance().getCurrentUser(), timestap,
                                contentPost, contentPhotoURL,begeniIDler,begenisayisi);
                        postList.add(post);
                    }

             //}

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
    private void OneriList(){


        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userid=mAuth.getCurrentUser().getUid().toString();
        final DatabaseReference TableRef,TableRef2;
        TableRef = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("posts");
        TableRef2=FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        TableRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                //  mGroupList = new ArrayList<>();
                AprioriFrequentItemsetGenerator<String> generator =
                        new AprioriFrequentItemsetGenerator<>();
                List<Set<String>> itemsetList = new ArrayList<>();



                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String s1 = hashMap.get("tur1").toString();
                    String s2=hashMap.get("tur2").toString();
                    String s3=hashMap.get("tur3").toString();
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
                FrequentItemsetData<String> data = generator.generate(itemsetList, 0.2);
                int i = 1;
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

}
