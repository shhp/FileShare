package mine.fileshare;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * FSFile用于表示一个共享文件，包含了一个文件的基本信息
 *
 */
public class FSFile implements Serializable{
	String fileName;
	//文件的实际拥有者
	String actualOwner;
	//文件传输路径上的中转设备，即请求此文件时请求应该转发到的设备
	String transitDevice;
	//文件从实际拥有者传递到本设备所需经历的跳数
	int hop;
	//其他人共享的文件在本设备上是否有副本
	boolean gotten = false;
	//其他人共享的文件在本设备上是否有缓存
	boolean cached = false;
	//文件标签
	ArrayList<String> tags;
	
	public FSFile(String fileName,String actualOwner,String transitDevice,int hop){
		this.fileName = fileName;
		this.actualOwner = actualOwner;
		this.transitDevice = transitDevice;
		this.hop = hop;
		this.tags = new ArrayList<String>();
	}
	
	public boolean equals(Object o){
		FSFile file = (FSFile)o;
		return this.fileName.equals(file.fileName) && this.actualOwner .equals(file.actualOwner);
	}

}
