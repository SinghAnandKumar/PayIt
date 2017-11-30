package com.athome.practice.PayIt.threadhandlers;

import android.app.Service;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PaymentService extends Service {

	public static final int SERVER = 0;
	public static final int CLIENT = 1;
	public static final int CLEAN_UP = 2;
	public static final String REQ_TYPE = "com.dashdrivers.payIt";
	public static final String SERVER_TYPE = "Merchant";
	public static final String CLIENT_TYPE = "Customer";
	public static final String TAG = "PaymentService";
	
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	HandlerThread mhThread;
	private final IBinder mChatBinder = new PaymentBinder();
	
	private PayItServer server = null;
	private PayItClientServer client = null;
	private PayItServer.UICallbacks mUICallback;
	private NsdServiceInfo mNsdItem;
	private String debugMessageCache = "\n";
	private TextView mTextBean = null;
	
	
	public String copyOfDebugCache() {
		return new String(debugMessageCache);
	}
	
	public synchronized TextView getmTextBean() {
		return mTextBean;
	}

	public synchronized void setmTextBean(final TextView mTextBean) {
		this.mTextBean = mTextBean;
	}
	
	public class PaymentBinder extends Binder {
		PaymentService getService() {
			return PaymentService.this;
		}
	}
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) { //startId is stored in msg.arg1
			//start our chat server and client here
			
			if (msg.what == SERVER) {//do server initialization
				if (server != null) return;
				
				server = new PayItServer(mUICallback);
				try{
					server.init();
				} catch (IOException e) {
					Log.e(TAG , "ServerSocket init failed !!!");
					e.printStackTrace();
					server.cleanUp();
					server = null;
					//but we need to restart server in this case
					initAndPostForServer();
					return;
				}
			}
			else if (msg.what == CLIENT) {//do client initialization
				mNsdItem = (NsdServiceInfo)msg.obj;
				try {
					if (client == null){
						client = new PayItClientServer(mUICallback);
					}
					if (client != null) {
						client.init(mNsdItem);
					}
				} catch(IOException e) {
					Log.e(TAG, "Client Socket Initialization Error!!");
					e.printStackTrace();
					if (client == null) return;
					client.cleanUp();
					client = null;
					//trying to reinit client but only for a 3 times
					msg.arg1 = msg.arg1-1;
					if (msg.arg1 > 0) initAndPostForClient(msg);
					return;
				}
			}
			else if (msg.what == CLEAN_UP) {
				
			}
		
		}		
		//do we need to stop after init?
		//stopSelf();
	}
	// 0 --meaning everything fine, 1 meaning server is disconnected, 2 meaning client disconnected	
	// 3 meaning both server and clients are disconnected
	public int checkHealthy() {
		return 0;
	}
	@Override
	public IBinder onBind(Intent i) {
		return mChatBinder;
	}

	@Override
	public void onCreate() {
		//start up thread running service
		Log.i(TAG, " here in onCreate method !!!");
		mUICallback = new PayItServer.UICallbacks() {
			@Override
			public void sendMessageToUI(final String msg) {
				//this must run on UI thread
				outputDebugMessageToUI(msg);
			}
	        //it is either called by server or by client 1 -- meaning server down, 2 -- meaning client down,
			@Override
			public void notifyErrors(int errCode) {
				if (errCode == 1) {
					//clean up and restart
					if (server != null) {
						server.cleanUp();
						server = null;
						initAndPostForServer();
					}
				} else if (errCode == 2) {
					if (client != null) {
						client.cleanUp();
						client = null;
						//note we don' restart client for now
					}
				}
			}
		};
		mhThread = new HandlerThread("mServiceHandlerThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mhThread.start();
		mServiceLooper = mhThread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}
	
	@Override
	public int onStartCommand(Intent in, int flags, int startId) {
		Log.i(TAG, "here we are in start command with startid = "+startId);
		if (in != null) {
			Bundle bundle = in.getExtras();
			if (bundle != null) { //there are chances for NULL intent when process is resurrected after being destroyed by MountainView
				//for each start request send mesages to start job with startId initialized in message body
				if (SERVER_TYPE.equals(bundle.getString(REQ_TYPE))){	
					//start a Server Job
					Message msg = mServiceHandler.obtainMessage(SERVER);
					//msg.arg1 = startId;
					mServiceHandler.sendMessage(msg);
				}
			}
		}
		return START_STICKY;
	}
	
	//call this only in OnCreate
	//TODO : pass the relative layout to manipulate by this service
	public void initAll(final TextView debugView) {
		setmTextBean(debugView);
	}
	//to ensure service is initialized before ServiceHandler Loop need to send message here
	// count is the number of times retrying send the Request for Client in case of error
	public void initAndPostForClient(final NsdServiceInfo service, final int count) {
		mNsdItem = service;
		Message msg = mServiceHandler.obtainMessage(CLIENT, (Object)service);
		msg.arg1 = count;
		mServiceHandler.sendMessage(msg);
	}
	//private method used internally to retry connection for Client, msg is assumed client message
	private void initAndPostForClient(Message msg) {
		mServiceHandler.sendMessage(msg);
		
	}
	public void initAndPostForServer() {
		Message msg = mServiceHandler.obtainMessage(SERVER);
		mServiceHandler.sendMessage(msg);
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG," Before service is destroyed!!");
		//TO-DO: adding things to destroy Loopers and all server and client resources
		if(server != null) server.cleanUp();
		if (client != null) client.cleanUp();
		mhThread.quit();
		super.onDestroy();
	}

	//TODO: create similar method to hide/show merchant images
	public void outputDebugMessageToUI(final String msg) {
		//this must run on UI thread
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder(debugMessageCache);
				sb.append("\n"+msg);
				debugMessageCache = sb.toString();
				if (getmTextBean() == null) return;
				getmTextBean().setText(debugMessageCache);
			}
		});
	}
	//following are classes used for communicating with activity
	public void postMessageToServer(final String str) {
		if (server != null)
			server.sendMessages(str);
	}
	
	public void postMessageToClient(final String str) {
		if (client != null)
			client.sendMessages(str);
	}
	//do dummy toast
	public void toastOk() {
		Toast.makeText(this, "Here we are successfully accessing server!!", Toast.LENGTH_LONG).show();
	}
	
	public boolean hasServerOrClient() {
		return (server != null && server.getmLoopThread()!= null && server.getmLoopThread().isAlive());
	}
}
