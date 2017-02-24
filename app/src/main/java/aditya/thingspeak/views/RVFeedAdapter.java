package aditya.thingspeak.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.models.FeedObject;

/**
 * Created by adi on 2/24/17.
 */
public class RVFeedAdapter extends RecyclerView.Adapter<RVFeedHolders> {

        List<FeedObject> feedObjectList;
private Context context;

public RVFeedAdapter(Context context,List<FeedObject> feedObjectList) {
        this.feedObjectList=feedObjectList;
        this.context = context;
        }

@Override
public RVFeedHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, null);
        RVFeedHolders rcv = new RVFeedHolders(layoutView, context);
        return rcv;
        }

@Override
public void onBindViewHolder(RVFeedHolders holder, final int position) {

        holder.feedHead.setText("ID: "+feedObjectList.get(position).getFeedHeading());
        holder.feedTime.setText(feedObjectList.get(position).getFeedTime());
        holder.feedContent.setText(feedObjectList.get(position).getFeedContent());

        }

@Override
public int getItemCount() {
        return this.feedObjectList.size();
        }
        }

