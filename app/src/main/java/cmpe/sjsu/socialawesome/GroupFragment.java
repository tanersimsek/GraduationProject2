package cmpe.sjsu.socialawesome;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cmpe.sjsu.socialawesome.Utils.FriendUtils;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.Group;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.R.id.allgroupMenu;
import static cmpe.sjsu.socialawesome.R.id.creategroupMenu;
import static cmpe.sjsu.socialawesome.R.id.default_activity_button;
import static cmpe.sjsu.socialawesome.R.id.mygroupMenu;
import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.PENDING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.Group.stringAll;
import static cmpe.sjsu.socialawesome.models.Group.stringMine;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends SocialFragment {

    private static List<String> mFriendList = new ArrayList<>();
    public static final String Groups_TABLE = "Groups";
  //  public static final String stringAll = "All";
 //   public static final String stringMine = "Mine";
 //   public static final String Users_TABLE = "users";
    private RecyclerView recList;
    private Button btn,btn2,btn3;
    private FriendListAdapter mAdapter;
    private FriendListAdapter.OnInfoUpdateListener mListener;
    private TextView mFriendTitle;
    private  EditText grupadi;
    private LinearLayout linearLayout1;
    private View mAddFriendByEmailView;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group, container, false);
        ///grupadi=(EditText) view.findViewById(R.id.edittextgrupadi);
        //linearLayout1=(LinearLayout)view.findViewById(R.id.lineargroups);*/
        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn=(Button)view.findViewById(R.id.buttongomygrups);
        btn2=(Button)view.findViewById(R.id.buttongoallgrups);
        btn3=(Button)view.findViewById(R.id.buttongocreategrup);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
//Gruplarım
           }
       });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//Tüm gruplar
                SocialFragment fragment = new AllGroupsActivity();
              SocialFragment  mCurrentFragment = new GroupFragment();

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();

             /*   Intent mainIntent = new Intent(getContext(), AllGroupsActivity.class);
                //mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);*/
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Grup oluştur
                Intent mainIntent = new Intent(getContext(), AddGroupActivity.class);
                //mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);

            }
        });
       /* recList = (RecyclerView) view.findViewById(R.id.cardList);
        mListener = new FriendListAdapter.OnInfoUpdateListener() {
            @Override
            public void onInfoUpdate(boolean bool, String st) {
                ((MainActivity) getActivity()).switchFriendToProfileFrag(bool, st);
            }
        };

        recList.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        mAdapter = new FriendListAdapter(mFriendList, mListener);
        recList.setAdapter(mAdapter);

        mFriendTitle = (TextView) view.findViewById(R.id.friend_frag_title);
        mAddFriendByEmailView = view.findViewById(R.id.add_email_friend_view);

        setHasOptionsMenu(true);


        Button addFriendEmailBtn = (Button) view.findViewById(R.id.add_email_friend_btn);
        final EditText emailET = (EditText) view.findViewById(R.id.email_editText);
        addFriendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                if (email.isEmpty() || email.length() == 0 || email.equals("") || email == null) {
                    emailET.setError("Please Enter the email!");
                } else {
                    FriendUtils.addFriendByEmail(getActivity(), email);
                    emailET.setText("");
                }
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
//linearLayout1.setVisibility(View.GONE);
        String control="";
getGroups(control);
       // getFriendList(FRIEND_LIST);
    }

 /*   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_group, menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mygroupMenu:
                mFriendTitle.setText("Gruplarim");
                mAddFriendByEmailView.setVisibility(View.GONE);
                mFriendList = new ArrayList<>();
                getGroups(stringMine);
                return true;
            case R.id.allgroupMenu:
                mFriendTitle.setText("Tum Gruplar");
                mAddFriendByEmailView.setVisibility(View.VISIBLE);
                mFriendList = new ArrayList<>();
                getGroups(stringAll);
                return true;
            case R.id.creategroupMenu:
                mFriendTitle.setText("Grup Olustur");
                mAddFriendByEmailView.setVisibility(View.GONE);
                mFriendList = new ArrayList<>();
                createGroup();
                return true;
            default :

            return super.onOptionsItemSelected(item);
        }
       // return super.onOptionsItemSelected(item);

    }
*/
    /* public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mygroupMenu:
                    mFriendTitle.setText("Gruplarim");
                    mAddFriendByEmailView.setVisibility(View.GONE);
                    mFriendList = new ArrayList<>();
                    getGroups(stringMine);
                    return true;
                case R.id.allgroupMenu:
                    mFriendTitle.setText("Tum Gruplar");
                    mAddFriendByEmailView.setVisibility(View.VISIBLE);
                    mFriendList = new ArrayList<>();
                    getGroups(stringAll);
                    return true;
                case R.id.creategroupMenu:
                    mFriendTitle.setText("Grup Olustur");
                    mAddFriendByEmailView.setVisibility(View.GONE);
                    mFriendList = new ArrayList<>();
                    createGroup();
                    return true;
                default:
                    return true;
                    //return super.onOptionsItemSelected(item);
            }

            }*/
    public void getGroups (String control)
    {
        //linearLayout1.setVisibility(View.GONE);
    }
    public void createGroup()
    {
     //   linearLayout1.setVisibility(View.VISIBLE);



        final String token = FirebaseInstanceId.getInstance().getToken();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Groups_TABLE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                UUID uuid = UUID.randomUUID();
                String uuidString = uuid.toString();
                if (dataSnapshot.getValue() != null) {
                    Group group = dataSnapshot.getValue(Group.class);

                    if (group != null && token != null && !token.equals(group.token)) {
                        group.token = token;
                        ref.child(uuidString).child("token").setValue(token);
                    }
               //     mProgressDialog.dismiss();
                //    UserAuth.getInstance().setCurrentUser(user);
                  //  launchMainActivity();
                } else {
                    final Group group = new Group();

                    group.group_name = grupadi.getText().toString();

                    group.status = 1;
                    group.notification = true;
                    group.token = token;
                    group.pushNotification = true;
                    ref.child(uuidString).setValue(group);
                  //  mProgressDialog.dismiss();
                   // UserAuth.getInstance().setCurrentUser(user);
                  //  nicknameUI();

              /*      mNextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            user.unique_id = mUniqueId.getText().toString();

                            if (TextUtils.isEmpty(user.unique_id)) {
                                mUniqueId.setError("Required.");
                                return;
                            } else {
                                ref.orderByChild("unique_id").equalTo(user.unique_id).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            Toast.makeText(StartActivity.this, "Username Is Already Taken",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            ref.child(fbUser.getUid()).setValue(user);
                                            UserAuth.getInstance().setCurrentUser(user);
                                            launchMainActivity();
                                            Log.d(TAG, "Successfully create user in db");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }


                        }
                    });*/

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

}
