package com.athome.practice.PayIt.threadhandlers;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class SenderHandler extends HandlerThread {
    private final static String TAG = "SenderThread";
    private final static int SEND = 0;
    static Handler handler = null;
//    PrintWriter netOutput = null;
    HandlerCallbacks handlerCallbacks = null;

    public interface HandlerCallbacks
    {
        void doneSendingMessage(final String msg);
    }

    /**
     * Main worker method
     *
     * @param msg SktResourceBundle
     */
    public void postNewMessage(final SktResourceBundle msg)
    {
        handler.obtainMessage(SEND, msg).sendToTarget();
    }

    public SenderHandler(HandlerCallbacks hcb) {
        super(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        handlerCallbacks = hcb;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SEND) {
                    Log.i(TAG, "Before handling SEND event");
                    final SktResourceBundle message = (SktResourceBundle) msg.obj;
                    handleSendEvent(message);
                }
            }

            private void handleSendEvent(SktResourceBundle msg)
            {
                msg.getOutStream().println(msg.getMessage());
                handlerCallbacks.doneSendingMessage(msg.getMessage() + " : " + msg.getSocket().toString());
            }
        };
    }

    public void cleanUp()
    {
        if (handler != null) {
            handler.removeMessages(SEND);
        }
        this.quit();
    }

    //	public synchronized void setmSocket(Socket mSock) {
//		if (mSocket != null) { //we previously had a connection do cleanup first
//			try {
//				//mark outputstream also
//				netOutput.close();
//				netOutput = null;
//				mSocket.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			if (handler != null) {
//				handler.removeMessages(SEND);
//			}
//		}
//		this.mSocket = mSock;
//	}

}
