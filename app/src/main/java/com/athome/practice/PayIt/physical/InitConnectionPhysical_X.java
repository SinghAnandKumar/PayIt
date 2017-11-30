package com.athome.practice.PayIt.physical;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.athome.practice.PayIt.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Anand Singh on 19/11/2017.
 */
public class InitConnectionPhysical_X {

    ServerSocket servSkt;

    Callback callback;
    public InitConnectionPhysical_X(Callback callback){
        this.callback = callback;
    }

    public void openSocket(int port){
        try {
            Log.e("My Tag", "Opening Port: "+port);
            servSkt = new ServerSocket(port);
            Log.e("My Tag", "Server Started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnections(){
        try {
            //Sending Tokens
            while (true) {
                Log.e("My Tag", "Waiting...");
                Socket skt = servSkt.accept();
                Log.e("My Tag", "Connected");
                JSONObject initialToken = callback.onSocketFound(skt);
                boolean success = sendToken(skt, initialToken);
                if (success) {
                    callback.onTokenSendSuccess();
                }
                else {
                    callback.onTokenSendFailed();
                }
            }
            //-- Sending Tokens
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void closeSocket(){
        try {
            servSkt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //payee gets token from receiver
    public JSONObject receiveToken(NsdServiceInfo serviceInfo){
        InetAddress ip = serviceInfo.getHost();
        int port = serviceInfo.getPort();

        try {
            Log.e("My Tag", "Connecting to "+serviceInfo.getServiceName());
            Socket skt = new Socket(ip, port);
            Log.e("My String", "Connected");
            //Reading Response
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            String temp;
            String result = "";
            while((temp = br.readLine()) != null){
                if(temp.contains(Constants.END_OF_MSG)){
                    temp = temp.replace(Constants.END_OF_MSG, "");
                    result+=temp;
                    break;
                }
                result += temp;
            }
            Log.e("My Tag", "Msg Received:"+result);
            //-- Reading Response

            JSONObject main = new JSONObject(result);
            return main;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return null;
    }

    //For Server node
    private boolean sendToken(Socket skt, JSONObject msg){
        //Sending Request
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(skt.getOutputStream())
                    ), true);
            out.println( msg.toString() + Constants.END_OF_MSG);
            Log.e("My Tag", "Msg Sent: "+msg.toString());
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
        //-- Sending Request
    }

    public interface Callback{
        JSONObject onSocketFound(Socket skt);
        void onTokenSendSuccess();
        void onTokenSendFailed();
    }
}
