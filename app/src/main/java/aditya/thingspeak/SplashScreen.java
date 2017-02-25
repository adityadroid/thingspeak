package aditya.thingspeak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import aditya.thingspeak.activities.HomeActivity;
import aditya.thingspeak.activities.MainActivity;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        Intent intent;
        if(mAuth.getCurrentUser()==null){
            intent = new Intent(SplashScreen.this, MainActivity.class);
        }else{
            intent = new Intent(SplashScreen.this, HomeActivity.class);


        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
