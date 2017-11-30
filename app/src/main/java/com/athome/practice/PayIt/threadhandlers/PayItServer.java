package com.athome.practice.PayIt.threadhandlers;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PayItServer {
    protected ReceiverHandler receiverHandler;
    protected SenderHandler senderHandler;
    protected final UICallbacks cbs;
    private final String TAG = "PayItServer";
    private Thread mLoopThread = null;

    private Socket clientSock = null;
    private ServerSocket serverSocket = null;
    //use this map to maintain globally available client lists, must be synchronized
    protected Map<String, SktResourceBundle> msgExchangeInfoBundleMap;

    public interface UICallbacks {
        void sendMessageToUI(final String msg);
        void notifyErrors(int errCode);
    }

    public PayItServer(final UICallbacks calls) {
        cbs = calls;
        msgExchangeInfoBundleMap = Collections.synchronizedMap(new HashMap<String, SktResourceBundle>());
        initLoopers();
    }

    public Thread getmLoopThread() {
        return mLoopThread;
    }

    protected void initLoopers() {
        if (senderHandler != null && senderHandler.isAlive()) return;
        if (receiverHandler != null && receiverHandler.isAlive()) return;
        senderHandler = new SenderHandler(new SenderHandler.HandlerCallbacks() {
            @Override
            public void doneSendingMessage(final String msg) {
//				cbs.sendMessageToUI("Me said: "+msg);
                cbs.sendMessageToUI("Msg send : " + msg);
            }
        });

        //After received new client immediately start listening to it by posting new Handler Message
        receiverHandler = new ReceiverHandler(new ReceiverHandler.HandlerCallbacks() {
            @Override
            public void hadReceivedNewMessage(final String ip, final String message) {
                cbs.sendMessageToUI(ip + " sent : " + message);
            }

            @Override
            public void onReceiverReadingError(final String uid) {
                //the pipe is broken, need to remove
                if (msgExchangeInfoBundleMap.containsKey(uid)) {
                    msgExchangeInfoBundleMap.get(String.valueOf(uid)).cleanUp();
                    msgExchangeInfoBundleMap.remove(String.valueOf(uid));
                    //cbs.notifyErrors(1);
                }
            }
        });
        senderHandler.start();
        receiverHandler.start();
        senderHandler.getLooper();
        receiverHandler.getLooper();
        Log.i(TAG, "Server successfully created!!");
    }

    protected void init() throws IOException {
        //initiates server socket accepting loop:
        if (serverSocket != null) {
            try {
                serverSocket.close();
                //only wait for three seconds until it dies
                mLoopThread.join(3000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        serverSocket = new ServerSocket(PaymentActivity.PORT);

        runLoop(serverSocket);
        Log.i(TAG, "Server successfully initialized!!");
    }

    @SuppressWarnings("resource")
    void runLoop(final ServerSocket sSock) {
//		    if (mLoopThread != null&& mLoopThread.isAlive()) {
//		    	try {
//					serverSocket.close();
//					//only wait for three seconds until it dies
//					mLoopThread.join(3000);
//		    	} catch (IOException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}	
//		    }
        mLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    do {//run loop for server
                        clientSock = sSock.accept();
                        //first check if clientSocket already exists in list
                        //if client exits it will be replaced with the new one so there will be no duplicates for same client
                        //if (msgExchangeInfoBundleMap.containsKey(clientSock.getInetAddress().getHostAddress()))
                        //	continue; this two lines commented coz our HashMap can replace duplicate on IP address
                        //after we have a new client socket initialize tcp resource
                        final SktResourceBundle sktResourceBundle = new SktResourceBundle(clientSock);

                        msgExchangeInfoBundleMap.put(new String(sktResourceBundle.getIP_ADDR()), sktResourceBundle);
                        if (cbs != null) //there are chances that cbs is null we cannot avoid it
                            cbs.sendMessageToUI("New client connected!! [" + clientSock.getInetAddress().toString() + "]");//TODO:send initial token as new client is added
                        //for each client to maintain a place in event loop, the first message must be fired to trigger subsequent messages.
                        //receiverHandler.postNewMessage(sktResourceBundle);
                    } while (true);
                } catch (IOException e) { //if we have exception we are out!!
                    Log.e(TAG, "having exception in accepting Loops now we are out!!!");
                    e.printStackTrace();
                } finally {//do we really can notify error here? it can trigger another request for server
                    cbs.notifyErrors(1);
                }
            }
        });
        mLoopThread.start();
    }

    public void cleanUp() {
        //clean up Server Loop, Receiver, Sender Looper thread
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//		mLoopThread.interrupt();
        senderHandler.cleanUp();
        receiverHandler.cleanUp();
        //clean up cache
        Set<String> keySet = msgExchangeInfoBundleMap.keySet();
        for (Iterator<String> i = keySet.iterator(); i.hasNext(); ) {
            msgExchangeInfoBundleMap.get(i.next()).cleanUp();
        }
        msgExchangeInfoBundleMap.clear();
    }

    public void sendMessages(final String msg) {
        //Log.i(TAG, "before sending message "+msg+" from "+this.getClass().toString());
        if (senderHandler != null) {
            //Loop through all available clients and sending message for each one
            //TODO: only send msg once and delete it from map after receiving msg
            Log.i(TAG, "before sending message " + msg + " from " + this.getClass().toString());
            Set<String> keySet = msgExchangeInfoBundleMap.keySet();
            for (Iterator<String> i = keySet.iterator(); i.hasNext(); ) {
                //here need to create new copy to make sure no synchronization issue
                SktResourceBundle bundle = SktResourceBundle.clone(msgExchangeInfoBundleMap.get(i.next()));
                bundle.setMessage(msg);
                senderHandler.postNewMessage(bundle);
            }
        }
    }

    private void debugMessage(final String msg) {
        String className = PayItServer.this.toString();
        cbs.sendMessageToUI("Debug from [" + className.substring(className.indexOf("Chat")) + "]: " + msg);
    }

    public boolean needToInitThread() {
        // TODO Auto-generated method stub
        if (mLoopThread == null) return true;
        else
            return !mLoopThread.isAlive();
    }
}