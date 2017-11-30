package com.athome.practice.PayIt.threadhandlers;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

public class ReceiverHandler extends HandlerThread {
    private final static String TAG = "ReceiverThread";
    private final static int REC = 1;
    Handler mHandler = null;
    //	 BufferedReader netInput = null;
    HandlerCallbacks handlerCallbacks = null;

    public interface HandlerCallbacks {
        void hadReceivedNewMessage(final String bundle, final String message);
        void onReceiverReadingError(final String uid);
    }

    public ReceiverHandler(HandlerCallbacks hcb) {
        super(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        handlerCallbacks = hcb;
    }

    /**
     * Main worker method
     */
    public void postNewMessage(final SktResourceBundle msg) {
        while (mHandler == null) {
            Log.e(TAG, "we are blocked here waiting for handler to become available !!!");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mHandler.obtainMessage(REC, msg).sendToTarget();
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                final SktResourceBundle message = (SktResourceBundle) msg.obj;
                handleRecEvent(message);
            }

            private void handleRecEvent(final SktResourceBundle msg) {

                new Thread(new Runnable() {
                    //blocking statement below
                    @Override
                    public void run() {
                        try {
                            //read in while loop for big chunk of data
                            String message = msg.getInStream().readLine();
                            //sending messages to main thread
                            if (message != null) {
                                handlerCallbacks.hadReceivedNewMessage(msg.getSocket().toString(), new String(message));
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "reading from stream error out!!!");
                            e.printStackTrace();
                            handlerCallbacks.onReceiverReadingError(msg.getIP_ADDR());
                            return;
                        }
                        //schedule another event
                        mHandler.obtainMessage(REC, (Object) msg.clone(msg)).sendToTarget();
                    }
                }).start();

            }
        };
        //handler.sendEmptyMessage(0);
    }

    public void cleanUp() {
        //remove message, close socket and outputstream.
        if (mHandler != null) {
            mHandler.removeMessages(REC);
        }
        this.quit();
    }
}
