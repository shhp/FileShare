package mine.fileshare;

import android.bluetooth.BluetoothDevice;

/**
 * Device类存储一个设备的相关信息，包括：
 * 名称、物理地址、连接状态
 *
 */
public class Device {
	BluetoothDevice device;
	boolean connection_state;
	int port;
	
	public Device(BluetoothDevice device,boolean connection_state,int port){
		this.device = device;
		this.connection_state = connection_state;
		this.port = port;
	}
	
	public boolean equals(Object o){
		Device d = (Device)o;
		if((this.device == null && d.device != null) || (this.device != null && d.device == null))
			return false;
		else if(this.device == null && d.device == null)
			return this.port == d.port;
		else
			return this.device.getAddress().equalsIgnoreCase(d.device.getAddress());
	}
}
