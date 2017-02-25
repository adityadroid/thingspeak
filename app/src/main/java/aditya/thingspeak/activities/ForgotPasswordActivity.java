package aditya.thingspeak.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import aditya.thingspeak.R;
import aditya.thingspeak.utilities.Utility;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btResetPwd;
    FirebaseAuth auth;
    String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        auth = FirebaseAuth.getInstance();

        etEmail=(EditText)findViewById(R.id.et_email);
        btResetPwd= (Button)findViewById(R.id.bt_reset_pwd);
        btResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);

                if(etEmail.getText().toString().isEmpty()){

                    Utility.showSnack(getApplicationContext(),btResetPwd,Utility.FIELD_EMPTY);
                }else{
                    emailAddress=etEmail.getText().toString();
                      auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    findViewById(R.id.progressIndicator).setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Log.d("SENT", "Email sent.");
                                        Utility.showSnackLong(getApplicationContext(),btResetPwd,"A reset link has been sent to your mail.");
                                    }
                                    else{
                                        Utility.showSnack(getApplicationContext(),btResetPwd,Utility.SOMETHING_WRONG);

                                    }
                                }
                            });
                }
            }
        });

    }
}
