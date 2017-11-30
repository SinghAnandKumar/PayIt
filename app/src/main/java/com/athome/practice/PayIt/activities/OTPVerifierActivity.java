package com.athome.practice.PayIt.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.athome.practice.PayIt.R;

public class OTPVerifierActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "PayItPrefsFile";

    private EditText otpEntered;
    private Button verifyOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverifier);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String otp = sharedPreferences.getString("otp", "-1");

        otpEntered = (EditText) findViewById(R.id.otp);
        verifyOTP = (Button) findViewById(R.id.verifyOTP);

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpEnteredVal = otpEntered.getText().toString();
                if(otpEnteredVal.length() < 4) {
                    Toast.makeText(getApplicationContext(), "Enter 4 digit OTP...", Toast.LENGTH_SHORT);
                    return;
                } else if(!otpEnteredVal.equals(otp)) {
                    Toast.makeText(getApplicationContext(), "Enter valid OTP...." + otp, Toast.LENGTH_SHORT);
                    return;
                }

                Intent nextActivity = new Intent(OTPVerifierActivity.this, HomePage.class);
                startActivity(nextActivity);
            }
        });
    }
}
