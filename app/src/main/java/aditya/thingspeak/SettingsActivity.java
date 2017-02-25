package aditya.thingspeak;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import aditya.thingspeak.models.SubscriptionObject;
import aditya.thingspeak.utilities.Constants;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_settings);
        settingsChangePwd = (TextView) findViewById(R.id.settings_change_pwd);

        settingsEmail = (TextView)findViewById(R.id.settings_email);
        settingsPhone= (TextView)findViewById(R.id.settings_phone);
        settingsRecycler= (RecyclerView) findViewById(R.id.subs_recycler);
        expandableRelativeLayout= (ExpandableRelativeLayout)findViewById(R.id.expandableLayoutSettings);
        cancelPwdButton= (TextView)findViewById(R.id.settings_pwd_cancel);
        changePwdButton = (Button)findViewById(R.id.settings_pwd_update);
        oldPwd= (EditText)findViewById(R.id.settings_old_pwd);
        newPwd = (EditText)findViewById(R.id.settings_new_pwd);
        Firebase.setAndroidContext(getApplicationContext());
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebase = new Firebase(Constants.BASE_URL+Constants.USERS_MAP+"/"+mAuth.getCurrentUser().getUid());
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
                    Snackbar.make(settingsChangePwd,"One or more fields empty!",Snackbar.LENGTH_SHORT).show();
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
                            }else{
                                Snackbar.make(settingsChangePwd,"Incorrect password!",Snackbar.LENGTH_SHORT).show();
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
                firebase.child(subscriptionObjects.get(position).getPushID()).removeValue();
            }
        });
        settingsRecycler.setAdapter(adapter);


    }

    public interface  ClickListener{
        void onClick(int position);
    }
}
