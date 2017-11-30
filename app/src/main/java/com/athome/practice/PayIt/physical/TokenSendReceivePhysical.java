package com.athome.practice.PayIt.physical;

import android.os.AsyncTask;
import android.util.Log;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.beans.Token;

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
 * Created by Anand Singh on 21/11/17.
 */
public class TokenSendReceivePhysical {

    TokenPhysicalCallback callback;
    ServerSocket servSkt;

    public TokenSendReceivePhysical(TokenPhysicalCallback callback) {
        this.callback = callback;
    }

    public void sendToken(InetAddress ip, int port, Token token) {
        new TokenSendReceive(ip, port,token).execute();
    }

    public void updateToken(InetAddress ip, int port, Token token) {
        new TokenReceiveSend(ip, port, token).execute();
    }

    private class TokenReceiveSend extends AsyncTask<Void, Void, JSONObject> {
        InetAddress ip;
        int port;
        Token token;

        public TokenReceiveSend(InetAddress ip, int port,Token token) {
            this.ip = ip;
            this.port = port;
            this.token = token;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                Log.e("My Tag", "Connecting...");
                Socket skt = new Socket(ip, port);
                Log.e("My Tag", "Connected");

                //Reading Response
                BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                String temp, result = "";
                while ((temp = br.readLine()) != null) {
                    if (temp.contains(Constants.END_OF_MSG)) {
                        temp = temp.replace(Constants.END_OF_MSG, "");
                        result += temp;
                        break;
                    }
                    result += temp;
                }
                Log.e("My Tag", "Msg Received:" + result);
                //-- Reading Response

                JSONObject main = updatedToken(result);

                //--Sending updated Token
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(skt.getOutputStream())
                        ), true);
                out.println(main.toString() + Constants.END_OF_MSG);
                Log.e("My Tag", "Msg Sent: " + main.toString());
                out.flush();
                //--Sending updated Token

                return main;
            } catch (IOException e) {
//                closeSocket();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject main) {
            callback.onTokenReceivedAtPayee(main);
        }

        private JSONObject updatedToken(String msg) {
            try {
                JSONObject itoken = new JSONObject(msg);
//                msg.put("",token.getReceiverId());
                itoken.put("senderId","IamSender");
                itoken.put("totalAmount","20");
                return itoken;
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class TokenSendReceive extends AsyncTask<Void, Void, JSONObject> {
        InetAddress ip;
        int port;
        Token token;

        public TokenSendReceive(InetAddress ip, int port , Token token) {
            this.ip = ip;
            this.port = port;
            this.token = token;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                Socket skt = new Socket(ip, port);
                //Sending Token
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(skt.getOutputStream())
                        ), true);
                out.println(token.toString() + Constants.END_OF_MSG);
                Log.e("My Tag", "Msg Sent: " + token.toString());
                out.flush();
                //Sending Token

                //Reading Response
                BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                String temp, result = "";
                while ((temp = br.readLine()) != null) {
                    if (temp.contains(Constants.END_OF_MSG)) {
                        temp = temp.replace(Constants.END_OF_MSG, "");
                        result += temp;
                        break;
                    }
                    result += temp;
                }
                Log.e("My Tag", "Msg Received:" + result);
                //-- Reading Response
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            callback.onTokenReceivedAtBeneficiary(jsonObject);
        }
    }

    //Methods for receiver
    public void openSocket(int port) {
        try {
            Log.e("My Tag", "Opening Port: " + port);
            servSkt = new ServerSocket(port);
            Log.e("My Tag", "Receiver NSD started...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnections(Token token) {
        try {
            while (true) {
                Log.e("My Tag", "Waiting for payee..");
                final Socket skt = servSkt.accept();
                Log.e("My Tag", "Connected to receiver");
                callback.PayeeConnected(skt);
                sendToken(skt.getInetAddress(), skt.getPort(), token);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            servSkt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface TokenPhysicalCallback {
        void onTokenReceivedAtBeneficiary(JSONObject token);
        //used by payee
        void PayeeConnected(Socket skt);
        void onTokenReceivedAtPayee(JSONObject token);
    }
}
