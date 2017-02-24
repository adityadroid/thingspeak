package aditya.thingspeak.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.activities.FieldGraphActivity;
import aditya.thingspeak.models.ChannelAttribute;

/**
 * Created by adi on 2/24/17.
 */
public class RVFeedHolders extends RecyclerView.ViewHolder{

    public TextView feedHead;
    public TextView feedTime;
    public TextView feedContent;
    Context context;

    public RVFeedHolders(View itemView,Context context) {
        super(itemView);
        feedHead= (TextView)itemView.findViewById(R.id.feed_heading);
        feedContent = (TextView)itemView.findViewById(R.id.feed_values);
        feedTime= (TextView)itemView.findViewById(R.id.feed_time);
        this.context = context;

    }


}
