package com.athome.practice.PayIt.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

public class MobileDataStateChangeReceiver extends BroadcastReceiver {

    public MobileDataStateChangeReceiver() {
    }

    WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.v("NetworkCheckReceiver", "NetworkCheckReceiver invoked...");

            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiConfiguration netConfig = new WifiConfiguration();

                //set SSID as Username
                netConfig.SSID = "Anand Singh";
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
                showHomePageActivity(context);
            }
            else
            {
                Log.d("NetworkCheckReceiver", "disconnected");
                return;
            }
        }
    }

    /*
     * Starts next expected activity
     */
    private void showHomePageActivity(Context context) {
        Intent i = new Intent(context,HomePage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
