package cmpe.sjsu.socialawesome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import cmpe.sjsu.socialawesome.Utils.DbUtils;
import cmpe.sjsu.socialawesome.Utils.FriendUtils;
import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.adapters.TimeLineAdapter;
import cmpe.sjsu.socialawesome.models.Post;
import cmpe.sjsu.socialawesome.models.User;
import cmpe.sjsu.socialawesome.models.UserIDMap;

import static cmpe.sjsu.socialawesome.InMailDetailFragment.IN_MAIL_EMAIL_ADDRESS;
import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;
import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.PENDING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends SocialFragment {
    private static final String TAG = ProfileFragment.class.toString();
    private static int UPLOAD_REQUEST = 31;
    String userId;
    private EditText mNicknameEt;
    private EditText mEmailEt;
    private EditText mLocationEt;
    private EditText mProfessionEt;
    private EditText mAboutEt;
    private EditText mInterestEt;
    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private TextView mNickname;
    private TextView mEmail;
    private TextView mLocation;
    private TextView mProfession;
    private TextView mAbout;
    private TextView mInterest;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mUniqueId;
    private ImageView profilePicEt;
    private ImageView profilePic;
    private Button mUpdateBtn;
    private Button mCancelBtn;
    private DatabaseReference mFirebaseDatabase;
    private View editView;
    private View standardView;
    private View changeProfileBtn;

    private String currentUserId;
    private MainActivity activity;
    private ProgressDialog pd;

    private RecyclerView mTimelineListView;
    private ArrayList<Post> postList;

    MenuItem mFollowItem;
    MenuItem mAddFriendItem;
    MenuItem mNewInMailItem;
    MenuItem mNewChatItem;
    MenuItem mEditProfileItem;

    public ProfileFragment() {
        mTitle = ProfileFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        setHasOptionsMenu(true);

        editView = view.findViewById(R.id.editView);
        standardView = view.findViewById(R.id.standardView);

        mNicknameEt = (EditText) view.findViewById(R.id.nickname);
        mFirstNameEt = (EditText) view.findViewById(R.id.first_name);
        mLastNameEt = (EditText) view.findViewById(R.id.last_name);
        mEmailEt = (EditText) view.findViewById(R.id.email);
        mLocationEt = (EditText) view.findViewById(R.id.location);
        mProfessionEt = (EditText) view.findViewById(R.id.profession);
        mAboutEt = (EditText) view.findViewById(R.id.about_me);
        mInterestEt = (EditText) view.findViewById(R.id.interests);
        mNickname = (TextView) view.findViewById(R.id.nicknameSV);
        mFirstName = (TextView) view.findViewById(R.id.first_nameSV);
        mLastName = (TextView) view.findViewById(R.id.last_nameSV);
        mEmail = (TextView) view.findViewById(R.id.emailSV);
        mLocation = (TextView) view.findViewById(R.id.locationSV);
        mProfession = (TextView) view.findViewById(R.id.professionSV);
        mAbout = (TextView) view.findViewById(R.id.about_meSV);
        mInterest = (TextView) view.findViewById(R.id.interestsSV);
        mUniqueId = (TextView) view.findViewById(R.id.uniqueSV);
        mUpdateBtn = (Button) view.findViewById(R.id.update_btn);
        mCancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        profilePic = (ImageView) view.findViewById(R.id.profilePicSV);
        profilePicEt = (ImageView) view.findViewById(R.id.profilePic);
        changeProfileBtn = view.findViewById(R.id.changeProfilePicBtn);
        mTimelineListView = (RecyclerView) view.findViewById(R.id.timelineListView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toggleEditMode(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mTimelineListView.setLayoutManager(mLayoutManager);

        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setMessage("Uploading image...");

        changeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), UPLOAD_REQUEST);
            }
        });

        activity = (MainActivity) getActivity();
        if (activity.isOtherUser && activity.otherUserId != null) {
            currentUserId = activity.otherUserId;
        } else {
            currentUserId = UserAuth.getInstance().getCurrentUser().id;
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference()
                .child(StartActivity.USERS_TABLE).child(currentUserId);


        mFirebaseDatabase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData mutableData) {

                final User user = mutableData.getValue(User.class);
                if (user != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postList = new ArrayList<>();
                            HashMap posts = ((HashMap) ((HashMap) mutableData.getValue()).get("posts"));
                            if (posts != null) {
                                Iterator postIterator = posts.entrySet().iterator();
                                while (postIterator.hasNext()) {
                                    Map.Entry entry = (Map.Entry) postIterator.next();
                                    HashMap postMap = (HashMap) entry.getValue();
                                    Post post = new Post(user, (long) postMap.get("timestamp"),
                                            (String) postMap.get("contentPost"), (String) postMap.get("contentPhotoURL"));
                                    postList.add(post);
                                }
                            }
                            populateStandardView(user);
                            populateInfoIntoEditText(user);
                            Collections.sort(postList);
                            TimeLineAdapter mAdapter = new TimeLineAdapter(postList);
                            mTimelineListView.setAdapter(mAdapter);
                        }
                    });

                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });


        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first_name = mFirstNameEt.getText().toString();
                String last_name = mLastNameEt.getText().toString();
                String email = mEmailEt.getText().toString();
                String location = mLocationEt.getText().toString();
                String nickname = mNicknameEt.getText().toString();
                String profession = mProfessionEt.getText().toString();
                String about_me = mAboutEt.getText().toString();
                String interest = mInterestEt.getText().toString();

                mFirebaseDatabase.child("first_name").setValue(first_name);
                mFirebaseDatabase.child("last_name").setValue(last_name);
                mFirebaseDatabase.child("email").setValue(email);
                mFirebaseDatabase.child("location").setValue(location);
                mFirebaseDatabase.child("nickname").setValue(nickname);
                mFirebaseDatabase.child("profession").setValue(profession);
                mFirebaseDatabase.child("about_me").setValue(about_me);
                mFirebaseDatabase.child("interest").setValue(interest);
                if (UserAuth.getInstance().getCurrentUser().profilePhotoURL != null) {
                    mFirebaseDatabase.child("profilePhotoURL").setValue(UserAuth.getInstance().getCurrentUser().profilePhotoURL);
                }

                UserAuth.getInstance().getCurrentUser().first_name = first_name;
                UserAuth.getInstance().getCurrentUser().last_name = last_name;
                UserAuth.getInstance().getCurrentUser().email = email;
                UserAuth.getInstance().getCurrentUser().location = location;
                UserAuth.getInstance().getCurrentUser().nickname = nickname;
                UserAuth.getInstance().getCurrentUser().profession = profession;
                UserAuth.getInstance().getCurrentUser().about_me = about_me;
                UserAuth.getInstance().getCurrentUser().interest = interest;
                populateStandardView(UserAuth.getInstance().getCurrentUser());
                Toast.makeText(getActivity(), "Profile Has Been Updated", Toast.LENGTH_SHORT).show();

                toggleEditMode(false);
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //displayProfile();
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);

        mFollowItem = menu.findItem(R.id.follow);
        mAddFriendItem = menu.findItem(R.id.addFriend);
        mNewInMailItem = menu.findItem(R.id.new_in_mail);
        mNewChatItem = menu.findItem(R.id.new_chat);
        mEditProfileItem = menu.findItem((R.id.editProfile));

        if (((MainActivity) getActivity()).isOtherUser) {

            mAddFriendItem.setVisible(true);
            mFollowItem.setVisible(true);
            mNewInMailItem.setVisible(true);
            mNewChatItem.setVisible(true);
            mEditProfileItem.setVisible (false);

            final String id = ((MainActivity) getActivity()).otherUserId;

            FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                    .child(FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(id).exists()) {
                        mAddFriendItem.setVisible(false);
                        mFollowItem.setVisible(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                    .child(PENDING_FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(id).exists()) {
                        mAddFriendItem.setTitle("Accept Friend Request");
                        mFollowItem.setVisible(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                    .child(FOLOWING_FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(id).exists()) {
                        mFollowItem.setTitle("UnFollow");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_in_mail) {
            if (((MainActivity) getActivity()).isOtherUser) {
                final Intent intent = new Intent(getActivity(), InMailActivity.class);
                final String id = ((MainActivity) getActivity()).otherUserId;

                DbUtils.executeById(getActivity(), id, new DbUtils.OnQueryDbListener() {
                    @Override
                    public void execute(User user) {
                        if (user == null) return;
                        intent.putExtra(IN_MAIL_EMAIL_ADDRESS, user.email);
                        intent.putExtra(InMailActivity.ACTION_EXTRA, InMailActivity.ACTION_DETAIL);
                        startActivity(intent);
                    }
                });
            }
            return true;
        } else if (item.getItemId() == R.id.new_chat) {
            if (((MainActivity) getActivity()).isOtherUser) {
                Intent intent = new Intent(getActivity(), PrivateMessageActivity.class);
                UserIDMap id = new UserIDMap();
                id.id = ((MainActivity) getActivity()).otherUserId;
                intent.putExtra(PrivateMessageActivity.BUNDLE_OTHER_USER, id);
                intent.putExtra(InMailActivity.ACTION_EXTRA, InMailActivity.ACTION_DETAIL);
                startActivity(intent);
            }

            return true;
        }

        if (((MainActivity) getActivity()).isOtherUser) {
            final String id = ((MainActivity) getActivity()).otherUserId;

            if (item.getItemId() == R.id.addFriend) {

                FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                        .child(PENDING_FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(id).exists()) {
                            FriendUtils.unFollowFriend(getActivity(), 0, id);
                            mAddFriendItem.setVisible(false);
                        } else {
                            FriendUtils.addFriend(getActivity(), 0, id);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            if (item.getItemId() == R.id.follow) {

                FirebaseDatabase.getInstance().getReference().child(USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id)
                        .child(FOLOWING_FRIEND_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(id).exists()) {
                            FriendUtils.unFollowFriend(getActivity(), 1, id);
                            mFollowItem.setTitle("Un-Followed");
                            mFollowItem.setEnabled(false);
                        } else {
                            FriendUtils.addFriend(getActivity(), 1, id);
                            mFollowItem.setTitle("Following");
                            mFollowItem.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }



        }

        if (item.getItemId() == R.id.editProfile) {

            toggleEditMode(true);
            mCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleEditMode(false);
                }
            });

            mEditProfileItem.setEnabled(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode(boolean isEditMode) {
        if (isEditMode) {
            mUpdateBtn.setVisibility(View.VISIBLE);
            mCancelBtn.setVisibility(View.VISIBLE);
            standardView.setVisibility(View.GONE);
            editView.setVisibility(View.VISIBLE);
            mTimelineListView.setVisibility(View.GONE);
        } else {
            mUpdateBtn.setVisibility(View.GONE);
            mCancelBtn.setVisibility(View.GONE);
            standardView.setVisibility(View.VISIBLE);
            editView.setVisibility(View.GONE);
            mTimelineListView.setVisibility(View.VISIBLE);
        }
    }

    private void populateStandardView(User user) {
        if (user.profilePhotoURL == null || user.profilePhotoURL.isEmpty()) {
            String defaultURL = getResources().getString(R.string.default_profile_pic);
            Picasso.with(getContext()).load(defaultURL).into(profilePic);
        } else {
            Picasso.with(getContext()).load(user.profilePhotoURL).into(profilePic);
        }
        setTextView(mFirstName, user.first_name);
        setTextView(mLastName, user.last_name);
        setTextView(mEmail, user.email);
        setTextView(mLocation, user.location);
        setTextView(mNickname, user.nickname);
        setTextView(mInterest, user.interest);
        setTextView(mProfession, user.profession);
        setTextView(mAbout, user.about_me);
        setTextView(mUniqueId, user.unique_id);
    }

    private void populateInfoIntoEditText(User user) {
        if (user.profilePhotoURL == null || user.profilePhotoURL.isEmpty()) {
            String defaultURL = getResources().getString(R.string.default_profile_pic);
            Picasso.with(getContext()).load(defaultURL).into(profilePicEt);
        } else {
            Picasso.with(getContext()).load(user.profilePhotoURL).into(profilePicEt);
        }
        setEditText(mFirstNameEt, user.first_name);
        setEditText(mLastNameEt, user.last_name);
        setEditText(mEmailEt, user.email);
        setEditText(mLocationEt, user.location);
        setEditText(mNicknameEt, user.nickname);
        setEditText(mInterestEt, user.interest);
        setEditText(mProfessionEt, user.profession);
        setEditText(mAboutEt, user.about_me);
    }

    private void setTextView(TextView tv, String str) {
        if (TextUtils.isEmpty(str)) {
            tv.setText("");
        } else {
            tv.setText(str);
        }
    }

    private void setEditText(EditText et, String st) {
        if (et != null && !TextUtils.isEmpty(st)) {
            et.setText(st);
        }
    }

    //private static profileDisplay getProfile(User user) {
    //    UserAuth.getInstance().setCurrentUser(user);


    //}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profilePicEt.setImageBitmap(bitmap);
                pd.show();
                String fileName = UUID.randomUUID().toString() + ".jpg";
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference newRef = storageRef.child(fileName);
                UploadTask uploadTask = newRef.putFile(filePath);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        UserAuth.getInstance().getCurrentUser().profilePhotoURL = taskSnapshot.getDownloadUrl().toString();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_LONG);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}