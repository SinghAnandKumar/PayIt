package com.athome.practice.PayIt.helpers;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import com.athome.practice.PayIt.Constants;

/**
 * Created by Anand Singh on 19/11/17.
 */
public class NsdHelper {

    Context context;
    NsdHelper.Callback callback;
    NsdManager mNsdManager;
    NsdManager.RegistrationListener mRegistrationListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.ResolveListener mResolveListener;
    String serviceName = "";
    final String SERVICE_TYPE = "_http._tcp.";

    String TAG = "My Tag";

    public NsdHelper(Context context, NsdHelper.Callback callback) {
        this.context = context;
        this.callback = callback;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void registerService(String serviceName, int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName);
        this.serviceName = serviceName;
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        if (mNsdManager != null) {
            initRegistrationListener();
            mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
        }
    }

    public void discoverServices() {
        if (mNsdManager != null) {
            initDiscoveryListener();
            mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        }
    }

    public void resolveService(NsdServiceInfo serviceInfo) {
        Log.e("My Tag", "Resolving : " + serviceInfo.getServiceName());
        if (mNsdManager != null) {
            initResolveListener();
            mNsdManager.resolveService(serviceInfo, mResolveListener);
        }
    }

    public void unregisterService() {
        if (mNsdManager != null)
            mNsdManager.unregisterService(mRegistrationListener);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    void initDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                Toast.makeText(context, "Discovery Start Failed : " + s, Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                Toast.makeText(context, "Discovery Stop Failed : " + s, Toast.LENGTH_SHORT).show();
                Log.e("My Tag", s);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                Toast.makeText(context, "Discovery Started : " + s, Toast.LENGTH_SHORT).show();
                Log.e("My Tag", "Discovery Started : " + s);
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Toast.makeText(context, "Discovery Stopped : " + s, Toast.LENGTH_SHORT).show();
                Log.e("My Tag", "Discovery Stopped : " + s);
                callback.onDiscoveryStopped(s);
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                Log.d(TAG, "Service discovery success : " + nsdServiceInfo.getServiceName());
                if (!nsdServiceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type : " + nsdServiceInfo.getServiceType());
                } else if (nsdServiceInfo.getServiceName().equals(serviceName)) {
                    Log.d(TAG, "Same machine : " + serviceName);
                } else if (nsdServiceInfo.getServiceName().contains(Constants.NSD_BASE_NAME_PAYIT)) {
                    Log.d(TAG, "Service Found : " + serviceName);
                    Toast.makeText(context, "Service Found : " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
                    callback.onServiceDiscovered(nsdServiceInfo);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                if (!nsdServiceInfo.getServiceName().equals(serviceName)) {
                    Toast.makeText(context, nsdServiceInfo.getServiceName() + " Service Lost : " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
                    callback.onServiceLost(nsdServiceInfo);
                }
            }
        };
    }

    void initRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(context, "Registration Failed : " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, nsdServiceInfo.toString());
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(context, "Unregistration Failed : " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                serviceName = nsdServiceInfo.getServiceName(); //get updated service name ,if it has been changed by android to resolve conflict
                Toast.makeText(context, "Service Registered " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(context, "Service Unregistered : " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    void initResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed " + nsdServiceInfo.getServiceName() + " : " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                Log.e(TAG, "Resolve Succeeded : " + nsdServiceInfo.getServiceName());
                if (nsdServiceInfo.getServiceName().equals(serviceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                } else if (nsdServiceInfo.getServiceName().contains(Constants.NSD_BASE_NAME_PAYIT)) {
                    callback.onServiceResolved(nsdServiceInfo);
                }
            }
        };
    }

    public interface Callback {
        void onServiceDiscovered(NsdServiceInfo service);
        void onServiceResolved(NsdServiceInfo service);
        void onServiceLost(NsdServiceInfo service);
        void onDiscoveryStopped(String s);
    }
}