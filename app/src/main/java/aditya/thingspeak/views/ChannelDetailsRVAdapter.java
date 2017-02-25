package aditya.thingspeak.views;

/**
 * Created by adi on 2/24/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.models.ChannelAttribute;

public class ChannelDetailsRVAdapter extends RecyclerView.Adapter<ChannelDetailsRVHolders> {

    List<ChannelAttribute> channelAttributeList;
    private Context context;
    String channelID;

    public ChannelDetailsRVAdapter(Context context,List<ChannelAttribute> channelAttributeList,String channelID) {
        this.channelAttributeList = channelAttributeList;
        this.context = context;
        this.channelID= channelID;
       }

    @Override
    public ChannelDetailsRVHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_detail_item, null);
        ChannelDetailsRVHolders rcv = new ChannelDetailsRVHolders(layoutView, context, channelAttributeList,channelID);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ChannelDetailsRVHolders holder, final int position) {
        holder.channelKey.setText(channelAttributeList.get(position).getKey()+":");
        holder.channelValue.setText(channelAttributeList.get(position).getValue());

        if(channelAttributeList.get(position).getKey().equals("description"))
        {
            holder.channelValue.setTextSize(20);

        }


    }

    @Override
    public int getItemCount() {
        return this.channelAttributeList.size();
    }
}

