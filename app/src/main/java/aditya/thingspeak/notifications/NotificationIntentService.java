package aditya.thingspeak.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import aditya.thingspeak.R;
import aditya.thingspeak.activities.FieldGraphActivity;
import aditya.thingspeak.activities.HomeActivity;
import aditya.thingspeak.models.ChannelAddObject;
import aditya.thingspeak.models.SubscriptionObject;
import aditya.thingspeak.utilities.Constants;
import aditya.thingspeak.utilities.RestClient;
import aditya.thingspeak.utilities.Utility;

/**
 * Created by adi on 2/25/17.
 */
public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    List<SubscriptionObject> subscriptionObjects = new ArrayList<>();

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();

            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {

        // Do something. For example, fetch fresh data from backend to create a rich notification?
        Firebase.setAndroidContext(getApplicationContext());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Firebase firebase = new Firebase(Constants.BASE_URL+Constants.USERS_MAP+"/"+mAuth.getCurrentUser().getUid()+"/subscriptions");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subscriptionObjects.clear();
                for(DataSnapshot subsItem : dataSnapshot.getChildren())
                {
                    SubscriptionObject subscriptionObject = new SubscriptionObject();
                    subscriptionObject.setChannelID(subsItem.child("channelID").getValue().toString());
                    subscriptionObject.setFieldID(subsItem.child("fieldID").getValue().toString());
                    subscriptionObject.setMaxVal(subsItem.child("maxVal").getValue().toString());
                    subscriptionObject.setMinVal(subsItem.child("minVal").getValue().toString());
                    subscriptionObject.setFieldLabel(subsItem.child("fieldLabel").getValue().toString());
                    subscriptionObjects.add(subscriptionObject);


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        for(SubscriptionObject object : subscriptionObjects){
            new fetchNotification(object).execute();
        }



    }
    public void displayNotification(String title, String message,SubscriptionObject object){
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this, FieldGraphActivity.class);
        intent.putExtra("channelid",object.getChannelID());
        intent.putExtra("fieldid",object.getFieldID());
        intent.putExtra("fieldlabel",object.getFieldLabel());
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());

    }
    public  class fetchNotification extends AsyncTask{
        SubscriptionObject subscriptionObject;
        RestClient restClient;
        String result;
        int code;
        public fetchNotification(SubscriptionObject subscriptionObject){
            this.subscriptionObject = subscriptionObject;
        }
        @Override
        protected void onPreExecute() {
            restClient = new RestClient(Constants.CHANNEL_BASE_URL+"/"+subscriptionObject.getChannelID()+"/fields/"+subscriptionObject.getFieldID()+".json?results=1");
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                restClient.executeGet();
                result = restClient.getResponse();
                code = restClient.getCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(result!=null&&code==200){
                Log.d("result",result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String fieldVal = jsonObject.getJSONArray("feeds").getJSONObject(0).getString("field"+subscriptionObject.getFieldID());
                    Log.d("fieldVal",fieldVal);
                    if(Utility.isInteger(fieldVal)){
                    int value = Integer.parseInt(fieldVal);
                    if(value>Integer.parseInt(subscriptionObject.maxVal)|| value<Integer.parseInt(subscriptionObject.minVal)){
                        displayNotification(subscriptionObject.getFieldLabel(),"Field"+subscriptionObject.getFieldID()+" from channel "+subscriptionObject.getChannelID()+" changed to "+fieldVal,subscriptionObject);
                    }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            super.onPostExecute(o);
        }
    }
}