package cmpe.sjsu.socialawesome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.UserIDMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingFragment extends SocialFragment {
    private RadioButton radio_private;
    private RadioButton radio_public;
    private RadioButton radio_friend;
    private RadioButton radio_yes;
    private RadioButton radio_no;
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private Button backBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference msubData;
    private DatabaseReference msuData;
    private RadioGroup pushNoRadioGroup;
    private RadioButton pushNoRadioYes;
    private RadioButton pushNoRadioNo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTitle = context.getString(R.string.setting);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child(StartActivity.USERS_TABLE).child(UserAuth.getInstance().getCurrentUser().id);
        radio_private = (RadioButton) view.findViewById(R.id.radio_private);
        radio_public = (RadioButton) view.findViewById(R.id.radio_public);
        radio_friend = (RadioButton) view.findViewById(R.id.radio_friend);
        radio_yes = (RadioButton) view.findViewById(R.id.radio_yes);
        radio_no = (RadioButton) view.findViewById(R.id.radio_no);
        radioGroup1 = (RadioGroup) view.findViewById(R.id.radioGroup1);
        radioGroup2 = (RadioGroup) view.findViewById(R.id.radioGroup2);
        pushNoRadioGroup = (RadioGroup) view.findViewById(R.id.pushNoRadioGroup);
        pushNoRadioYes = (RadioButton) view.findViewById(R.id.pushNoRadioYes);
        pushNoRadioNo = (RadioButton) view.findViewById(R.id.pushNoRadioNo);

        int userStatus = UserAuth.getInstance().getCurrentUser().status;
        boolean userNotificationSet = UserAuth.getInstance().getCurrentUser().notification;
        boolean userPushNoSet = UserAuth.getInstance().getCurrentUser().pushNotification;

        switch (userStatus) {
            case 0:
                radio_private.setChecked(true);
                break;
            case 1:
                radio_friend.setChecked(true);
                break;
            case 2:
                radio_public.setChecked(true);
                break;
            default:
        }

        if (userNotificationSet) {
            radio_yes.setChecked(true);
        } else {
            radio_no.setChecked(true);
        }

        if (userPushNoSet) {
            pushNoRadioYes.setChecked(true);
        } else {
            pushNoRadioYes.setChecked(true);
        }

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mAuth = FirebaseAuth.getInstance();
                switch (checkedId) {
                    case R.id.radio_private:
                        mFirebaseDatabase.child("status").setValue(0);
                        Toast.makeText(getActivity(), "Profile Set to Private", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radio_public:
                        mFirebaseDatabase.child("status").setValue(1);
                        Toast.makeText(getActivity(), "Profile Set to Public", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radio_friend:
                        mFirebaseDatabase.child("status").setValue(2);
                        Toast.makeText(getActivity(), "Profile Set to Friend Only", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mAuth = FirebaseAuth.getInstance();
                switch (checkedId) {
                    case R.id.radio_yes:
                        mFirebaseDatabase.child("notification").setValue(true);
                        Toast.makeText(getActivity(), "Email Notification Set", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radio_no:
                        mFirebaseDatabase.child("notification").setValue(false);
                        Toast.makeText(getActivity(), "Disabled Email Notification", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        pushNoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mAuth = FirebaseAuth.getInstance();
                switch (checkedId) {
                    case R.id.pushNoRadioYes:
                        mFirebaseDatabase.child("pusNotification").setValue(true);
                        Toast.makeText(getActivity(), "Push Notification Set", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.pushNoRadioNo:
                        mFirebaseDatabase.child("pusNotification").setValue(false);
                        Toast.makeText(getActivity(), "Disabled Push Notification", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        return view;

    }
}

