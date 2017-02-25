package aditya.thingspeak.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import aditya.thingspeak.models.ChannelAddObject;
import aditya.thingspeak.models.ChannelObject;
import aditya.thingspeak.notifications.NotificationEventReceiver;
import aditya.thingspeak.utilities.Constants;
import aditya.thingspeak.R;
import aditya.thingspeak.utilities.Settings;
import aditya.thingspeak.utilities.Utility;
import aditya.thingspeak.views.RecyclerViewAdapter;
import aditya.thingspeak.utilities.RestClient;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class HomeActivity extends AppCompatActivity {

    FabSpeedDial fabSpeedDial;
    private GridLayoutManager lLayout;
    EditText etAddChannelID,etAddChannelURL;
    Button btAddChannelDone;
    TextView tvAddChannelCancel;
    ExpandableRelativeLayout expandableLinearLayoutHome;
    MultiStateToggleButton multiStateToggleButton;
    Firebase firebase;
    FirebaseAuth mAuth;
    List<ChannelObject> listOfChannels = new ArrayList<ChannelObject>();
    RecyclerViewAdapter rcAdapter;
    List<ChannelAddObject> channelAddObjects = new ArrayList<>();
    boolean editChannelMode = false;
    int itemClickedPosition;
    public static boolean publicFeed = false;
    RecyclerView rView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        etAddChannelID= (EditText)findViewById(R.id.home_add_channel_id);
        etAddChannelURL = (EditText)findViewById(R.id.home_add_channel_url);
        btAddChannelDone= (Button)findViewById(R.id.home_add_channel_done);
        tvAddChannelCancel= (TextView)findViewById(R.id.home_add_channel_cancel);
        expandableLinearLayoutHome = (ExpandableRelativeLayout)findViewById(R.id.expandableLayoutHome);
        fabSpeedDial = (FabSpeedDial)findViewById(R.id.fab_speed_dial);
        rView = (RecyclerView)findViewById(R.id.channel_recycler);
        multiStateToggleButton = (MultiStateToggleButton)findViewById(R.id.home_choose_channel_type);
        Firebase.setAndroidContext(this);
        firebase = new Firebase(Constants.BASE_URL+Constants.USERS_MAP);
        mAuth = FirebaseAuth.getInstance();

        multiStateToggleButton.setValue(0);

        multiStateToggleButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                if(value==0){
                    publicFeed = false;
                }else{
                    publicFeed = true;
                }
                resetRecycerFetchData(value);
            }
        });




        lLayout = new GridLayoutManager(HomeActivity.this, 2);

        //rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        rcAdapter = new RecyclerViewAdapter(HomeActivity.this, listOfChannels, new EditButtonClickListener() {
            @Override
            public void itemClicked(int position) {
                //Perform Edit actions
                expandableLinearLayoutHome.expand();
                editChannelMode = true;
                itemClickedPosition = position;
                btAddChannelDone.setText("MAKE CHANGES");


            }
        },
                new EditButtonClickListener() {
                    @Override
                    public void itemClicked(int position) {

                        //Perform Delete Actions


                        firebase.child(mAuth.getCurrentUser().getUid()).child("channels").child(channelAddObjects.get(position).getChannelPushID()).removeValue(new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {

                                    Utility.showSnack(getApplicationContext(), rView,Utility.SOMETHING_WRONG);
                                }
                            }
                        });

                    }
                },
                new EditButtonClickListener() {
                    @Override
                    public void itemClicked(int position) {

                        //Perform Add Actions
                        Log.d("id",listOfChannels.get(position).getChannelID());
                        new fetchDataAsync(listOfChannels.get(position).getChannelID(),"",true, false).execute();


                        }
                }

        );
        rView.setAdapter(rcAdapter);
        resetRecycerFetchData(0);





        tvAddChannelCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLinearLayoutHome.collapse();
            }
        });



        btAddChannelDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAddChannelURL.getText().toString().trim().isEmpty() ||
                        etAddChannelID.getText().toString().trim().isEmpty()) {
                    Utility.showSnack(getApplicationContext(),btAddChannelDone,Utility.FIELD_EMPTY);

                } else {

                    if(editChannelMode){
                       new fetchDataAsync(etAddChannelID.getText().toString().trim(),etAddChannelURL.getText().toString().trim(),true,true).execute();

                         }else {


                        new fetchDataAsync(etAddChannelID.getText().toString().trim(),etAddChannelURL.getText().toString().trim(), true,false).execute();
                    }
                }
            }
        });

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                Intent i2;

                switch (menuItem.getItemId())
                {

                    case R.id.action_create_channel:

                        i2 = new Intent(HomeActivity.this,CreateChannelActivity.class);
                        startActivity(i2, oc2.toBundle());


                        break;
                    case R.id.action_add_channel:
                        btAddChannelDone.setText("DONE");
                        expandableLinearLayoutHome.expand();
                        break;
                    case R.id.action_settings:
                         i2 = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(i2, oc2.toBundle());

                        break;
                    case R.id.action_sign_out:
                        mAuth.signOut();
                        i2= new Intent(HomeActivity.this,MainActivity.class);
                        i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i2,oc2.toBundle());
                        finish();
                        break;

                }
                return false;
            }
        });

        if(!Settings.getSharedPreference(getApplicationContext(),"notifications").equals("")) {
            if(Boolean.parseBoolean(Settings.getSharedPreference(getApplicationContext(),"notifications"))){
            NotificationEventReceiver.setupAlarm(getApplicationContext());
                Log.d("Service","started");
        }}
    }

    public interface EditButtonClickListener
    {
        void itemClicked(int position);
    }



    private void resetRecycerFetchData(int value) {

        listOfChannels.clear();
        channelAddObjects.clear();
        rcAdapter.notifyDataSetChanged();

        if(value==0){

            Firebase firebaseChild =firebase.child(mAuth.getCurrentUser().getUid()).child("channels");


            firebaseChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);
                    if(multiStateToggleButton.getValue()==0) {
                        channelAddObjects.clear();
                        listOfChannels.clear();
                        rcAdapter.notifyDataSetChanged();
                        for (DataSnapshot channelItem : dataSnapshot.getChildren()) {
                            ChannelAddObject channelAddObject = new ChannelAddObject();
                            channelAddObject.setChannelID(channelItem.child("channelID").getValue().toString());
                            channelAddObject.setChannelURL(channelItem.child("channelURL").getValue().toString());
                            channelAddObject.setChannelPushID(channelItem.getKey());
                            if (!channelAddObjects.contains(channelAddObject)) {
                                channelAddObjects.add(channelAddObject);
                                Log.d("channel", channelAddObject.getChannelID().toString());
                                Log.d("PushKey", channelItem.getKey());
                            }

                        }

                        for (ChannelAddObject channelAddObject : channelAddObjects) {
                            new fetchDataAsync(channelAddObject.getChannelID(), channelAddObject.getChannelURL(), false, false).execute();
                        }
                    }
                    findViewById(R.id.progressIndicator).setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
        else if(value==1){
            new fetchDataAsync("-1","",false,false).execute();
        }else{

        }

    }

    private class fetchDataAsync extends  AsyncTask{

        RestClient restClient;
        String result;
        String channelID;
        int status;
        boolean checkChannelExists = false;
        boolean editModeEnabled = false;
        String channelURL;
        public fetchDataAsync(String channelID,String channelURL,boolean checkChannelExists, boolean editModeEnabled){
            this.channelID = channelID;
            this.checkChannelExists = checkChannelExists;
            this.editModeEnabled = editModeEnabled;
            this.channelURL = channelURL;
        }
        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);

            if(channelID.equals("-1")) {
                restClient = new RestClient(Constants.CHANNEL_BASE_URL + "/public.json");
            }else{
                restClient = new RestClient(Constants.CHANNEL_BASE_URL+"/"+channelID+".json");
            }
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                restClient.executeGet();
                result = restClient.getResponse();
                status = restClient.getCode();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            findViewById(R.id.progressIndicator).setVisibility(View.GONE);

            if(result !=null&&status==200){
                Log.d("result",result);
                try {
                    JSONObject jsonObj = new JSONObject(result);


                    if(channelID.equals("-1")) {
                        JSONArray channels = jsonObj.getJSONArray("channels");
                        for (int i = 0; i < channels.length(); i++) {
                            ChannelObject channel = new ChannelObject();
                            channel.setChannelName(channels.getJSONObject(i).getString("name"));
                            channel.setChannelID(channels.getJSONObject(i).getString("id"));
                            String desc = channels.getJSONObject(i).getString("description");
                            if (desc.length() > 25) {
                                desc = desc.substring(0, 25) + "...";
                            }
                            channel.setChannelDesc(desc);

                            // Map<String,Object> jsonMap = Utility.jsonToMap(channels.getJSONObject(i));
                            Iterator<?> keys = channels.getJSONObject(i).keys();
                            int count = 0;
                            final List<String> excludecase = Arrays.asList("name", "id", "description");

                            while (keys.hasNext()) {

                                String key = (String) keys.next();
                                if (!(channels.getJSONObject(i).get(key) instanceof JSONObject) &&
                                        !(channels.getJSONObject(i).get(key) instanceof JSONArray) &&
                                        (!excludecase.contains(key))) {
                                    String value = channels.getJSONObject(i).getString(key);
                                    switch (count) {
                                        case 0:
                                            channel.setVal1(key + " : " + value);
                                            break;
                                        case 1:
                                            channel.setVal2(key + " : " + value);
                                            break;
                                        case 2:
                                            channel.setVal3(key + " : " + value);
                                            break;
                                        case 3:
                                            channel.setVal4(key + " : " + value);
                                            break;
                                    }

                                    count++;
                                }
                            }
                            if(!listOfChannels.contains(channel))
                            listOfChannels.add(channel);


                        }
                    }else{

                        ChannelObject channel = new ChannelObject();
                        if(jsonObj.has("name"))
                            channel.setChannelName(jsonObj.getString("name"));
                        else
                            channel.setChannelName("N/A");

                        channel.setChannelID(jsonObj.getString("id"));
                        String desc;
                        if(jsonObj.has("description"))
                         desc =jsonObj.getString("description");
                        else
                         desc = "N/A";
                        if (desc.length() > 25) {
                            desc = desc.substring(0, 25) + "...";
                        }
                        channel.setChannelDesc(desc);

                        // Map<String,Object> jsonMap = Utility.jsonToMap(channels.getJSONObject(i));
                        Iterator<?> keys = jsonObj.keys();
                        int count = 0;
                        final List<String> excludecase = Arrays.asList("name", "id", "description");

                        while (keys.hasNext()) {

                            String key = (String) keys.next();
                            if (!(jsonObj.get(key) instanceof JSONObject) &&
                                    !(jsonObj.get(key) instanceof JSONArray) &&
                                    (!excludecase.contains(key))) {
                                String value = jsonObj.getString(key);
                                switch (count) {
                                    case 0:
                                        channel.setVal1(key + " : " + value);
                                        break;
                                    case 1:
                                        channel.setVal2(key + " : " + value);
                                        break;
                                    case 2:
                                        channel.setVal3(key + " : " + value);
                                        break;
                                    case 3:
                                        channel.setVal4(key + " : " + value);
                                        break;
                                }

                                count++;
                            }
                        }
                        if(checkChannelExists){
                            if(editModeEnabled){


                                Firebase firebaseChild =firebase.child(mAuth.getCurrentUser().getUid()).child("channels").child(channelAddObjects.get(itemClickedPosition).getChannelPushID());
                                firebaseChild.child("channelID").setValue(etAddChannelID.getText().toString().trim());
                                firebaseChild.child("channelURL").setValue(etAddChannelURL.getText().toString().trim());

                            }else {
                                ChannelAddObject obj = new ChannelAddObject(channelID, channelURL, "");


                                String pushID = firebase.child(mAuth.getCurrentUser().getUid()).child("channels").push().getKey();
                                    obj.setChannelPushID(pushID);

                                firebase.child(mAuth.getCurrentUser().getUid()).child("channels").child(pushID).setValue(obj);
                            }
                            etAddChannelID.setText("");
                            etAddChannelURL.setText("");
                            expandableLinearLayoutHome.collapse();


                        }else {
                            if(!listOfChannels.contains(channel))
                            listOfChannels.add(channel);
                        }
                    }

                    rcAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                String message= "Something went wrong!";
                if(checkChannelExists){
                    message = "Channel doesn't exist";
                }
                Utility.showSnack(getApplicationContext(),fabSpeedDial,message);
            }
            super.onPostExecute(o);
        }
    }




}
