package cmpe.sjsu.socialawesome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import cmpe.sjsu.socialawesome.models.InMailMessage;
import cmpe.sjsu.socialawesome.models.User;

import static cmpe.sjsu.socialawesome.StartActivity.USERS_TABLE;

/**
 * Created by lam on 4/28/17.
 */
public class InMailListFragment extends SocialFragment {
    final DatabaseReference mSelfRef = FirebaseDatabase.getInstance().getReference().child(USERS_TABLE)
            .child(UserAuth.getInstance().getCurrentUser().id).child(User.IN_MAIL);
    private RecyclerView mListView;
    private List<InMailMessage> inMailMessages = new ArrayList<>();
    private InMailListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.private_message_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (RecyclerView) view.findViewById(R.id.message_list);
        mListView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(llm);

        mAdapter = new InMailListAdapter(inMailMessages, new InMailListAdapter.OnInMailMessageClickListener() {
            @Override
            public void onClicked(String messageId) {
                Intent intent = new Intent(getActivity(), InMailActivity.class);
                intent.putExtra(InMailActivity.ACTION_EXTRA, InMailActivity.ACTION_DETAIL);
                intent.putExtra(InMailActivity.BUNDLE_MESSAGE_ID, messageId);
                startActivity(intent);
            }
        }, new InMailListAdapter.OnInMailMessageChangeListener() {
            @Override
            public void onChanged() {
                loadMessageList();
            }
        });
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_inmail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_in_mail) {
            Intent intent = new Intent(getActivity(), InMailActivity.class);
            intent.putExtra(InMailActivity.ACTION_EXTRA, InMailActivity.ACTION_DETAIL);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMessageList();
    }

    private void loadMessageList() {
        mSelfRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                inMailMessages.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    InMailMessage message = messageSnapshot.getValue(InMailMessage.class);
                    inMailMessages.add(message);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
