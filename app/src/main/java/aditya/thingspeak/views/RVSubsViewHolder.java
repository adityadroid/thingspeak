package aditya.thingspeak.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import aditya.thingspeak.R;

/**
 * Created by adi on 2/25/17.
 */
public class RVSubsViewHolder extends RecyclerView.ViewHolder{

    public TextView subHead;
    public TextView deleteBt;
    public TextView subChannelID;
    Context context;

    public RVSubsViewHolder(View itemView, Context context) {
        super(itemView);
        subHead= (TextView)itemView.findViewById(R.id.setting_subs_heading);
        subChannelID = (TextView)itemView.findViewById(R.id.settings_subs_channel_id);
        deleteBt= (TextView)itemView.findViewById(R.id.settings_delete_sub);
        this.context = context;

    }


}
