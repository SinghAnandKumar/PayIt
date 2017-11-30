package com.athome.practice.PayIt.threadhandlers;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Following are inherited members from PayItServer:
 * <p/>
 * protected ReceiverHandler receiverHandler;
 * protected SenderHandler senderHandler;
 * protected final UICallbacks cbs;
 * protected Map<String, SktResourceBundle> msgExchangeInfoBundleMap;
 */
public class PayItClientServer extends PayItServer {
    private final String TAG = "PayItClientServer";
    SktResourceBundle chatResource = null;

    public SktResourceBundle getChatResource() {
        return chatResource;
    }

    public PayItClientServer(UICallbacks calls) {
        super(calls);
    }

    @Override
    protected void initLoopers() {
        if (senderHandler != null && senderHandler.isAlive()) return;
        if (receiverHandler != null && receiverHandler.isAlive()) return;
        senderHandler = new SenderHandler(new SenderHandler.HandlerCallbacks() {
            @Override
            public void doneSendingMessage(final String msg) {
//                cbs.sendMessageToUI("Me said: " + msg);
                cbs.sendMessageToUI("Msg send C : " + msg);
            }
        });

        receiverHandler = new ReceiverHandler(new ReceiverHandler.HandlerCallbacks() {
            @Override
            public void hadReceivedNewMessage(final String bundle, final String msg) {
                cbs.sendMessageToUI(bundle + " said : " + msg);
            }

            @Override
            public void onReceiverReadingError(String uid) {
                if (msgExchangeInfoBundleMap.containsKey(uid)) {
                    msgExchangeInfoBundleMap.get(uid).cleanUp();
                    msgExchangeInfoBundleMap.remove(uid);
//                    cbs.notifyErrors(2);
                }
            }
        });
        // Get Looper ready
        senderHandler.start();
        receiverHandler.start();
        senderHandler.getLooper();
        receiverHandler.getLooper();
        Log.i(TAG, "Client successfully created!!");
    }

    protected void init(final NsdServiceInfo nsdItem) throws IOException {
        // Checks to see if we already connected to the same server
        if (msgExchangeInfoBundleMap.containsKey(nsdItem.getHost().getHostAddress())) {
            return;
        }
        SktResourceBundle cnrb;
        Socket serverSock = new Socket(nsdItem.getHost(), nsdItem.getPort());
        cnrb = new SktResourceBundle(serverSock);
//        chatResource = SktResourceBundle.clone(cnrb);
        // Adds new chat resource to hash map
        msgExchangeInfoBundleMap.put(new String(cnrb.getIP_ADDR()), cnrb);
        // Pushes first message for receiving looper
        if (cnrb != null) {
            receiverHandler.postNewMessage(cnrb.clone(cnrb));
        }
    }

    @Override
    public void sendMessages(final String msg) {
//        Log.i(TAG, "before sending message "+msg+" from "+this.getClass().toString());
        if (senderHandler != null && chatResource != null) {
            Log.i(TAG, "before sending message " + msg + " from " + this.getClass().toString());
            SktResourceBundle bundle = SktResourceBundle.clone(chatResource);
            bundle.setMessage(msg);
            senderHandler.postNewMessage(bundle);
        }
    }
}