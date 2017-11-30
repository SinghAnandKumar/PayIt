package com.athome.practice.PayIt.threadhandlers.interfaces;

import android.net.nsd.NsdServiceInfo;

/**
 * Created by teocci on 2/17/17.
 *
 */
public interface NsdHelperListener
{
//    public void notifyResolvedOneItem(NsdServiceInfo nsdItem);
//    public void notifyDiscoveredOneItem(NsdServiceInfo nsdItem);
//    public void outputDebugMessage(final String msg);
    void notifyRegistrationComplete(NsdServiceInfo nsdItem);
    void onServiceDiscovered(NsdServiceInfo service);
    void onServiceResolved(NsdServiceInfo service);
    void onServiceLost(NsdServiceInfo service);
    void onDiscoveryStopped(String s);
}
