package mine.fileshare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;


/**
 * ConnectionManager用来管理设备之间的连接，
 * 如监听其他设备发送过来的连接请求，
 * 向其他设备发送连接请求等
 */
public class ConnectionManager {
	private static final String NAME = "FileShare";
	//private static final UUID MY_UUID = UUID.fromString("0024589A-B724-A78D-2EEA-E3E344FF6929");
	//private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private BluetoothAdapter bluetoothAdapter;
	private Handler handler;
	private ServerThread serverThread;
	
	/*以下常量或变量用于与模拟器的连接测试*/
	public static final String SERVERLOCALIP = "10.0.2.2";/*主机ip，相当于其他模拟器的ip*/
	public static int listenPort;/*其他设备需要与此设备连接时需要连接此端口*/
	private EmulatorServerThread emulatorServerThread;/*服务器线程，监听其他设备的连接请求*/
	
	public ConnectionManager(Context context, Handler handler,int port){
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.handler = handler;
		Log.i("fileshare","listenport:"+port);
		listenPort = port;
	}
	
	/*
	 * 启用severThread，开始监听连接请求
	 */
	public void start(){		
		if(FileShare.ISEMULATOR){
			Log.i("FileShare","fileshare:connection manager in emulatorserver start");
			if(emulatorServerThread == null){
				emulatorServerThread = new EmulatorServerThread();
				emulatorServerThread.start();
			}
		}
		else{
			if(serverThread == null){
				serverThread = new ServerThread();
				serverThread.start();
			}
			if(emulatorServerThread == null){
				Log.i("fileshare","fileshare:also start emulatorserver");
				emulatorServerThread = new EmulatorServerThread();
				emulatorServerThread.start();
			}
		}			
	}
	
	/*
	 * 停止监听连接请求
	 */
	public void stop(){
		if(serverThread != null)
			serverThread.cancel();
		if(emulatorServerThread != null)
			emulatorServerThread.cancel();
	}
	
	
	
	/*
	 * 向一个设备发起连接请求
	 */
	public void connect(BluetoothDevice device) {       
		ClientThread clientThread = new ClientThread(device);
		clientThread.start();
    }
	
	/*
	 *  与模拟器连接
	 */
	public void connectEmulator(int serverPort){
		EmulatorClientThread emulatorClientThread = new EmulatorClientThread(serverPort);
		emulatorClientThread.start();
	}
	
	/*
	 * 此线程用来监听其他设备发起的连接请求并建立连接
	 * （不用于模拟器）
	 */
	private class ServerThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public ServerThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.i("FileShare_ConnectionManager", "can not get BluetoothServerSocket");
            }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;

            /*监听连接请求，一旦发现有请求便建立一个连接*/
            while (true) {
                try {                   
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.i("FileShare_ConnectionManager_ServerThread", "accept fail");
                    break;
                }
                /*连接成功，向主界面发送“连接成功”,并传递相应的BluetoothSocket*/
                if (socket != null) {
                	Message msg = handler.obtainMessage(FileShare.MESSAGE_CONNECTION_SUCCESS);
    	            HashMap<String,Object> data = new HashMap<String,Object>();
    	            data.put("device", socket.getRemoteDevice());
    	            data.put("socket", socket);
                	msg.obj = data;
                	handler.sendMessage(msg);
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.i("FileShare_ConnectionManager", "can not close serverSocket");
            }
        }
    }
	
	/*
	 * 此线程用来向其他设备发送连接请求并建立连接
	 * （不用于模拟器）
	 */
	 private class ClientThread extends Thread{
		 BluetoothSocket socket;
		 BluetoothDevice device;
		 
		 public ClientThread(BluetoothDevice device) {
	            this.device = device;
	            BluetoothSocket tmp = null;

	            /*向指定设备获取一个用于连接的BluetoothSocket*/
	            try {
	                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	            } catch (IOException e) {
	                Log.i("FileShare_ConnectionManager","can not get bluetooth socket from"+device.getName());
	            }
	            socket = tmp;
	        }
		 
	     public void run(){
	    		bluetoothAdapter.cancelDiscovery();
	    		
	    		try {	                
	                socket.connect();
	            } catch (IOException e) {
	                /*向主界面发送“连接失败”*/
	            	Message msg = handler.obtainMessage(FileShare.MESSAGE_CONNECTION_FAIL);
	            	msg.obj = device;
	            	handler.sendMessage(msg);
	                try {
	                    socket.close();
	                } catch (IOException e2) {                  
	                }
	              
	                return;
	            }
	            /*向主界面发送“连接成功”，并传递相应的BluetoothSocket*/
	            Message msg = handler.obtainMessage(FileShare.MESSAGE_CONNECTION_SUCCESS);
	            HashMap<String,Object> data = new HashMap<String,Object>();
	            data.put("device", device);
	            data.put("socket", socket);
            	msg.obj = data;
            	handler.sendMessage(msg);
	    	}
	     public void cancel() {
	            try {
	                socket.close();
	            } catch (IOException e) {
	            }
	        }
	    }
	 
	 /*运行于模拟器上的服务器进程，监听其他设备发送过来的连接请求*/
	 private class EmulatorServerThread extends Thread{
		ServerSocket emulatorSocket;
		
		public EmulatorServerThread(){
			try{
				emulatorSocket = new ServerSocket(listenPort);
			}
			catch(IOException e){
				Log.i("FileShare_ConnectionManager","can not get emulatorSocket");
			}
		}
		
		public void run(){
			Log.i("FileShare_ConnectionManager","emulator server run");
			while(true){
				Socket socket = null;
				try {                   
					socket = emulatorSocket.accept();
	            } catch (IOException e) {
	                Log.i("FileShare_ConnectionManager_EmulatorServerThread", "accept fail");
	                break;
	            }
	            /*连接成功，向主界面发送“连接成功”,并传递相应的Socket*/
	            if (socket != null) {
	            	Log.i("fileshare","before read");
	            	byte[] buffer = new byte[20];
	            	int bytes = 0;
	            	try{
	            		bytes = socket.getInputStream().read(buffer);
	            	}
	            	catch(IOException e){}
	            	
	            	int port = Integer.valueOf(new String(buffer,0,bytes)).intValue();
	            	Log.i("fileshare","get port in server:"+port);
	                Message msg = handler.obtainMessage(FileShare.MESSAGE_CONNECTION_EMULATOR_SUCCESS);	    
	                msg.obj = socket;
	                msg.arg1 = port;
	                handler.sendMessage(msg);
	            }
			}
		}
		
		public void cancel() {
	           try {
	        	   emulatorSocket.close();
	            } catch (IOException e) {
	                Log.i("FileShare_ConnectionManager", "can not close emulatorSocket");
	            }
	        }
	 }
	 /*运行于模拟器上的连接设备的客户端进程*/
	 private class EmulatorClientThread extends Thread{
		 int serverPort;
		 
		 public EmulatorClientThread(int port){
			 serverPort = port;
		 }
		 
		 public void run(){
			 Socket socket = null;
			 try{
				 socket = new Socket(SERVERLOCALIP,serverPort);
				 /*向对方发送此模拟器监听的端口*/
				 socket.getOutputStream().write(new Integer(listenPort).toString().getBytes());
				 /*向主界面发送“连接成功”，并传递相应的Socket*/				 
		         Message msg = handler.obtainMessage(FileShare.MESSAGE_CONNECTION_EMULATOR_SUCCESS);		         
	             msg.obj = socket;
	             msg.arg1 = serverPort;
	             handler.sendMessage(msg);
	             
			 }
			 catch(IOException e){
				 /*向主界面发送“连接失败”*/
	            Message msg = handler.obtainMessage(FileShare.MESSAGE_CONNECTION_EMULATOR_FAIL);
	            msg.arg1 = serverPort;
	            handler.sendMessage(msg);
	            if(socket != null){
	            	try {
		                  socket.close();
		            } catch (IOException e2) {                  
		            }
	            }
	            
			 }			 
		 }
	 }
}
