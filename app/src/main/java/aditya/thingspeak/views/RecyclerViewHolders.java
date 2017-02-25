package aditya.thingspeak.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.activities.ChannelDetailsActivity;
import aditya.thingspeak.activities.HomeActivity;
import aditya.thingspeak.models.ChannelObject;

/**
 * Created by adi on 2/23/17.
 */

public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView channelName;
    public TextView channelDesc;
    public TextView val1,val2,val3,val4;
    public TextView editButton, deleteButton,addButton;
    Context context;
    private List<ChannelObject> itemList;
    LinearLayout publicBtLayout, myFeedBtLayout;

    public RecyclerViewHolders(View itemView, Context context,List<ChannelObject> itemList) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.context= context;
        this.itemList = itemList;
        channelName = (TextView)itemView.findViewById(R.id.item_channel_name);
        channelDesc = (TextView)itemView.findViewById(R.id.item_channel_details);
        val1= (TextView)itemView.findViewById(R.id.item_channel_val1);
        val2= (TextView)itemView.findViewById(R.id.item_channel_val2);
        val3= (TextView)itemView.findViewById(R.id.item_channel_val3);
        val4 = (TextView)itemView.findViewById(R.id.item_channel_val4);
        editButton= (TextView)itemView.findViewById(R.id.item_channel_edit_bt);
        deleteButton=(TextView)itemView.findViewById(R.id.item_channel_delete_bt);
        addButton = (TextView)itemView.findViewById(R.id.item_channel_add_bt);
        publicBtLayout= (LinearLayout) itemView.findViewById(R.id.public_feed_bt_layout);
        myFeedBtLayout=(LinearLayout)itemView.findViewById(R.id.my_feed_bt_layout);


    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, ChannelDetailsActivity.class);
        intent.putExtra("channelid",itemList.get(getPosition()).getChannelID());
        context.startActivity(intent);
    }
}
