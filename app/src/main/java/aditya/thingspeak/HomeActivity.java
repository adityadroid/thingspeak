package aditya.thingspeak;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class HomeActivity extends AppCompatActivity {

    FabSpeedDial fabSpeedDial;
    private GridLayoutManager lLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);



        List<ChannelObject> rowListItem = getAllItemList();
        lLayout = new GridLayoutManager(HomeActivity.this, 2);

        RecyclerView rView = (RecyclerView)findViewById(R.id.channel_recycler);
        //rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(HomeActivity.this, rowListItem);
        rView.setAdapter(rcAdapter);








        fabSpeedDial = (FabSpeedDial)findViewById(R.id.fab_speed_dial);

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                switch (menuItem.getItemId())
                {
                    case R.id.action_add_channel:
//                        mFirstDemoActSwitchAnimTool = new ActSwitchAnimTool(getActivity()).setAnimType(ActSwitchAnimTool.MODE_SPREAD)
//                                .target(view)
//                                .setShrinkBack(true)
//                                .setmColorStart(getResources().getColor(R.color.transition_start))
//                                .setmColorEnd(getResources().getColor(R.color.colorPrimary))
//                                .startActivity(intent, false);
//
//
//                        mFirstDemoActSwitchAnimTool.setAnimType(ActSwitchAnimTool.MODE_SPREAD)
//                                .build();
                        Explode explode = new Explode();
                        explode.setDuration(500);

                        getWindow().setExitTransition(explode);
                        getWindow().setEnterTransition(explode);
                        ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                        Intent i2 = new Intent(HomeActivity.this,CreateChannelActivity.class);
                        startActivity(i2, oc2.toBundle());


                        break;
                    case R.id.action_settings:

                        break;

                }
                return false;
            }
        });

    }


    private List<ChannelObject> getAllItemList(){


        List<ChannelObject> allItems = new ArrayList<ChannelObject>();
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));
        allItems.add(new ChannelObject("United States", "this is description","val","val","val","val"));

        return allItems;
    }
    public class getUserChannelsAsync extends AsyncTask{
        String result;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public getUserChannelsAsync(List<String> channelList){

        }
        @Override
        protected Object doInBackground(Object[] params) {
            RestClient restClient= new RestClient(Constants.CHANNEL_BASE_URL+"1417"+".json");
            restClient.addParam("api_key",Constants.API_KEY);
            try {
                restClient.executeGet();
                String result = restClient.getResponse();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d("Res",result);
            super.onPostExecute(o);
        }
    }
}
