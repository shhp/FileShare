package mine.fileshare;

import android.util.Log;

import java.io.Serializable;

/**
 * 获取文件的请求信息，包括发出请求的设备标识、请求序号
 * 和请求的文件的基本信息
 */
public class GetFileRequest implements Serializable{
	String sourceID;
	int requestNum;
	FSFile file;

	public GetFileRequest(String sourceID,int requestNum,FSFile file){
		this.sourceID = sourceID;
		this.requestNum = requestNum;
		this.file = file;
	}
	
	public boolean equals(Object o){
		//Log.i("fileshare","GetFileRequest:call equals");
		GetFileRequest request = (GetFileRequest)o;
		return this.sourceID.equals(request.sourceID) && this.requestNum == request.requestNum;
	}
	
}
