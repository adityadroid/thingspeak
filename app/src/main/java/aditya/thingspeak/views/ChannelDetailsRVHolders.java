package aditya.thingspeak.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.activities.FieldGraphActivity;
import aditya.thingspeak.activities.HomeActivity;
import aditya.thingspeak.models.ChannelAttribute;

/**
 * Created by adi on 2/24/17.
 */
public class ChannelDetailsRVHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView channelKey;
    public TextView channelValue;
    Context context;
    List<ChannelAttribute> channelAttributes;
    String channelID;

    public ChannelDetailsRVHolders(View itemView,Context context, List<ChannelAttribute> channelAttributes,String channelID) {
        super(itemView);
        itemView.setOnClickListener(this);
        channelKey= (TextView)itemView.findViewById(R.id.channel_detail_key);
        channelValue = (TextView)itemView.findViewById(R.id.channel_detail_value);

        this.context = context;
        this.channelAttributes = channelAttributes;
        this.channelID = channelID;

    }

    @Override
    public void onClick(View view) {
        String key = channelAttributes.get(getPosition()).getKey();
        if(key.contains("field")){
            Intent intent = new Intent(context, FieldGraphActivity.class);
            Log.d("key",key.substring(5,key.length())+" "+key);
            Log.d("channelid",channelID);
            intent.putExtra("fieldid",key.substring(5,key.length()));
            intent.putExtra("channelid",channelID);
            intent.putExtra("fieldlabel",channelAttributes.get(getPosition()).getValue());
            context.startActivity(intent);

        }

    }
}
