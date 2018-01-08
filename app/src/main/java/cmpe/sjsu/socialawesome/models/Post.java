package cmpe.sjsu.socialawesome.models;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmpe.sjsu.socialawesome.TimeLineFragment;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

/**
 * Created by lam on 4/27/17.
 */
@IgnoreExtraProperties
public class Post implements Comparable<Post>{
    public String ID;
    public User user;
    public String userid;
    public String contentPost;
    public String contentPhotoURL;
    public long timestamp;
    public int begenisayisi=0;
    private TimeLineFragment mActivity;
    private String key2;
    public static final String TAG = Post.class.getSimpleName();
    public String tur1,tur2,tur3;
    public List<String> begeniler,begeniler2;
    public Post(String ID,User user, long timestamp, String contentPost, String contentPhotoURL,List<String> begeniler,int begenisayisi) {
        this.user = user;
        this.ID=ID;
        this.contentPost = contentPost;
        this.contentPhotoURL = contentPhotoURL;
        this.timestamp = timestamp;
        this.begeniler=begeniler;
        this.begenisayisi=begenisayisi;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("contentPost", contentPost);
        result.put("contentPhotoURL", contentPhotoURL);
        result.put("timestamp", timestamp);
        result.put("begeniler",begeniler);
        result.put("ID",ID);
        result.put("begenisayisi",begenisayisi);
        result.put("tur1",tur1);
        result.put("tur2",tur2);
        result.put("tur3",tur3);
        result.put("userid",user.id);
        result.put("user_firstname",user.first_name);
        result.put("user_lastname",user.last_name);
        return result;
    }

    /*
public interface SimpleCallback{
    void callback(Object data);
}*/
public interface SimpleCallback<T>
{
    public void callback(T data);
}
    public User getUser() {
        return user;
    }

    public String getID() {
        return ID;
    }

    public String getAuthorName() {
        return user.first_name + " " + user.last_name;
    }

    public String getContentPost() {
        return contentPost;
    }

    public String getContentPhotoURL() {
        return contentPhotoURL;
    }
    public void getBegenisayisi2(@NonNull final SimpleCallback<Integer> finishedCallback)
    {
        DatabaseReference TableRef;
        String userid=user.unique_id;
        TableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(userid).child("posts").child(ID);
        // TableRef.child(userID).child("begeniID").setValue(userID);
        TableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                String s = hashMap.get("begenisayisi").toString();
             final   int ret=Integer.parseInt(s);
               finishedCallback.callback(ret);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public int getBegenisayisi()
    {
       // final String[] key = new String[1];
    //  final  String key2;
     /*   DatabaseReference TableRef;
        String userid=user.id;
        TableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(userid).child("posts").child(ID).child("begenisayisi");
       // TableRef.child(userID).child("begeniID").setValue(userID);
TableRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
    key2 =dataSnapshot.getValue(String.class);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});*/

     return  begenisayisi;
    }
    public List<String> getBegeniler() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userid=mAuth.getCurrentUser().getUid().toString();
        DatabaseReference TableRef;
     //   TableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(userid).child("posts").child(ID).child("begeniler");


//if(TableRef!=null) {
  //  begeniler2=getBegeniIDler(ID,userid);


   /* TimeLineFragment lastSMSFragment = (TimeLineFragment)getSupportFragmentManager().findFragmentByTag("lastSMSFragment"); */
  /*  TableRef.addListenerForSingleValueEvent(new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //   for (DataSnapshot ds : dataSnapshot.getChildren()) {
            //  mGroupList = new ArrayList<>();


            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                String s = hashMap.get("begeniID").toString();
                begeniler2.add(s);
            }


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {


        }
    });*/

//}


        return begeniler;
    }
    public void begeniEkle(String userID)
    {
     /*   FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        String userid=mAuth.getCurrentUser().getUid().toString();*/
     String userid=user.id;
        DatabaseReference TableRef;
        TableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(userid).child("posts").child(ID).child("begeniler");
        TableRef.child(userID).child("begeniID").setValue(userID);
       String oldu="oldu";
        //lastdayonearthburak2@gmail.com
        begenisayisi=begenisayisi+1;
        //return  oldu;*/
    }

    public long getTimestamp() {
        return timestamp;
    }
    public List<String> getBegeniIDler(String ID2,String userID){
//final List<String> begenilerreturn;
        DatabaseReference TableRef;
        TableRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(userID).child("posts").child(ID2).child("begeniler");
   /*     TableRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot2) {
                //   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                //  mGroupList = new ArrayList<>();


                for (DataSnapshot ds : dataSnapshot2.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String s = hashMap.get("begeniID").toString();
                    begeniler2.add(s);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
*/

        return begeniler2;
    }

    @Override
    public int compareTo(@NonNull Post post) {
        return (int)(post.getTimestamp() - timestamp);
    }
}
