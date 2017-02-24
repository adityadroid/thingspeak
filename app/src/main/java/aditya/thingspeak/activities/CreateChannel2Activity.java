package aditya.thingspeak.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import aditya.thingspeak.utilities.Constants;
import aditya.thingspeak.R;
import aditya.thingspeak.utilities.RestClient;

public class CreateChannel2Activity extends AppCompatActivity {

    String name, desc, tags, field1,field2,field3,field4,metadata,url;
    CheckBox isPublicCheckBox;
    EditText etTag, etField1,etField2, etField3,etField4, etMetaData, etUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel2);
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        isPublicCheckBox=(CheckBox)findViewById(R.id.create_channel_public_cb);
        etTag= (EditText)findViewById(R.id.cc_tags);
        etMetaData = (EditText)findViewById(R.id.cc_metadata);
        etUrl= (EditText)findViewById(R.id.et_cc_url);
        etField1 = (EditText) findViewById(R.id.cc_field1);
        etField2 = (EditText) findViewById(R.id.cc_field2);
        etField3 = (EditText) findViewById(R.id.cc_field3);
        etField4 = (EditText) findViewById(R.id.cc_field4);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create_channel2);
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
    }

    public class createChannel extends AsyncTask{

        RestClient restClient;
        String result;
        @Override
        protected void onPreExecute() {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            Log.d("result",result.toString());
            super.onPostExecute(o);
        }


    }


}
