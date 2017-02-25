package aditya.thingspeak.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.models.SubscriptionObject;
import aditya.thingspeak.notifications.NotificationEventReceiver;
import aditya.thingspeak.notifications.NotificationIntentService;
import aditya.thingspeak.utilities.Constants;
import aditya.thingspeak.utilities.Settings;
import aditya.thingspeak.utilities.Utility;
import aditya.thingspeak.views.RVSubsAdapter;

public class SettingsActivity extends AppCompatActivity {

    TextView settingsEmail;
    TextView settingsPhone;
    TextView settingsChangePwd;
    RecyclerView settingsRecycler;
    List<SubscriptionObject> subscriptionObjects = new ArrayList<>();
    Firebase firebase;
    RVSubsAdapter adapter;
    ExpandableRelativeLayout expandableRelativeLayout;
    EditText oldPwd, newPwd;
    Button changePwdButton;
    TextView cancelPwdButton;
    NumberPicker refreshIntervalPicker;
    SwitchCompat notificationsSwitch;
    boolean notificationsEnabled;
    int refreshInterval=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_settings);

        //initialization
        settingsChangePwd = (TextView) findViewById(R.id.settings_change_pwd);
        settingsEmail = (TextView)findViewById(R.id.settings_email);
        settingsPhone= (TextView)findViewById(R.id.settings_phone);
        settingsRecycler= (RecyclerView) findViewById(R.id.subs_recycler);
        expandableRelativeLayout= (ExpandableRelativeLayout)findViewById(R.id.expandableLayoutSettings);
        cancelPwdButton= (TextView)findViewById(R.id.settings_pwd_cancel);
        changePwdButton = (Button)findViewById(R.id.settings_pwd_update);
        oldPwd= (EditText)findViewById(R.id.settings_old_pwd);
        newPwd = (EditText)findViewById(R.id.settings_new_pwd);
        refreshIntervalPicker = (NumberPicker)findViewById(R.id.settings_number_picker);
        notificationsSwitch = (SwitchCompat)findViewById(R.id.settings_notifications_enabled);
        Firebase.setAndroidContext(getApplicationContext());
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebase = new Firebase(Constants.BASE_URL+Constants.USERS_MAP+"/"+mAuth.getCurrentUser().getUid());



        notificationsEnabled=true;
        if(!Settings.getSharedPreference(getApplicationContext(),"notifications").equals("")){
            notificationsEnabled = Boolean.parseBoolean(Settings.getSharedPreference(getApplicationContext(),"notifications"));

        }
        Log.d("ntooff",""+notificationsEnabled);


        notificationsSwitch.setChecked(notificationsEnabled);
        refreshIntervalPicker.setMinValue(0);
        refreshIntervalPicker.setMaxValue(300);


        if(!Settings.getSharedPreference(getApplicationContext(),"refreshinterval").equals("")){

            Log.d("ri","not null");

            refreshInterval= Integer.parseInt(Settings.getSharedPreference(getApplicationContext(),"refreshinterval"));
            Log.d("rfrsointrvl",refreshInterval+"");
        }



        refreshIntervalPicker.setValue(refreshInterval);
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              switchNotificationService(isChecked);
            }
        });

         refreshIntervalPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    Settings.setSharedPreference(getApplicationContext(),"refreshinterval",String.valueOf(newVal));

            }
        });

        //get user email from current user
        final String email = mAuth.getCurrentUser().getEmail();
        settingsEmail.setText(email);


        settingsChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(!expandableRelativeLayout.isExpanded())
                 expandableRelativeLayout.expand();
            }
        });
        cancelPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPwd.setText("");
                newPwd.setText("");
                expandableRelativeLayout.collapse();
            }
        });
        changePwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPwd.getText().toString().trim().isEmpty()|| newPwd.getText().toString().trim().isEmpty()){
                    Utility.showSnack(getApplicationContext(),settingsChangePwd,Utility.FIELD_EMPTY);
                }
                else{

                    mAuth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(email,oldPwd.getText().toString().trim())).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mAuth.getCurrentUser().updatePassword(newPwd.getText().toString().trim());
                                oldPwd.setText("");
                                newPwd.setText("");
                                expandableRelativeLayout.collapse();
                                Utility.showSnack(getApplicationContext(),settingsChangePwd,Utility.DONE);

                            }else{
                                Utility.showSnack(getApplicationContext(),settingsChangePwd,"Incorrect password!");
                            }
                        }
                    });
                }
            }
        });


        firebase.child("mobile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String phone = dataSnapshot.getValue().toString();
                settingsPhone.setText(phone);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        firebase.child("/subscriptions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subscriptionObjects.clear();

                for(DataSnapshot subsItem : dataSnapshot.getChildren())
                {
                    SubscriptionObject subscriptionObject = new SubscriptionObject();
                    subscriptionObject.setChannelID(subsItem.child("channelID").getValue().toString());
                    subscriptionObject.setFieldID(subsItem.child("fieldID").getValue().toString());
                    subscriptionObject.setMaxVal(subsItem.child("maxVal").getValue().toString());
                    subscriptionObject.setMinVal(subsItem.child("minVal").getValue().toString());
                    subscriptionObject.setFieldLabel(subsItem.child("fieldLabel").getValue().toString());
                    subscriptionObject.setPushID(subsItem.child("pushID").getValue().toString());
                    subscriptionObjects.add(subscriptionObject);


                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        settingsRecycler.setLayoutManager(linearLayoutManager);
        adapter = new RVSubsAdapter(getApplicationContext(), subscriptionObjects, new ClickListener() {
            @Override
            public void onClick(int position) {
                deleteChild(position);
            }
        });
        settingsRecycler.setAdapter(adapter);


    }



    private void deleteChild(final int position) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this subscription?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("id",subscriptionObjects.get(position).getPushID());
                firebase.child("subscriptions").child(subscriptionObjects.get(position).getPushID()).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if(firebaseError!=null)
                            Utility.showSnack(getApplicationContext(),settingsChangePwd,Utility.SOMETHING_WRONG);
                        else
                            Utility.showSnack(getApplicationContext(),settingsChangePwd,Utility.DONE);
                    }
                });

            }
        });
        builder.show();



    }

    private void switchNotificationService(boolean isChecked) {
        if(!isChecked){
            Settings.setSharedPreference(getApplicationContext(),"notifications",isChecked+"");
            stopService(new Intent(getApplicationContext(),NotificationIntentService.class));
        }else{
            Settings.setSharedPreference(getApplicationContext(),"notifications",isChecked+"");
            NotificationEventReceiver.setupAlarm(getApplicationContext());
        }
    }

    public interface  ClickListener{
        void onClick(int position);
    }
}
