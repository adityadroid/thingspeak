package aditya.thingspeak;

/**
 * Created by adi on 2/23/17.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private List<ChannelObject> itemList;
    private Context context;

    public RecyclerViewAdapter(Context context, List<ChannelObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_list_item, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.channelName.setText(itemList.get(position).getChannelName());
        holder.channelDesc.setText(itemList.get(position).getChannelDesc());
        holder.val1.setText(itemList.get(position).getVal1());
        holder.val2.setText(itemList.get(position).getVal2());
        holder.val3.setText(itemList.get(position).getVal3());
        holder.val4.setText(itemList.get(position).getVal4());
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}

