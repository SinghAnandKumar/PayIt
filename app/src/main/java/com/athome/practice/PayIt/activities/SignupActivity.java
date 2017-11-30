package com.athome.practice.PayIt.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.athome.practice.PayIt.R;
import com.athome.practice.PayIt.Constants;

import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText userName;
    private EditText phoneNumber;
    private EditText emailId;
    private Spinner bankName;
    private Button btnSignup;
    private TextView login;

    private boolean otpStatus = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = (EditText) findViewById(R.id.fName);
        lastName = (EditText) findViewById(R.id.lName);
        userName = (EditText) findViewById(R.id.uName);
        phoneNumber = (EditText) findViewById(R.id.phone);
        bankName = (Spinner) findViewById(R.id.bankName);
        emailId = (EditText) findViewById(R.id.emailId);
        btnSignup = (Button) findViewById(R.id.submit);
        login = (TextView) findViewById(R.id.login_page_link);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstNameVal = firstName.getText().toString();
                String lastNameVal = lastName.getText().toString();
                String userNameVal = userName.getText().toString();
                String phoneNumberVal = phoneNumber.getText().toString();
                String bankNameVal = bankName.getSelectedItem().toString();
                String emailIdVal = emailId.getText().toString();

                //call endpoints with this data and collect the response
                String values = firstNameVal + " " + lastNameVal + " " + userNameVal + " " + phoneNumberVal + " " + bankNameVal + " " + emailIdVal;
                Log.d("values", values);

                otpStatus = sendOTP(phoneNumberVal);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean sendOTP(String phoneNumber) {
        //Generate 4 digit OTP
        Random random = new Random();
        final String otp = String.format("%04d", random.nextInt(10000));

        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        otpStatus = true;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        otpStatus = false;
                        break;
                }

                if(otpStatus) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME,0);
                    sharedPreferences.edit().putString(Constants.OTP, otp).apply();
                    sharedPreferences.edit().putString(Constants.USERNAME,userName.getText().toString()).apply();
                    Intent nextActivity = new Intent(SignupActivity.this, OTPVerifierActivity.class);
                    startActivity(nextActivity);
                } else {
                    Toast.makeText(getApplicationContext(), "Make Sure you have balance....", Toast.LENGTH_LONG);
                    return;
                }
            }
        }, new IntentFilter("SMS_SENT"));
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, "OTP: " + otp, sentPI, null);
        return otpStatus;
    }
    /*
     * Get user data from UI screen and store it to Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence("firstName",firstName.getText());
        outState.putCharSequence("lastName",lastName.getText());
        outState.putCharSequence("userName",userName.getText());
        outState.putCharSequence("phoneNumber",phoneNumber.getText());
        outState.putCharSequence("bankName",bankName.getSelectedItem().toString());
        outState.putCharSequence("emailId",emailId.getText());
    }

    /*
     * Restore User data from Bundle and set it to UI
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        firstName.setText(savedInstanceState.getCharSequence("firstName"));
        lastName.setText(savedInstanceState.getCharSequence("lastName"));
        userName.setText(savedInstanceState.getCharSequence("userName"));
        phoneNumber.setText(savedInstanceState.getCharSequence("phoneNumber"));
        //bankName.setSe(savedInstanceState.getCharSequence("bankName"));
        emailId.setText(savedInstanceState.getCharSequence("emailId"));
    }

//    public void signup() {
//        Log.d(TAG, "Signup");
//
//        if (!validate()) {
//            onSignupFailed();
//            return;
//        }
//
//        btnSignup.setEnabled(false);
//
//        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Creating Account...");
//        progressDialog.show();
//
//        String fName = firstName.getText().toString();
//        String lName = lastName.getText().toString();
//        String email = emailId.getText().toString();
////        String password = .getText().toString();
//
//        // TODO: Implement your own signup logic here.
//
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
//    }
//
//
//    public void onSignupSuccess() {
//        _signupButton.setEnabled(true);
//        setResult(RESULT_OK, null);
//        finish();
//    }
//
//    public void onSignupFailed() {
//        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
//
//        _signupButton.setEnabled(true);
//    }
//
//    public boolean validate() {
//        boolean valid = true;
//
//        String name = _nameText.getText().toString();
//        String email = _emailText.getText().toString();
//        String password = _passwordText.getText().toString();
//
//        if (name.isEmpty() || name.length() < 3) {
//            _nameText.setError("at least 3 characters");
//            valid = false;
//        } else {
//            _nameText.setError(null);
//        }
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }
//
//        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
//            _passwordText.setError("between 4 and 10 alphanumeric characters");
//            valid = false;
//        } else {
//            _passwordText.setError(null);
//        }
//
//        return valid;
//    }
}
