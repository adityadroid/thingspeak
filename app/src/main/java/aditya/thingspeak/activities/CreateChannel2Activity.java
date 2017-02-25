package aditya.thingspeak.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import aditya.thingspeak.models.ChannelAddObject;
import aditya.thingspeak.utilities.Constants;
import aditya.thingspeak.R;
import aditya.thingspeak.utilities.RestClient;
import aditya.thingspeak.utilities.Utility;

public class CreateChannel2Activity extends AppCompatActivity {

    String name, desc, tags, field1,field2,field3,field4,metadata,url;
    CheckBox isPublicCheckBox;
    EditText etTag, etField1,etField2, etField3,etField4, etMetaData, etUrl;
    FloatingActionButton fab;
    int channelID;
    ExpandableRelativeLayout expandableRelativeLayout;
    TextView idDisplay;
    TextView addChannelButton;
    String channelURL;
    FirebaseAuth mAuth;
    Firebase firebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel2);

        //Enter animation
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        //Initialization
        Firebase.setAndroidContext(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        firebase = new Firebase(Constants.BASE_URL+Constants.USERS_MAP);
        isPublicCheckBox=(CheckBox)findViewById(R.id.create_channel_public_cb);
        etTag= (EditText)findViewById(R.id.cc_tags);
        etMetaData = (EditText)findViewById(R.id.cc_metadata);
        etUrl= (EditText)findViewById(R.id.et_cc_url);
        etField1 = (EditText) findViewById(R.id.cc_field1);
        etField2 = (EditText) findViewById(R.id.cc_field2);
        etField3 = (EditText) findViewById(R.id.cc_field3);
        etField4 = (EditText) findViewById(R.id.cc_field4);
        expandableRelativeLayout = (ExpandableRelativeLayout)findViewById(R.id.expandableLayoutCreateChannel);
        idDisplay = (TextView)findViewById(R.id.create_channel_add_id);
        addChannelButton = (TextView)findViewById(R.id.create_channel_add_channel);

        fab = (FloatingActionButton) findViewById(R.id.fab_create_channel2);


        //Create channel on fab click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tags = etTag.getText().toString().trim();
                metadata = etMetaData.getText().toString().trim();
                url = etUrl.getText().toString().trim();
                field1 = etField1.getText().toString().trim();
                field2= etField2.getText().toString().trim();
                field3 = etField3.getText().toString().trim();
                field4= etField4.getText().toString().trim();
                name = getIntent().getExtras().getString("name");
                desc = getIntent().getExtras().getString("desc");
                new createChannel().execute();
            }
        });


        //Add the channel to current user feed after creation

        addChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChannel();
            }
        });
    }

    public void addChannel(){

        ChannelAddObject obj = new ChannelAddObject(channelID+"", channelURL, "");


        String pushID = firebase.child(mAuth.getCurrentUser().getUid()).child("channels").push().getKey();
        obj.setChannelPushID(pushID);

        firebase.child(mAuth.getCurrentUser().getUid()).child("channels").child(pushID).setValue(obj);
        Utility.showSnack(getApplicationContext(),fab,Utility.DONE);

        //Channel created, launch homeactivity
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }

    public class createChannel extends AsyncTask{

        RestClient restClient;
        String result;
        int code;
        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);
            restClient= new RestClient(Constants.CHANNEL_BASE_URL+".json");
            restClient.addParam("api_key",Constants.API_KEY);
            restClient.addParam("name",name);
            restClient.addParam("desc",desc);
            if(isPublicCheckBox.isChecked()){
                restClient.addParam("public_flag","true");
            }else{
                restClient.addParam("public_flag","false");

            }
            if(!url.isEmpty())restClient.addParam("url",url);
            if(!metadata.isEmpty())restClient.addParam("metadata",metadata);
            if(!tags.isEmpty())restClient.addParam("tags",tags);
            if(!field1.isEmpty())restClient.addParam("field1",field1);
            if(!field2.isEmpty())restClient.addParam("field2",field2);
            if(!field3.isEmpty())restClient.addParam("field3",field3);
            if(!field4.isEmpty())restClient.addParam("field4",field4);


            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                restClient.executePost();

                result = restClient.getResponse();
                code = restClient.getCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            findViewById(R.id.progressIndicator).setVisibility(View.GONE);

            Log.d("result",result.toString());
            if(result!=null&& code==200){

                try {
                    JSONObject jsonObject = new JSONObject(result);
                     channelID = jsonObject.getInt("id");
                    Log.d("id",channelID+"");
                    if(jsonObject.has("website"))
                        channelURL = jsonObject.getString("website");
                    else
                    channelURL="";

                    expandableRelativeLayout.expand();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                Utility.showSnack(getApplicationContext(),fab,Utility.SOMETHING_WRONG);
            }
            super.onPostExecute(o);
        }


    }


}
