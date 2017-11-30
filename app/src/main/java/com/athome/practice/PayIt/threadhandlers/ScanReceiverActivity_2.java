package com.athome.practice.PayIt.threadhandlers;

import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.R;
import com.athome.practice.PayIt.beans.Token;
import com.athome.practice.PayIt.helpers.NsdHelper;
import com.athome.practice.PayIt.physical.TokenSendReceivePhysical;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class ScanReceiverActivity_2 extends AppCompatActivity implements NsdHelper.Callback, TokenSendReceivePhysical.TokenPhysicalCallback {

    private ImageView centerImage;
    private RippleBackground rippleBackground;
    private HashMap<String, NsdServiceInfo> hmAvailableServices;
    private HashMap<String, Boolean> hmIsServiceReady;
    private HashMap<String, Integer> nameIdBinder;
    private NsdHelper nsdHelper;
    private NsdServiceInfo connectedNsd;
    private RelativeLayout receiver1;
    private TextView receiverName1;
    private RelativeLayout receiver2;
    private TextView receiverName2;
    private RelativeLayout receiver3;
    private TextView receiverName3;
    private RelativeLayout receiver4;
    private TextView receiverName4;


    int connectionCount = 0;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_receiver_2);
        init();

        rippleBackground.startRippleAnimation();
        nsdHelper.discoverServices();
    }

    private void init() {
        centerImage = (ImageView) findViewById(R.id.centerImage);
        rippleBackground = (RippleBackground) findViewById(R.id.ripple_background);
        receiver1 = (RelativeLayout) findViewById(R.id.receiver1);
        receiverName1 = (TextView) findViewById(R.id.beneficiary_name1);
        receiver2 = (RelativeLayout) findViewById(R.id.receiver2);
        receiverName2 = (TextView) findViewById(R.id.beneficiary_name2);
        receiver3 = (RelativeLayout) findViewById(R.id.receiver3);
        receiverName3 = (TextView) findViewById(R.id.beneficiary_name3);
        receiver4 = (RelativeLayout) findViewById(R.id.receiver4);
        receiverName4 = (TextView) findViewById(R.id.beneficiary_name4);
        nsdHelper = new NsdHelper(this,this);
        hmAvailableServices = new HashMap<>();
        hmIsServiceReady = new HashMap<>();
        nameIdBinder = new HashMap<>();
        Intent intent = getIntent();
        amount = intent.getStringExtra(Constants.AMOUNT);
    }

    private void addReceiverToView(String beneficiaryName, boolean isVisible) {

        if(hmIsServiceReady.containsKey(beneficiaryName) && !hmIsServiceReady.get(beneficiaryName)) {
            switch (nameIdBinder.get(beneficiaryName)) {
                case Constants.ONE:
                    if (isVisible) {
                        receiver1.setVisibility(View.VISIBLE);
                        receiverName1.setText(beneficiaryName);
                        receiver1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(ScanReceiverActivity_2.this, receiverName1.getText() + " Selected", Toast.LENGTH_SHORT).show();
                                onBeneficiarySelected(receiverName1.getText().toString());
                            }
                        });
                    } else {
                        receiver1.setVisibility(View.GONE);
                        receiver1.setClickable(false);
                    }
                    break;
                case Constants.TWO:
                    if (isVisible) {
                        receiver2.setVisibility(View.VISIBLE);
                        receiverName2.setText(beneficiaryName);
                        receiver2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(ScanReceiverActivity_2.this, receiverName2.getText() + " Selected", Toast.LENGTH_SHORT).show();
                                onBeneficiarySelected(receiverName2.getText().toString());
                            }
                        });
                    } else {
                        receiver1.setVisibility(View.GONE);
                        receiver1.setClickable(false);
                    }
                    break;
                case Constants.THREE:
                    if (isVisible) {
                        receiver3.setVisibility(View.VISIBLE);
                        receiverName3.setText(beneficiaryName);
                        receiver3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(ScanReceiverActivity_2.this, receiverName3.getText() + " Selected", Toast.LENGTH_SHORT).show();
                                onBeneficiarySelected(receiverName3.getText().toString());
                            }
                        });
                    } else {
                        receiver1.setVisibility(View.GONE);
                        receiver1.setClickable(false);
                    }
                    break;
                case Constants.FOUR:
                    if (isVisible) {
                        receiver4.setVisibility(View.VISIBLE);
                        receiverName4.setText(beneficiaryName);
                        receiver4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(ScanReceiverActivity_2.this, receiverName4.getText() + " Selected", Toast.LENGTH_SHORT).show();
                                onBeneficiarySelected(receiverName4.getText().toString());
                            }
                        });
                    } else {
                        receiver1.setVisibility(View.GONE);
                        receiver1.setClickable(false);
                    }
                    break;
                default:
                    receiver1.setVisibility(View.GONE);
                    receiver1.setClickable(false);
                    receiver2.setVisibility(View.GONE);
                    receiver2.setClickable(false);
                    receiver3.setVisibility(View.GONE);
                    receiver3.setClickable(false);
                    receiver4.setVisibility(View.GONE);
                    receiver4.setClickable(false);
                    break;
            }
        }
    }

    private void onBeneficiarySelected(String beneficiaryName) {
        // TODO : Show progress bar
        Token token = new Token("1","Me","You",amount,null,null);
        if (hmIsServiceReady.get(beneficiaryName)) {
            connectedNsd = hmAvailableServices.get(beneficiaryName);
            receiveToken(connectedNsd.getHost(), token);

        } else {
            Toast.makeText(ScanReceiverActivity_2.this, "Please wait...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceDiscovered(NsdServiceInfo service) {
        String beneficiaryName = "NA";
        if(service.getServiceName().split(Constants.NSD_SEPERATOR).length >= 2)
            beneficiaryName = service.getServiceName().split(Constants.NSD_SEPERATOR)[1]; //Sample service name: PayIt.Renuka

        if (!hmAvailableServices.containsKey(beneficiaryName) && !beneficiaryName.equals("NA")) {
            hmAvailableServices.put(beneficiaryName, service);//At time of discovery service just have name and type,but Host is null and it will be resolved at onResolveSuccess
            hmIsServiceReady.put(beneficiaryName, false);
            nsdHelper.resolveService(service);
        } else if(!hmIsServiceReady.get(beneficiaryName)) {
            nsdHelper.resolveService(service);
        }

    }

    @Override
    public void onServiceResolved(NsdServiceInfo service) {
        Log.d("My tag",service.getServiceName());

//        String beneficiaryName = service.getServiceName()==null?"NA":service.getServiceName();
        String beneficiaryName = null;

        if(service.getServiceName().split(Constants.NSD_SEPERATOR).length >= 2)
            beneficiaryName = service.getServiceName().split(Constants.NSD_SEPERATOR)[1];//Sample service name: PayIt.Punjabi Junction
        if(!hmIsServiceReady.get(beneficiaryName)) {
            hmAvailableServices.put(beneficiaryName, service);//Now the Host name is already resolved,overriding the existing service with new host
            hmIsServiceReady.put(beneficiaryName, true);
            connectionCount++;
            nameIdBinder.put(beneficiaryName,connectionCount);

            final String bene = beneficiaryName;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addReceiverToView(bene, true);
                }
            });
        }
    }

    @Override
    public void onServiceLost(NsdServiceInfo service) {
        final String bene = service.getServiceName();
        connectionCount--;
        nameIdBinder.remove(bene);

        if(hmAvailableServices.containsKey(bene)) {
            hmIsServiceReady.put(service.getServiceName(),false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addReceiverToView(bene, false);
                }
            });
        }
    }

    @Override
    public void onDiscoveryStopped(String s) {
        nameIdBinder.put("CLEARALL",10);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addReceiverToView("CLEARALL", false);
            }
        });
    }

    public void receiveToken(InetAddress targetIp, Token token){
        new TokenSendReceivePhysical(this).updateToken(targetIp, Constants.PORT_TOKEN_DIST, token);
    }

//    public void sendToken(InetAddress targetIp, Token token){
//        new TokenSendReceivePhysical(this).sendToken(targetIp, Constants.PORT_TOKEN_DIST, token);
//    }

    @Override
    public void onTokenReceivedAtBeneficiary(JSONObject token) {
        //No need to implement here
        return;
    }

    @Override
    public void PayeeConnected(Socket skt) {
        //No need to implement here
        return;
    }

    @Override
    public void onTokenReceivedAtPayee(JSONObject token) {
        Log.d("My Tag","Token received at Payee");
        Toast.makeText(this,"Token received at Payee",Toast.LENGTH_SHORT);
        Toast.makeText(this,"Paid to : ",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nsdHelper.stopDiscovery();
        //close payee socket
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        nsdHelper.discoverServices();
    }
}
