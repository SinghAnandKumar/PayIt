package com.athome.practice.PayIt.threadhandlers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.R;
import com.athome.practice.PayIt.activities.TransactionHistoryActivity;
import com.athome.practice.PayIt.dialogs.AmountDialog;

import java.lang.reflect.Method;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    //private AppBarLayout appBarLayout;
    private Button btnReceive;
    private Button btnPay;
    private WifiManager wifiManager;
    public static int PORT = 5134;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btnReceive = (Button) findViewById(R.id.collect);
        btnReceive.setOnClickListener(this);

        btnPay = (Button) findViewById(R.id.pay);
        btnPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect:
                //TODO : start mobile data and hotspot
                startTransactionHistoryActivity();
                break;

            case R.id.pay:
                //TODO : connect to hotspot started by receiver
                //collect amount to pay
                AmountDialog amountDialog = new AmountDialog();
                amountDialog.show(getSupportFragmentManager(), "Amount to pay");
//                Intent intent = new Intent(this, ScanReceiverActivity_2.class);
//                startActivity(intent);
//                break;
        }
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        //If both, wifi and mobile data is disabled, show dialog to start mobile data
        if (!isMobile && !isWifi) {
            showDialogToSwitchOnMobileNetwork();
        } else if (isWifi) { //else if wifi is enabled ask user to switch off it
            Toast.makeText(PaymentActivity.this, "Please disconnect WiFi connection", Toast.LENGTH_LONG);
            return;
        } else { // else mobile data is already on then start hotspot
            startWiFiHotspot();
            startTransactionHistoryActivity();
        }
    }

    private void startWiFiHotspot() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration netConfig = new WifiConfiguration();

        //set SSID as Username
        netConfig.SSID = getUsername();
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager, netConfig, true);
            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){};
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
            int apstate = (Integer) getWifiApStateMethod.invoke(wifiManager);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(wifiManager);
            Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }

    private void showDialogToSwitchOnMobileNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setMessage(Constants.MSG_TO_CONNECT_MOBILE_INSTEAD_OF_WIFI)
                .setCancelable(true)
                .setPositiveButton(Constants.START_MOBILE_DATA, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                })
                .setNegativeButton(Constants.QUIT, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PaymentActivity.this.finish();
                        Log.d(Constants.TAG, Constants.QUIT);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startTransactionHistoryActivity() {
        Intent i = new Intent(this, TransactionHistoryActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.USERNAME, Constants.DEFAULT_USERNAME);
    }

}
