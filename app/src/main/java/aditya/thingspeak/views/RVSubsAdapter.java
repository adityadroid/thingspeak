package aditya.thingspeak.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.activities.SettingsActivity;
import aditya.thingspeak.models.SubscriptionObject;

/**
 * Created by adi on 2/25/17.
 */
public class RVSubsAdapter extends RecyclerView.Adapter<RVSubsViewHolder> {

        List<SubscriptionObject> subscriptionlist;
private Context context;
    SettingsActivity.ClickListener listener;

public RVSubsAdapter(Context context, List<SubscriptionObject> subscriptionlist, SettingsActivity.ClickListener listener) {
        this.subscriptionlist=subscriptionlist;
        this.context = context;
        this.listener = listener;
        }

@Override
public RVSubsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_item, null);
        RVSubsViewHolder rcv = new RVSubsViewHolder(layoutView, context);
        return rcv;
        }

@Override
public void onBindViewHolder(RVSubsViewHolder holder, final int position) {

        holder.subChannelID.setText("ID: "+subscriptionlist.get(position).getChannelID());
        holder.subHead.setText(subscriptionlist.get(position).getFieldLabel());
        holder.deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position);
            }
        });

        }

@Override
public int getItemCount() {
        return this.subscriptionlist.size();
        }
        }

