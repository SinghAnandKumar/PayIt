package com.athome.practice.PayIt.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.widget.Toast;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.beans.Token;
import com.athome.practice.PayIt.physical.TokenSendReceivePhysical;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Anand Singh on 21/11/17.
 */
public class ConnectionHelperReceiver implements TokenSendReceivePhysical.TokenPhysicalCallback, NsdHelper.Callback {

    int connections = Constants.MAX_CONNECTS;
    private TokenSendReceivePhysical tokenExchangePhysical;
    private NsdHelper nsdHelper;
    Context context;
    ConnHelperReceiverCallback callback;
    Token token;
    ArrayList<InetAddress> payeeList = new ArrayList<>();

    //Constructor for Receiver
    public ConnectionHelperReceiver(Context context, ConnHelperReceiverCallback callback, Token token) {
        this.context = context;
        this.token = token;
        this.callback = callback;
        tokenExchangePhysical = new TokenSendReceivePhysical(this);
        nsdHelper = new NsdHelper(context, this);
    }

    //Sending Methods
    public void openSocket() {  //To be called by root Node and ConnectionHelperReceiver internally
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                socketOpenByReceiver();
            }
        });
        t.start();
    }

    protected void socketOpenByReceiver() {
        String nsdName = Constants.NSD_BASE_NAME_PAYIT + "#" + getUserNameFromSharedPreference();
        tokenExchangePhysical.openSocket(Constants.PORT_TOKEN_DIST);
        nsdHelper.registerService(nsdName, Constants.PORT_TOKEN_DIST);
        tokenExchangePhysical.acceptConnections(token);
    }

    private String getUserNameFromSharedPreference() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.USERNAME, Constants.DEFAULT_USERNAME).toString();
    }

    public ArrayList<InetAddress> getClientList() {
        return payeeList;
    }

    @Override
    public void onTokenReceivedAtBeneficiary(JSONObject token) {
        //show dialog of payment confirmation
        String amount = "00.00";
        try{
            amount = token.getString("totalAmount");
        }catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(context,"Payment Received : "+amount,Toast.LENGTH_LONG);
    }

    @Override
    public void PayeeConnected(Socket skt) {
//        if (connections > 0) {
//            JSONObject main = new JSONObject();
//            main = mockTokenJSON(main);
//            //TODO : if-else to either add or delete client from list based on received payment token
//            payeeList.add(skt.getInetAddress()); //Store Client Address
//            return main;
//        } else
//            return null; //No space to connect then reject request
        Toast.makeText(context, "Payee connected", Toast.LENGTH_SHORT);
    }

    @Override
    public void onTokenReceivedAtPayee(JSONObject token) {
        if (token == null) {
            Toast.makeText(context, "TOKEN_RECEIVE_ERROR", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context,"Payment Successful",Toast.LENGTH_SHORT);
    }

    public void unRegisterService() {
        nsdHelper.unregisterService();
        tokenExchangePhysical.closeSocket();
    }

    @Override
    public void onServiceDiscovered(final NsdServiceInfo service) {
        //Do nothing as receiver don't need to discover services
        return;
    }

    @Override
    public void onServiceResolved(NsdServiceInfo service) {
        //Do nothing as receiver don't need to discover services
        return;
    }

    @Override
    public void onServiceLost(NsdServiceInfo service) {
        //Do nothing as receiver don't have to do anything for this
        return;
    }

    @Override
    public void onDiscoveryStopped(String s) {
        callback.onDiscoveryStopped(s);
    }

    public interface ConnHelperReceiverCallback {
        void onServiceDiscovered(NsdServiceInfo serviceInfo);
        void onServiceResolved(NsdServiceInfo serviceInfo);
        void onDiscoveryStopped(String s);
    }

//    @Override
//    public void onTokenSendSuccess() {
//        if (connections <= 0) { //Check if Max tokens sent
//            tokenExchangePhysical.closeSocket();
//            nsdHelper.unregisterService();
//        }
//    }
//
//    @Override
//    public void onTokenSendFailed() {
//        connections++;
//    }
}