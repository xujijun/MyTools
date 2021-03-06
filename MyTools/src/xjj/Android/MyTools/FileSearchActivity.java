package xjj.Android.MyTools;

import java.io.File;

import xjj.Android.MyTools.R;
import android.app.Activity;
//import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FileSearchActivity extends Activity{
	
	private TextView TextViewFileSearchResult;
	private Button ButtonFileSearch;
	private EditText EditTextKeyword;
	private Button ButtonReturn;
	
	private File file;
	private String path;
	private String keyword;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) { // 处理消息
			switch (msg.what) {
			case 1:// 查找下个目录
				TextViewFileSearchResult.setText(TextViewFileSearchResult
						.getText() + ".");
				break;
			case 2:// 返回结果了
				TextViewFileSearchResult.setText("搜索到的路径：" + path);
			    
			/*	new AlertDialog.Builder(FileSearchActivity.this)
			     .setTitle("手机信息")
			     .setMessage("搜索到的路径：" + path)
			     .setPositiveButton("好的！", null)
			     .show(); 
			*/	
				break;
			default:
				break;
			}

			//super.handleMessage(msg);
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_file_search);
		
		
		EditTextKeyword = (EditText) findViewById(R.id.editTextkeyword);
		TextViewFileSearchResult = (TextView) findViewById(R.id.textViewFileSearchResult);
		TextViewFileSearchResult.setMovementMethod(ScrollingMovementMethod.getInstance()); //让TextView垂直滚动 ，配合 xml文件里面的  android:scrollbars="vertical"
		
		ButtonReturn = (Button)this.findViewById(R.id.buttonReturn);
		ButtonReturn.setOnClickListener(new View.OnClickListener() {//返回主界面
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(FileSearchActivity.this, MainActivity.class));
				FileSearchActivity.this.finish();
			}
		});
			
		ButtonFileSearch = (Button) findViewById(R.id.buttonFileSearch);				
		ButtonFileSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				keyword = EditTextKeyword.getText().toString();
				
				if(keyword.equals("")){//没有输入关键字
					TextViewFileSearchResult.setText("请输入关键字!");
					return;
				}
				else{
					//file = new File("/sdcard");
					file = new File(Environment.getExternalStorageDirectory().getPath());//SD卡路径
					
					if(file==null){
						TextViewFileSearchResult.setText("根目录错误:" + Environment.getExternalStorageDirectory().getPath());
						return;
					}
					
					TextViewFileSearchResult.setText("请稍候");
					
					Thread thread=new Thread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							//初始化：
							path = "";
							
							SearchFile(file);

							//发送结果“path”，what=2
							mHandler.removeMessages(0); 
							Message m = mHandler.obtainMessage(2, 1, 1, path);
							mHandler.sendMessage(m);
						}
					});
					
			        thread.start(); 
				}	

			}//onClick
		});//ButtonFileSearch.setOnClickListener
	}//onCreate


	public void SearchFile(File root){//递归搜索说有路径
		File[] files = root.listFiles();
		
		for(File tempF : files){
			if(tempF.isDirectory()){
				mHandler.removeMessages(0); 
				Message m = mHandler.obtainMessage(1, 1, 1, null);
				mHandler.sendMessage(m); 

				SearchFile(tempF); //递归调用
			}
			else{
				try{
					if(tempF.getName().indexOf(keyword) > -1){
						path += "\n" + tempF.getPath(); //找到一个符合条件的，加到Path里面
						//TextViewFileSearchResult.setText("搜索到的文件：" + path);
					}
				} catch (Exception e){//找不到路径
					path = "搜索出错！";
					//Toast.makeText(FileSearchActivity.this, "读取路径出错！", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}//SearchFIle
}
