package mine.fileshare;

import java.io.Serializable;

/**
 * 查找文件请求，包括发出请求的设备标识、请求序号
 * 和请求的文件标签
 */
public class FileSearchRequest implements Serializable{
	String sourceID;
	int requestNum;
	String tag;

	public FileSearchRequest(String sourceID,int requestNum,String tag){
		this.sourceID = sourceID;
		this.requestNum = requestNum;
		this.tag = tag;
	}
	
	public boolean equals(Object o){
		FileSearchRequest request = (FileSearchRequest)o;
		return this.sourceID.equals(request.sourceID) && this.requestNum == request.requestNum;
	}
}
