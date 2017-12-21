package cmpe.sjsu.socialawesome;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cmpe.sjsu.socialawesome.Utils.FriendUtils;

import static cmpe.sjsu.socialawesome.models.User.FOLOWING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.PENDING_FRIEND_LIST;
import static cmpe.sjsu.socialawesome.models.User.WAITING_FRIEND_LIST;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends SocialFragment {

    private static List<String> mFriendList = new ArrayList<>();
    private RecyclerView recList;
    private FriendListAdapter mAdapter;
    private FriendListAdapter.OnInfoUpdateListener mListener;
    private TextView mFriendTitle;
    private View mAddFriendByEmailView;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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


       // getFriendList(FRIEND_LIST);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_friend, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friendMenu:
                mFriendTitle.setText("Gruplarım");
                mAddFriendByEmailView.setVisibility(View.GONE);
                mFriendList = new ArrayList<>();
                getGroups("mine");
                return true;
            case R.id.addFriendMenu:
                mFriendTitle.setText("Tüm Gruplar");
                mAddFriendByEmailView.setVisibility(View.VISIBLE);
                mFriendList = new ArrayList<>();
                getGroups("all");
                return true;
            case R.id.incomingFriendReqMenu:
                mFriendTitle.setText("Grup Oluştur");
                mAddFriendByEmailView.setVisibility(View.GONE);
                mFriendList = new ArrayList<>();
                createGroup();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void getGroups (String control)
    {

    }
    public void createGroup()
    {

    }

}
