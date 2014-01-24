package mine.fileshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;  
import android.widget.SimpleAdapter;
import android.widget.TextView;  
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加共享文件时启动的文件浏览器
 *
 */
public class FileExplorer extends Activity {
	private final String STARTPATH = "/sdcard";
	
	private List<Map<String,Object>> fileList;
	private List<String> paths;
	private TextView textView;
	private ListView listView;
	private Button finishButton;
	private ArrayList<String> sharedFiles;
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.file_explorer);  
        
        textView = (TextView)this.findViewById(R.id.textView); 
        listView = (ListView)this.findViewById(R.id.listView);
        finishButton = (Button)this.findViewById(R.id.finishButton);
        fileList = new ArrayList<Map<String,Object>>();  
        sharedFiles = new ArrayList<String>();
        
        finishButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view){
        		String[] files = new String[sharedFiles.size()];
    	        for(int i = 0;i < sharedFiles.size();i++)
    	        	files[i] = sharedFiles.get(i);
    	        
    	        Intent intent = new Intent();
    	        intent.putExtra("filelist", files);
    	        
    	        setResult(Activity.RESULT_OK,intent);
    	        
    	        finish();
        	}
        });
        
        SimpleAdapter adapter = new SimpleAdapter(this,fileList,R.layout.filelist,
                                                  new String[]{"fileName"},
                                                  new int[]{R.id.file_name});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        	public void onItemClick(AdapterView<?>adapterView,View view,int position,long id){
        		String path = paths.get(position);
        		File file = new File(path);
        		if(file.isDirectory())
        			showFiles(path);
        		else if(file.isFile()){
        			sharedFiles.add(file.getPath());
        			Toast.makeText(getApplicationContext(), "文件："+file.getName()+" 已添加进共享文件列表", Toast.LENGTH_SHORT).show();
        			
        		}
        	}
        });
        
        this.showFiles(STARTPATH);
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
//        Log.i("fileshare","onDestroy() in FileExplorer");
    }
	
	/*
	 * 显示当前目录的文件列表
	 */
	private void showFiles(String filePath){
		try{  
            textView.setText("当前路径:"+filePath);
            
            fileList.clear();
            paths = new ArrayList<String>();  
            HashMap<String,Object> fileMap = new HashMap<String,Object>();
            
            File dir = new File(filePath);  
            File[] files = dir.listFiles();
            
            /* 如果不是起始目录,则列出"返回上层目录"选项*/
            if (!filePath.equals(STARTPATH)) {  
                fileMap.put("fileName", "返回上层目录");
                fileList.add(fileMap);
                paths.add(dir.getParent());  
            }  
            /*显示所有文件*/
            if(files != null){  
            	for(File file : files){
            		fileMap = new HashMap<String,Object>();
            		if(file.isDirectory())
            			fileMap.put("fileName", "<DIR> "+file.getName());
            		else if(file.isFile())
            			fileMap.put("fileName", "<FILE> "+file.getName());
            		fileList.add(fileMap);
            		paths.add(file.getPath());
            	}
            }
            
            listView.setVisibility(View.GONE);
    		SimpleAdapter adapter = (SimpleAdapter)listView.getAdapter();
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
        }catch(Exception e){  
            Log.e("fileshare","error in FileExplorer",e);
        }  
	}
}
