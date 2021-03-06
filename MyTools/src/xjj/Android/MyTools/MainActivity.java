package xjj.Android.MyTools;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import xjj.Android.GeoLocation;
import xjj.Android.GeoLocation.SCell;
import xjj.Android.GeoLocation.MyLocation;
import xjj.Android.Mail;
import xjj.Android.MyTools.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
//import android.widget.Toast;
//import android.widget.TextView; 
import android.content.Context;
import android.hardware.Camera;
import android.telephony.gsm.GsmCellLocation;  
import android.text.format.DateFormat;


public class MainActivity extends Activity {

	private TextView tvStatus;
	private TelephonyManager telMgr;
	private Camera camera;
	private Camera.Parameters mParameters;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		telMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE); 
		
		tvStatus = (TextView)this.findViewById(R.id.textViewStatus);
		
		Button buttonGoToFileSearch = (Button)this.findViewById(R.id.buttonGoToFileSearch);
		buttonGoToFileSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this, FileSearchActivity.class));
				MainActivity.this.finish();
			}
		});

		
		Button buttonGoToTaxCalculation = (Button)this.findViewById(R.id.buttonGoToIncomeTaxCalculation);
		buttonGoToTaxCalculation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this, IncomeTaxCalculationActivity.class));
				MainActivity.this.finish();
			}
		});

		Button buttonTest = (Button)this.findViewById(R.id.buttonTest);
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 /** 采用Android默认的HttpClient */
				
				new Thread() {
					@Override
					public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					String url = "https://api.weibo.com/2/statuses/update.json?source=792359858&status=";
				
					URLEncoder.encode("系统自动发送。。。测试。。。", "UTF-8");

					/** 采用POST方法 */
					HttpPost post = new HttpPost(url);

				
					/** 发出POST数据并获取返回数据 */
					HttpResponse response = client.execute(post);
					post.setHeader("Content-type", "application/json");
					String resEntity = EntityUtils.toString(response.getEntity());
					// 生成 JSON 对象
					JSONObject json = new JSONObject(resEntity);

					Log.i("Received JSON:", json.toString());
					
				} catch (Exception e) {
					Log.e(e.getMessage(), e.toString());
				}
					}
					}.start();	
				
			}

		});

		
		Button buttonScreenResolution = (Button)this.findViewById(R.id.buttonScreenResolution);
		buttonScreenResolution.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 DisplayMetrics dm = new DisplayMetrics();   
			     getWindowManager().getDefaultDisplay().getMetrics(dm); 
			     //tv.setText("屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels);
			     //Toast.makeText(MainActivity.this, "屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels, Toast.LENGTH_LONG).show();
			     
			     String s = "屏幕分辨率为:"+dm.widthPixels+" * "+dm.heightPixels + "\n";
			     
			     List<NeighboringCellInfo> cell_infos = telMgr.getNeighboringCellInfo();  
			     StringBuffer sb = new StringBuffer("已知相邻小区总数 : " + cell_infos.size() + "\n");  
			     for (NeighboringCellInfo cell_info : cell_infos) { // 根据邻区总数进行循环   
			         sb.append(" LAC : " + cell_info.getLac()); // 取出当前邻区的LAC   
			         sb.append(" CID : " + cell_info.getCid()); // 取出当前邻区的CID   
			         sb.append(" BSSS : " + (-113 + 2 * cell_info.getRssi()) + "\n"); // 获取邻区基站信号强度   
			     }  

			     s += sb;
			     
			     s += DateFormat.format("\nyyyy年M月d日 kk:mm:ss EEEE", new Date()).toString();
			     
			     new AlertDialog.Builder(MainActivity.this)
			     .setTitle("屏幕分辨率")
			     .setMessage(s)
			     .setPositiveButton("知道了！", null)
			     .show();
			     
			}
		});
		
		
		Button buttonRecordLocation = (Button)this.findViewById(R.id.buttonRecordLocation);
		buttonRecordLocation.setOnClickListener(new View.OnClickListener() {

			// 定义Handler对象
			private Handler mHandler = new Handler() {
				@Override
				// 当有消息发送出来的时候就执行Handler的这个方法
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case 0: {
						tvStatus.setText(tvStatus.getText() + "Done!\n" + msg.obj);

						new AlertDialog.Builder(MainActivity.this)
								.setTitle("位置已经记录")
								.setMessage("已经把当前位置发送到Google邮箱。" + msg.obj)
								.setPositiveButton("Very Good！", null).show();

						break;
					}
					case 1: {// 进度
						tvStatus.setText(tvStatus.getText()
								+ msg.obj.toString());
						break;
					}
					case 2: {// 发送邮件失败
						tvStatus.setText(tvStatus.getText()
								+ "发生失败！\n" + msg.obj.toString());
						
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("失败").setMessage("邮件发送失败")
								.setPositiveButton("真的吗？不可能吧？", null).show();

						break;
					}
					case 3: {// exception
						tvStatus.setText(tvStatus.getText()
								+ msg.obj.toString());
						break;
					}
					case 4: {// exception
						tvStatus.setText(tvStatus.getText() + "GPS定位发生错误！");
						Toast.makeText(MainActivity.this, "GPS定位发生错误",
								Toast.LENGTH_LONG).show();
						break;
					}
					case 5: {// exception
						tvStatus.setText(tvStatus.getText() + "基站定位发生错误！可能是网络问题。");
						Toast.makeText(MainActivity.this, "基站定位发生错误",
								Toast.LENGTH_LONG).show();
						break;
					}
					case 6: {// exception
						tvStatus.setText(tvStatus.getText() + "获取详细地址时发生错误！");
						Toast.makeText(MainActivity.this, "获取详细地址时发生错误",
								Toast.LENGTH_LONG).show();
						break;
					}
					}
					// 处理UI
				}
			};

		@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO: 代码整理

				tvStatus.setText("请稍候……");

				new Thread() {
					@Override
					public void run() {

							GeoLocation geoLocation = new GeoLocation();
							SCell cell = geoLocation.new SCell();
							MyLocation myLocation = geoLocation.new MyLocation();
							Message msg;
							String myAddress = null;
							String result = null;

							boolean isGPSLocation = false;
							boolean isSinaMap = false;

						try {
							// GPS Locating:
							LocationManager loctionManager;
							// 通过系统服务，取得LocationManager对象
							loctionManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

							if (loctionManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
								Log.i("0", "GPS定位");
								msg = mHandler.obtainMessage(1,	"正在通过GPS定位经纬度……");
								mHandler.sendMessage(msg);

								// "GPS 定位", Toast.LENGTH_SHORT).show();
								Location loc = loctionManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

								if (loc != null) {
									myLocation.latitude = String.valueOf(loc.getLatitude());
									myLocation.longitude = String.valueOf(loc.getLongitude());
									myLocation.accuracy = String.valueOf(loc.getAccuracy());
									isGPSLocation = true;
									
									msg = mHandler.obtainMessage(1,	"Done!");
									mHandler.sendMessage(msg);
								}
								else{
									//mHandler.removeMessages(0);
									msg = mHandler.obtainMessage(1,	"Failed!");
									mHandler.sendMessage(msg);
								}
							}

						} catch (Exception e) {
							mHandler.sendEmptyMessage(4);
							Log.e("MyTools", "GPS locating error", e);
						}
							
					if(isSinaMap==true){//通过Sina MAP API 查找位置
						try {
							if (isGPSLocation == false) {
								
								msg = mHandler.obtainMessage(1,	"\n正在获取基站信息……");
								mHandler.sendMessage(msg);
								
								// Cell Locating if GPS is not turned on：
								// 返回值MCC + MNC
								String operator = telMgr.getNetworkOperator();
								cell.MCC = Integer.parseInt(operator.substring(0, 3)); // 第一到三位
								cell.MNC = Integer.parseInt(operator.substring(3)); // 第四位开始

								// 中国移动和中国联通获取LAC、CID的方式
								GsmCellLocation cellLocation = (GsmCellLocation) telMgr.getCellLocation();
								cell.LAC = cellLocation.getLac();
								cell.CID = cellLocation.getCid();
								cell.signalStrength = -60;

								msg = mHandler.obtainMessage(1,	"Done!\n正在根据基站查询经纬度(Sina)……");
								mHandler.sendMessage(msg);

								myLocation = geoLocation.getMyLocation_Sina(cell);
							}

						} catch (Exception e) {
							mHandler.sendEmptyMessage(5);
							Log.e("MyTools", "Cell locating error", e);
						}
					}
					else{
						try {
							if (isGPSLocation == false) {
								
								msg = mHandler.obtainMessage(1,	"\n正在获取基站信息……");
								mHandler.sendMessage(msg);
								
								// Cell Locating if GPS is not turned on：
								// 返回值MCC + MNC
								String operator = telMgr.getNetworkOperator();
								cell.MCC = Integer.parseInt(operator.substring(0, 3)); // 第一到三位
								cell.MNC = Integer.parseInt(operator.substring(3)); // 第四位开始

								// 中国移动和中国联通获取LAC、CID的方式
								GsmCellLocation cellLocation = (GsmCellLocation) telMgr.getCellLocation();
								cell.LAC = cellLocation.getLac();
								cell.CID = cellLocation.getCid();
								cell.signalStrength = -60;

								msg = mHandler.obtainMessage(1,	"Done!\n正在根据基站查询经纬度(Google)……");
								mHandler.sendMessage(msg);

								myLocation = geoLocation.getMyLocation(cell);
							}

						} catch (Exception e) {
							mHandler.sendEmptyMessage(5);
							Log.e("MyTools", "Cell locating error", e);
						}
							
						try {	
							msg = mHandler.obtainMessage(1,	"Done!\n正在获取详细地址……");
							mHandler.sendMessage(msg);

							myAddress = geoLocation.getAddress(myLocation);
							
						} catch (Exception e) {
							mHandler.sendEmptyMessage(6);
							Log.e("MyTools", "Retriving detailed location error", e);
						}
					}//if(SinaMap)		
						
						
						try {	
							msg = mHandler.obtainMessage(1,	"Done!\n正在发送eMail……");
							mHandler.sendMessage(msg);

							Mail m = new Mail("i.am.on.my.trip@gmail.com", "Gle89jin");
							// Location location = getCurrentLocation();

							// String[] toArr = { "clementad.xu@gmail.com",
							// "lala@lala.com" };
							String[] toArr = { "clementad.xu@gmail.com" };
							m.set_to(toArr);
							m.set_from("WhereAmI@gmail.com");
							m.set_subject("手机位置报告");

							// String s = "时间：" + String.valueOf(new Date())
							// + "；(经度，纬度)：(" +
							// String.valueOf(location.getLongitude()) +" , " +
							// String.valueOf(location.getLatitude()) + ")";

							result = "时间：" + DateFormat.format("\nyyyy年M月d日 kk:mm:ss EEEE", new Date()).toString()
									+ "\n(经度，纬度，精确度(米))：(" + myLocation.longitude + " , "+ myLocation.latitude + " , "+ myLocation.accuracy + ")"
									+ "\n(LAC，CID)：(" + String.valueOf(cell.LAC) + " , " + String.valueOf(cell.CID) + ")"
									+ "\n地址("+ myLocation.accuracy +"米范围内)：" + myAddress
									+ "\n地图查询：http://maps.google.com/maps?q=" + myLocation.latitude + "+" + myLocation.longitude;

							m.set_body(result);

							// 发送附件
							// m.addAttachment("/sdcard/filelocation");

							if (m.send()) {
								msg = mHandler.obtainMessage(0, result);
								mHandler.sendMessage(msg);

							} else {
								msg = mHandler.obtainMessage(2, result);
								mHandler.sendMessage(msg);

							}

						} catch (Exception e) {
							msg = mHandler.obtainMessage(3, result);
							mHandler.sendMessage(msg);

							Log.e("MyTools", "Could not send email", e);
						}

						// 执行完毕后给handler发送一个空消息
						// mHandler.sendEmptyMessage(0);
					}
				}.start();

			}
		});
		

		Button buttonPhoneInfo = (Button)this.findViewById(R.id.buttonPhoneInfo);
		buttonPhoneInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
      
				String s;

				DisplayMetrics dm = new DisplayMetrics();   
				getWindowManager().getDefaultDisplay().getMetrics(dm); 
			    s = "屏幕分辨率："+dm.widthPixels+" * "+dm.heightPixels;

				if(telMgr==null)
			    	 s= "telMgr=null";
	     
			     s += "\nIMEI："+ telMgr.getDeviceId();
			     s = s + "\nIMEISV：" + telMgr.getDeviceSoftwareVersion();
			   	 s = s + "\n电话号码：" + telMgr.getLine1Number();
			   	 s = s + "\nIMSI：" + telMgr.getSubscriberId();
			   	 s = s + "\n国家代码：" + telMgr.getNetworkCountryIso();
			   	 s = s + "\n运营商：" + telMgr.getNetworkOperatorName();
			   	 s = s + "\n运营商：" + telMgr.getSimOperatorName();
			   	 s = s + "\nSIM卡系列号：" + telMgr.getSimSerialNumber();
			   	 s = s + "\nMCC+MNC：" + telMgr.getNetworkOperator();
			   	
			   	 int simState = telMgr.getSimState();
			   	 String ss = "";
			   	 
			   	 switch(simState){
			   	 case 0: ss="未知状态";break;
			   	 case 1: ss="没插卡";break;
			   	 case 2: ss="锁定状态，需要用户的PIN码解锁";break;
			   	 case 3: ss="锁定状态，需要用户的PUK码解锁";break;
			   	 case 4: ss="锁定状态，需要网络的PIN码解锁";break;
			   	 case 5: ss="就绪状态";break;
			   	 default : ss = "其他状态";	
			   	}
			   	 
			   	s = s + "\nSIM卡状态：" + ss;
			   	
			   	if(telMgr.isNetworkRoaming()==true)
			   		s = s + "\n是否漫游：是";
			   	else
			   		s = s + "\n是否漫游：否";
			   	 
			   	 int networkType = telMgr.getNetworkType();
			   	 String nt = "";
			   	 
			   	 switch (networkType){
			   	 case 0:
			   		nt = "网络类型未知"; break;
			   	 case 1:
			   		nt = "GPRS网络"; break;
			   	 case 2:
			   		nt = "EDGE网络"; break;
			   	 case 3:
			   		nt = "UMTS网络"; break;	
			   	 case 4:
			   		nt = "CDMA网络,IS95A 或 IS95B"; break;
			   	 case 5:
			   		nt = "EVDO网络, revision 0"; break;
			   	 case 6:
			   		nt = "EVDO网络, revision A"; break;
			   	 case 7:
			   		nt = "1xRTT网络"; break;	
			   	 case 8:
			   		nt = "HSDPA网络"; break;
			   	 case 9:
			   		nt = "HSUPA网络"; break;
			   	 case 10:
			   		nt = "HSPA网络"; break;		
			   	 default : nt = "其他网络类型";	
			    }//switch
			   	 
			   	 s = s + "\n网络类型：" + nt;
			   	 
			   	 int phoneType = telMgr.getPhoneType();
			   	 String pt = "";
			   	 
			   	 switch(phoneType){
			   	 case 0: pt="无信号"; break;
			   	 case 1: pt="GSM"; break;
			   	 case 2: pt="CDMA"; break;
			   	 case 3: pt="SIP"; break;
			   	 default : pt = "其他";	
			   	 }
			   	 
			   	s = s + "\n手机类型：" + pt;
			   	
			   	int dataState = telMgr.getDataState();
			   	String ds = "";
			   	
			   	switch(dataState){
			   	case 0: ds = "Disconnected. IP traffic not available.";break;
			   	case 1: ds = "Currently setting up a data connection.";break;
			   	case 2: ds = "Connected. IP traffic should be available.";break;
			   	case 3: ds = "The connection is up, but IP traffic is temporarily unavailable.";break;
			   	}
			   	
			   	s = s + "\n数据连接状态：" + ds;
			   	
			   	int dataActivity = telMgr.getDataActivity();
			   	String da = "";
			   	
			    switch(dataActivity){
			    case 0: da = "No traffic.";break;
			    case 1: da = "Currently receiving IP PPP traffic.";break;
			    case 2: da = "Currently sending IP PPP traffic.";break;
			    case 3: da = "Currently both sending and receiving IP PPP traffic.";break;
			    case 4: da = "Data connection is active, but physical link is down.";break;
			    }
			    
			    s = s + "\n数据传输状态：" + da;
			    
			    CellLocation cellLocation = telMgr.getCellLocation();
			    s = s + "\nCID，LAC： " + String.valueOf(cellLocation);
			    
			    //获得最后一次记录的经纬度:
			    Location location = getCurrentLocation();
			    s += "\n经度：" + String.valueOf(location.getLongitude())
			    	+ "；纬度："+String.valueOf(location.getLatitude())
			    	+ "；海拔："+String.valueOf(location.getAltitude());
   			    			     
			    //tv.setText(s); 
			    			    
			    new AlertDialog.Builder(MainActivity.this)
			     .setTitle("手机信息")
			     .setMessage(s)
			     .setPositiveButton("太好了！", null)
			     .show(); 
			}//onclick
		});
		
		//手电筒：
		ToggleButton toggleButtonFlashlight = (ToggleButton)this.findViewById(R.id.toggleButtonFlashlight);

		toggleButtonFlashlight.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					try  
		            {  

		                camera = Camera.open(); 
		                if(camera==null)
		                	Toast.makeText(getApplicationContext(), "获取摄像头错误！"+camera.toString(), Toast.LENGTH_SHORT).show();
		               
		                mParameters = camera.getParameters();  
		                mParameters.setFocusMode("infinity");
		                camera.setParameters(mParameters);
		                
		                //mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON); 
		                
		                mParameters = camera.getParameters();
		                mParameters.setFlashMode("torch"); 
		                camera.setParameters(mParameters); 

		                //一定要先设定一个Surfaceview才能startPreview，Surfaceview的高和宽都是一个点
						SurfaceHolder localSurfaceHolder = ((SurfaceView)findViewById(R.id.surfaceViewCamera)).getHolder();
						camera.setPreviewDisplay(localSurfaceHolder);
		                camera.startPreview(); // 开始亮灯
		                Toast.makeText(getApplicationContext(), "打开了手电筒", Toast.LENGTH_SHORT).show();

		            }catch(Exception ex){}
				}
				else{
					try  
		            {  
					    mParameters = camera.getParameters();  
		                mParameters.setFlashMode("off");  
		                camera.setParameters(mParameters);  
		                camera.stopPreview(); //关掉亮灯
		                camera.release();
		                
		                Toast.makeText(getApplicationContext(), "关闭了手电筒", Toast.LENGTH_SHORT).show();

		            }catch(Exception ex){} 
				}
			}
		
			
		});
		
		
		Button buttonExit = (Button)this.findViewById(R.id.buttonExit);
		buttonExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
		});
		
	
	}//onCreate()

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	//A private method to get the last known location
	private Location getCurrentLocation(){
		LocationManager loctionManager;
	    //通过系统服务，取得LocationManager对象
	    loctionManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    if(loctionManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
	    	Log.i("0", "GPS定位");
	    	//Toast.makeText(getApplicationContext(), "GPS 定位", Toast.LENGTH_SHORT).show();
            return loctionManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
	    }
	    else{
	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
	    criteria.setAltitudeRequired(true);//不要求海拔
	    criteria.setBearingRequired(false);//不要求方位
	    criteria.setCostAllowed(true);//允许有花费
	    criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
	    //从可用的位置提供器中，匹配以上标准的最佳提供器
	    String provider = loctionManager.getBestProvider(criteria, true);
	    
	    Log.i("1", "基站定位");
	    //Toast.makeText(getApplicationContext(), "基站定位", Toast.LENGTH_SHORT).show();
	    //获得最后一次变化的位置
	    return loctionManager.getLastKnownLocation(provider);
	    }  
	}//getCurrentLocation

}
