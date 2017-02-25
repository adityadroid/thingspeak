package aditya.thingspeak.activities;

import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import aditya.thingspeak.R;
import aditya.thingspeak.models.SubscriptionObject;
import aditya.thingspeak.utilities.Constants;

public class FieldGraphActivity extends AppCompatActivity {

    WebView webView;
    String channelID;
    String fieldID;
    TextView fieldDetailsTV;
    String fieldLabel;
    Firebase firebase;
    FirebaseAuth mAuth;
    TextView subscribeButton;
    EditText etMinVal;
    EditText etMaxVal;
    ExpandableLinearLayout expandableLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_graph);

        channelID = getIntent().getExtras().getString("channelid");
        fieldID = getIntent().getExtras().getString("fieldid");
        fieldLabel = getIntent().getExtras().getString("fieldlabel");
        webView = (WebView)findViewById(R.id.webview);
        fieldDetailsTV= (TextView)findViewById(R.id.attr_details_text);
        subscribeButton= (TextView)findViewById(R.id.subs_button);
        etMinVal = (EditText)findViewById(R.id.subs_field_min_val);
        etMaxVal = (EditText)findViewById(R.id.subs_field_max_val);
        expandableLinearLayout = (ExpandableLinearLayout)findViewById(R.id.subs_expandable_layout);

        fieldDetailsTV.setText(fieldLabel.toUpperCase());
        webView.getSettings().setJavaScriptEnabled(true);
        String html = "<iframe width=\"300\" height=\"300\" style=\"border: 1px solid #cccccc;\" src=\"http://api.thingspeak.com/channels/"+channelID+"/charts/"+fieldID+"?width=300&height=300&results=60&dynamic=true\" ></iframe>";
        Log.d("url",html);
        webView.setWebViewClient(new webClient());
        webView.loadData(html, "text/html", null);

        Firebase.setAndroidContext(getApplicationContext());
        firebase = new Firebase(Constants.BASE_URL+Constants.USERS_MAP);
        mAuth = FirebaseAuth.getInstance();



        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);
                if(!expandableLinearLayout.isExpanded())
                    expandableLinearLayout.expand();
                else{
                    if(etMaxVal.getText().toString().isEmpty()||etMinVal.getText().toString().isEmpty()){
                        Snackbar.make(subscribeButton,"One or more fields empty!",Snackbar.LENGTH_SHORT).show();
                    }else{

                        SubscriptionObject subscriptionObject = new SubscriptionObject();
                        subscriptionObject.setFieldID(fieldID);
                        subscriptionObject.setFieldLabel(fieldLabel);
                        subscriptionObject.setChannelID(channelID);
                        subscriptionObject.setMaxVal(etMaxVal.getText().toString());
                        subscriptionObject.setMinVal(etMinVal.getText().toString());
                        String pushID= firebase.child(mAuth.getCurrentUser().getUid()).child("subscriptions").push().getKey();
                        subscriptionObject.setPushID(pushID);
                        firebase.child(mAuth.getCurrentUser().getUid()).child("subscriptions").child(pushID).setValue(subscriptionObject);

                        etMaxVal.setText("");
                        etMinVal.setText("");

                        expandableLinearLayout.collapse();
                    }

                }
                findViewById(R.id.progressIndicator).setVisibility(View.GONE);

            }

        });
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();

            }
        });
    }

    public class webClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            findViewById(R.id.progressIndicator).setVisibility(View.GONE);

            super.onPageFinished(view, url);
        }
    }
}
