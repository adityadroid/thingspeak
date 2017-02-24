package aditya.thingspeak.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import aditya.thingspeak.R;

public class CreateChannelActivity extends AppCompatActivity {

    Button next;
    EditText etName,etDesc;
    Button btNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_create_channel);
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        etName = (EditText)findViewById(R.id.cc_name);
        etDesc= (EditText)findViewById(R.id.cc_desc);
        next = (Button)findViewById(R.id.cc_next_bt);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {
                    Explode explode = new Explode();
                    explode.setDuration(500);

                    getWindow().setExitTransition(explode);
                    getWindow().setEnterTransition(explode);
                    ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(CreateChannelActivity.this);
                    Intent i2 = new Intent(CreateChannelActivity.this, CreateChannel2Activity.class);
                    i2.putExtra("name",etName.getText().toString().trim());
                    i2.putExtra("desc",etDesc.getText().toString().trim());
                    startActivity(i2, oc2.toBundle());
                }
            }
        });

    }

    public boolean validateData(){
        if(etName.getText().toString().trim().isEmpty()||etDesc.getText().toString().trim().isEmpty()){
            return false;
        }
        return true;
    }


}
