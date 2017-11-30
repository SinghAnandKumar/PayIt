package com.athome.practice.PayIt.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.activities.TransactionHistoryActivity;

import java.lang.reflect.Method;

/**
 * Created by Anand Singh on 22/11/2017.
 */
public class DataConnectivityHelper {
    Context context;
    WifiManager wifiManager;

    public DataConnectivityHelper(Context context) {
        this.context = context;
    }

    public void checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        //If both, wifi and mobile data is disabled, show dialog to start mobile data
        if(!isMobile && !isWifi){
            showDialogToSwitchOnMobileNetwork();
        } else if (isWifi) { //else if wifi is enabled ask user to switch off it
            Toast.makeText(context,"Please disconnect WiFi connection",Toast.LENGTH_LONG);
            return;
        } else { // else mobile data is already on then start hotspot
            startWiFiHotspot();
//            connectionHelperReceiver.openSocket();
            startTransactionHistoryActivity();
        }

    }

    private void startWiFiHotspot() {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration netConfig = new WifiConfiguration();

        //set SSID as Username
        netConfig.SSID = getUsername();
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled",  WifiConfiguration.class, boolean.class);
            boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, netConfig,true);
            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){};
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
            int apstate=(Integer)getWifiApStateMethod.invoke(wifiManager);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig=(WifiConfiguration)getWifiApConfigurationMethod.invoke(wifiManager);
            Log.e("CLIENT", "\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }

    private void showDialogToSwitchOnMobileNetwork(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(Constants.MSG_TO_CONNECT_MOBILE_INSTEAD_OF_WIFI)
                .setCancelable(true)
                .setPositiveButton(Constants.START_MOBILE_DATA, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                })
                .setNegativeButton(Constants.QUIT, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish dialog
                        Log.d(Constants.TAG, Constants.QUIT);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startTransactionHistoryActivity() {
        Intent i = new Intent(context,TransactionHistoryActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.USERNAME,Constants.DEFAULT_USERNAME);
    }
}
