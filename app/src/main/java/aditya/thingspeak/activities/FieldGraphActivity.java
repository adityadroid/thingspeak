package aditya.thingspeak.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import aditya.thingspeak.R;

public class FieldGraphActivity extends AppCompatActivity {

    WebView webView;
    String channelID;
    String fieldID;
    TextView fieldDetailsTV;
    String fieldLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_graph);

        channelID = getIntent().getExtras().getString("channelid");
        fieldID = getIntent().getExtras().getString("fieldid");
        fieldLabel = getIntent().getExtras().getString("fieldlabel");
        webView = (WebView)findViewById(R.id.webview);
        fieldDetailsTV= (TextView)findViewById(R.id.attr_details_text);
        fieldDetailsTV.setText(fieldLabel.toUpperCase());
        webView.getSettings().setJavaScriptEnabled(true);
        String html = "<iframe width=\"300\" height=\"300\" style=\"border: 1px solid #cccccc;\" src=\"http://api.thingspeak.com/channels/"+channelID+"/charts/"+fieldID+"?width=300&height=300&results=60&dynamic=true\" ></iframe>";
        Log.d("url",html);
        webView.loadData(html, "text/html", null);


        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();

            }
        });
    }
}
