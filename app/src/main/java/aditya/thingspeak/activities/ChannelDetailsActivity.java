package aditya.thingspeak.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.models.ChannelAttribute;
import aditya.thingspeak.models.FeedObject;
import aditya.thingspeak.utilities.Constants;
import aditya.thingspeak.utilities.RestClient;
import aditya.thingspeak.views.ChannelDetailsRVAdapter;
import aditya.thingspeak.views.RVFeedAdapter;

public class ChannelDetailsActivity extends AppCompatActivity {

    RecyclerView channelDetailsRecycler;
    ChannelDetailsRVAdapter adapter;
    RVFeedAdapter feedAdapter;
    GridLayoutManager gLayout;
    List<ChannelAttribute> listOfAttributes = new ArrayList<>();
    List<FeedObject> listOfFeeds = new ArrayList<>();
    MultiStateToggleButton multiStateToggleButton;
    LinearLayoutManager lLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_details);

        String channelID = getIntent().getExtras().getString("channelid");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        multiStateToggleButton = (MultiStateToggleButton)findViewById(R.id.channel_details_toggle);
        channelDetailsRecycler = (RecyclerView)findViewById(R.id.channel_details_recycler);
        gLayout = new GridLayoutManager(ChannelDetailsActivity.this, 2);
        lLayout= new LinearLayoutManager(ChannelDetailsActivity.this);
        lLayout.setOrientation(LinearLayoutManager.VERTICAL);
        channelDetailsRecycler.setLayoutManager(gLayout);
        adapter = new ChannelDetailsRVAdapter(ChannelDetailsActivity.this,listOfAttributes,channelID);
        channelDetailsRecycler.setAdapter(adapter);
        feedAdapter = new RVFeedAdapter(ChannelDetailsActivity.this,listOfFeeds);

        new fetchChannelUpdatesAsync(channelID).execute();

        multiStateToggleButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                if(value==0){
                    channelDetailsRecycler.setLayoutManager(gLayout);
                    channelDetailsRecycler.setAdapter(adapter);
                }else{
                    channelDetailsRecycler.setLayoutManager(lLayout);
                    channelDetailsRecycler.setAdapter(feedAdapter);

                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public class fetchChannelUpdatesAsync extends AsyncTask{
        RestClient restClient;
        String result;
        int code;
        String channelID;
        public fetchChannelUpdatesAsync(String channelID){
            this.channelID = channelID;
        }
        @Override
        protected void onPreExecute() {
            restClient = new RestClient(Constants.CHANNEL_BASE_URL+"/"+channelID+"/feeds.json");
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                restClient.executeGet();
                result= restClient.getResponse();
                code = restClient.getCode();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            if(result!=null&& code ==200){
                try {
                    Log.d("result",result);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject channelObj = jsonObject.getJSONObject("channel");

                    Iterator<?> keys = channelObj.keys();

                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        if ( !(channelObj.get(key) instanceof JSONObject)&& !(channelObj.get(key) instanceof JSONArray) ) {
                            String value= channelObj.getString(key);
                            ChannelAttribute channelAttribute = new ChannelAttribute();
                            channelAttribute.setKey(key);
                            channelAttribute.setValue(value);
                            listOfAttributes.add(channelAttribute);

                        }
                    }

                    adapter.notifyDataSetChanged();

                    JSONArray feedObj = jsonObject.getJSONArray("feeds");
                   for(int i=0; i<feedObj.length();i++){
                       JSONObject feed = feedObj.getJSONObject(i);
                       FeedObject feedObject = new FeedObject();
                       Log.d("obj",feed.toString());
                       feedObject.setFeedHeading(String.valueOf(feed.getInt("entry_id")));
                       feedObject.setFeedTime(feed.getString("created_at"));

                        keys = feed.keys();
                       String content = "";

                       while( keys.hasNext() ) {
                           String key = (String)keys.next();
                           if ( !(feed.get(key) instanceof JSONObject)&& !(feed.get(key) instanceof JSONArray) && key.contains("field")) {
                               String value= feed.getString(key);
                               content = content + key+"    :   "+value+"\n";

                           }
                       }
                       feedObject.setFeedContent(content);
                       listOfFeeds.add(feedObject);



                   }
                    feedAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{
                Snackbar.make(channelDetailsRecycler,"Something went wrong!",Snackbar.LENGTH_SHORT).show();
            }
            super.onPostExecute(o);
        }

    }

}
