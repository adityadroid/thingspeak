package aditya.thingspeak.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    //Fields form the add channel section
    EditText etAddChannelID,etAddChannelURL;
    Button btAddChannelDone;
    TextView tvAddChannelCancel;
    ExpandableRelativeLayout expandableLinearLayoutHome;

    //Toggle button to switch to public or my channels
    MultiStateToggleButton multiStateToggleButton;

    //Firebase variables
    Firebase firebase;
    FirebaseAuth mAuth;

    //List of all the channels to display
    List<ChannelObject> listOfChannels = new ArrayList<ChannelObject>();


    RecyclerViewAdapter rcAdapter;

    //ChannelAddobjects are channelID details of all the user's channels from firebase database
    List<ChannelAddObject> channelAddObjects = new ArrayList<>();

    // variable to switch the explandablelinearlayout field from add channel to edit channel mode
    boolean editChannelMode = false;

    //Position of the item clicked in the recyler
    int itemClickedPosition;

    //vvariable to detect whether the user is on the public channel or personal channel
    public static boolean publicFeed = false;
    RecyclerView rView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Enter animation
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        //Initialization
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



        //Setting toggle to channel details by default
        multiStateToggleButton.setValue(0);


        //adding listener to toggle switch
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






        //Setting up recylerview
        lLayout = new GridLayoutManager(HomeActivity.this, 2);

        rView.setLayoutManager(lLayout);
        rcAdapter = new RecyclerViewAdapter(HomeActivity.this, listOfChannels, new EditButtonClickListener() {
            @Override
            public void itemClicked(int position) {

                //Perform Edit button actions
                expandableLinearLayoutHome.expand();
                editChannelMode = true;
                itemClickedPosition = position;
                btAddChannelDone.setText("MAKE CHANGES");


            }
        },
                new EditButtonClickListener() {
                    @Override
                    public void itemClicked(int position) {

                        //Perform Delete button Actions
                        deleteChild(position);

                    }
                },
                new EditButtonClickListener() {
                    @Override
                    public void itemClicked(int position) {

                        //Perform Add button Actions
                        Log.d("id",listOfChannels.get(position).getChannelID());
                        new fetchDataAsync(listOfChannels.get(position).getChannelID(),"",true, false).execute();


                        }
                }

        );
        rView.setAdapter(rcAdapter);


        //Start fetching channel details. 0 here implies that the default feed is from the user's channel
        resetRecycerFetchData(0);





        //Cancel adding of channel
        tvAddChannelCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLinearLayoutHome.collapse();
            }
        });


        // Check the existence of channel and add it to the user's channel database
        btAddChannelDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (etAddChannelURL.getText().toString().trim().isEmpty() ||
                        etAddChannelID.getText().toString().trim().isEmpty()) {
                    Utility.showSnack(getApplicationContext(),btAddChannelDone,Utility.FIELD_EMPTY);

                } else {

                    if(editChannelMode){
                        //if edit mode is enabled edit the channel
                       new fetchDataAsync(etAddChannelID.getText().toString().trim(),etAddChannelURL.getText().toString().trim(),true,true).execute();

                         }else {

                            //else add a new channel
                        new fetchDataAsync(etAddChannelID.getText().toString().trim(),etAddChannelURL.getText().toString().trim(), true,false).execute();
                    }
                }
            }
        });


        //Menu items on fabspeeddial

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat activityOptionsCompat= ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                Intent intent;

                switch (menuItem.getItemId())
                {

                    case R.id.action_create_channel:

                        intent = new Intent(HomeActivity.this,CreateChannelActivity.class);
                        startActivity(intent, activityOptionsCompat.toBundle());


                        break;
                    case R.id.action_add_channel:
                        btAddChannelDone.setText("DONE");
                        expandableLinearLayoutHome.expand();
                        break;
                    case R.id.action_settings:
                         intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(intent, activityOptionsCompat.toBundle());

                        break;
                    case R.id.action_sign_out:
                        mAuth.signOut();
                        intent= new Intent(HomeActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent,activityOptionsCompat.toBundle());
                        finish();
                        break;

                }
                return false;
            }
        });


        //Start the notification service if notifications are enabled
        if(!Settings.getSharedPreference(getApplicationContext(),"notifications").equals("")) {
            if(Boolean.parseBoolean(Settings.getSharedPreference(getApplicationContext(),"notifications"))){
            NotificationEventReceiver.setupAlarm(getApplicationContext());
                Log.d("Service","started");
        }}
    }

    private void deleteChild(final int position) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this channel?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebase.child(mAuth.getCurrentUser().getUid()).child("channels").child(channelAddObjects.get(position).getChannelPushID()).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {

                            Utility.showSnack(getApplicationContext(), rView,Utility.SOMETHING_WRONG);
                        }
                    }
                });
            }
        });
        builder.show();



    }

    public interface EditButtonClickListener
    {
        void itemClicked(int position);
    }



    private void resetRecycerFetchData(int value) {

        listOfChannels.clear();
        channelAddObjects.clear();
        rcAdapter.notifyDataSetChanged();


        //if value =0 fetch user's personal channels
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

        //else fetch public channels
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

        // variable to switch the mode to checking of channel
        boolean checkChannelExists = false;

        // variable to swtich the mode to editing of channel
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


            // -1 is the condition when public channel is selected

            if(channelID.equals("-1")) {
                findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);
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

            if(result !=null&&status==200){
                Log.d("result",result);
                try {
                    JSONObject jsonObj = new JSONObject(result);


                    //Case when public feed is requested
                    if(channelID.equals("-1")) {
                        fetchDataPublicCase(jsonObj);
                    }
                    ///Other cases
                    else{
                        fetchDataPrivateCase(jsonObj, checkChannelExists, editModeEnabled, channelID, channelURL);
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


    private void fetchDataPublicCase(JSONObject jsonObj) throws JSONException {

        findViewById(R.id.progressIndicator).setVisibility(View.GONE);
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
    }


    private void fetchDataPrivateCase(JSONObject jsonObj,boolean checkChannelExists, boolean editModeEnabled, String channelID, String channelURL) throws JSONException {


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

        //if channel checking mode is enabled
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
            Utility.showSnack(getApplicationContext(),fabSpeedDial,Utility.DONE);


        }
        //otherwise directly add to list of channels
        else {
            if(!listOfChannels.contains(channel))
                listOfChannels.add(channel);
        }

    }


}
