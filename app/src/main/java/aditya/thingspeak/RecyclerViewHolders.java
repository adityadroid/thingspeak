package aditya.thingspeak;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by adi on 2/23/17.
 */

public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView channelName;
    public TextView channelDesc;
    public TextView val1,val2,val3,val4;
    public TextView editButton, deleteButton;

    public RecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        channelName = (TextView)itemView.findViewById(R.id.item_channel_name);
        channelDesc = (TextView)itemView.findViewById(R.id.item_channel_details);
        val1= (TextView)itemView.findViewById(R.id.item_channel_val1);
        val2= (TextView)itemView.findViewById(R.id.item_channel_val2);
        val3= (TextView)itemView.findViewById(R.id.item_channel_val3);
        val4 = (TextView)itemView.findViewById(R.id.item_channel_val4);
        editButton= (TextView)itemView.findViewById(R.id.item_channel_edit_bt);
        deleteButton=(TextView)itemView.findViewById(R.id.item_channel_delete_bt);


    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition(), Toast.LENGTH_SHORT).show();
    }
}
