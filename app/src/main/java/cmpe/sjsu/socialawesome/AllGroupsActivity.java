package cmpe.sjsu.socialawesome;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.Group;
import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;
import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.PENDING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;

public class AllGroupsActivity extends SocialFragment {

    private static List<String> mGroupList = new ArrayList<>();
    private RecyclerView recList;
    private GroupListAdapter mAdapter;
    private GroupListAdapter.OnInfoUpdateListener mListener;
    public static final String Groups_TABLE = "Groups";
    private SocialFragment mCurrentFragment;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTitle = "Tüm Gruplarım";

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_all_groups, container, false);
        return v;
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recList = (RecyclerView)view.findViewById(R.id.cardListgroups);
     /*   mListener = new GroupListAdapter.OnInfoUpdateListener() {
            @Override
            public void onInfoUpdate(boolean bool, String st) {
                ((MainActivity) getActivity()).switchFriendToProfileFrag(bool, st);
            }
        };*/
getgroups();
        recList.setHasFixedSize(false);

       LinearLayoutManager llm2=new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm2);
        mAdapter = new GroupListAdapter(mGroupList);
      recList.setAdapter(mAdapter);




    }
    @Override
    public void onStart() {
        super.onStart();
        getgroups();
    }

    public void getgroups()
    {
        final DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child(Groups_TABLE);
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                mGroupList = new ArrayList<>();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Group publicUser = postSnapshot.getValue(Group.class);

                        mGroupList.add(publicUser.id);

                    mAdapter.notifyDataSetChanged();
                }
                mAdapter = new GroupListAdapter(mGroupList);

                int type = 0;

                mAdapter.updateType(3);
                recList.setAdapter(mAdapter);
                //  }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }

}