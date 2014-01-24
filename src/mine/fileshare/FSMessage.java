package mine.fileshare;

import java.io.Serializable;

/**
 * FSMessage是对设备间传递的消息数据的封装
 *
 */
public class FSMessage implements Serializable{
	public static final int ADD_FILES = 1,
	                        DEVICE_DEPARTURE = 2,
	                        GET_FILE = 3,
	                        TRANSFER_FILE_READY = 4,
	                        FILE_NOT_EXIST = 5,
	                        SEARCH_FILE = 6,
	                        SEARCH_RESULT = 7,
	                        UPDATE_TAG = 8;
	
	//标志域
	int flag;
	//数据域
	Object data;
	
	public FSMessage(int flag,Object data){
		this.flag = flag;
		this.data = data;
	}
}
