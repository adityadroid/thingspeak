package aditya.thingspeak.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;
import com.firebase.client.Firebase;

import aditya.thingspeak.utilities.Constants;
import aditya.thingspeak.R;
import aditya.thingspeak.utilities.Utility;

public class OTPVerifyActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, VerificationListener {



    private static final String TAG = Verification.class.getSimpleName();
    private Verification mVerification;
    private String userEmail,userPassword,phoneNumber,countryCode;
    Button verifyOTPButton;
    Firebase fireBase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        mAuth = FirebaseAuth.getInstance();

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        Firebase.setAndroidContext(this);
        fireBase = new Firebase(Constants.BASE_URL+Constants.PHONE_EMAIL_MAP);
        verifyOTPButton= (Button)findViewById(R.id.bt_verify_otp);
        initiateVerification();
        registerUserToFireBase();

        verifyOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClicked();
            }
        });

    }

    void createVerification(String phoneNumber, boolean skipPermissionCheck, String countryCode) {
        if (!skipPermissionCheck && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
            hideProgressBar();
        } else {
            mVerification = SendOtpVerification.createSmsVerification(this, phoneNumber, this, countryCode, true);
            mVerification.initiate();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "This application needs permission to read your SMS to automatically verify your "
                        + "phone, you may disable the permission once you have been verified.", Toast.LENGTH_LONG)
                        .show();
            }
        }
        initiateVerificationAndSuppressPermissionCheck();
    }

    void initiateVerification() {
        initiateVerification(false);
    }

    void initiateVerificationAndSuppressPermissionCheck() {
        initiateVerification(true);
    }

    void initiateVerification(boolean skipPermissionCheck) {
        Intent intent = getIntent();
        if (intent != null) {
            phoneNumber = intent.getStringExtra("INTENT_PHONENUMBER");
            countryCode = "91";
            userEmail= intent.getExtras().getString("INTENT_EMAIL");
            userPassword= intent.getExtras().getString("INTENT_PASSWORD");

            Log.d("number:",phoneNumber);

              //  createVerification(phoneNumber, skipPermissionCheck, countryCode);
        }
    }


    public void onSubmitClicked() {
        String code = ((EditText) findViewById(R.id.et_otp)).getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);
                showProgress();
                TextView messageText = (TextView) findViewById(R.id.verify_otp_header);
                messageText.setText("Verification in progress");
            }
        }
    }


    void hideProgressBarAndShowMessage(int message) {
        hideProgressBar();
        TextView messageText = (TextView) findViewById(R.id.verify_otp_header);
        messageText.setText(message);
    }

    void hideProgressBar() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.INVISIBLE);
    }

    void showProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.VISIBLE);
    }

    void showCompleted() {
        Toast.makeText(OTPVerifyActivity.this, "Verified!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitiated(String response) {
        Log.d(TAG, "Initialized!" + response);
    }

    @Override
    public void onInitiationFailed(Exception exception) {
        Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
        hideProgressBarAndShowMessage(R.string.failed);
    }

    @Override
    public void onVerified(String response) {
        Log.d(TAG, "Verified!\n" + response);
        hideProgressBarAndShowMessage(R.string.verified);
        showCompleted();
        registerUserToFireBase();
    }

    private void registerUserToFireBase() {

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            //Map the user email and phone number

                            fireBase.child(Utility.encodeEmail(userEmail)).setValue(phoneNumber);
                            Toast.makeText(OTPVerifyActivity.this, "Pushed to firebase!", Toast.LENGTH_SHORT).show();


                        }else{
                            Snackbar.make(verifyOTPButton,"Error!",Snackbar.LENGTH_SHORT).show();
                        }


                    }
                });




    }

    @Override
    public void onVerificationFailed(Exception exception) {
        Log.e(TAG, "Verification failed: " + exception.getMessage());
        hideProgressBarAndShowMessage(R.string.failed);
    }



    }