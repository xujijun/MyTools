package xjj.Android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GeoLocation {

	public class SCell{
	    public int MCC;
	    public int MNC;
	    public int LAC;
	    public int CID;
	    public int signalStrength;
	}
	
	public class MyLocation{
		public String latitude;
		public String longitude;
		public String accuracy;
		public String address;
	}

	public MyLocation getMyLocation(SCell cell) throws Exception {
   	
    	MyLocation myLocation = new MyLocation();
 
        /** 采用Android默认的HttpClient */
        HttpClient client = new DefaultHttpClient();
         
        /** 采用POST方法 */
        HttpPost post = new HttpPost("https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyAB0HWpWCVGxyhukac1sAgOiVW_cuUWab4");
        try {
            /** 构造POST的JSON数据 */
            JSONObject holder = new JSONObject();
            holder.put("homeMobileCountryCode", cell.MCC);
            holder.put("homeMobileNetworkCode", cell.MNC);
            holder.put("radioType", "gsm");
            holder.put("carrier", "cmcc");

            JSONObject tower = new JSONObject();
            tower.put("cellId", cell.CID);
            tower.put("locationAreaCode", cell.LAC);
            tower.put("mobileCountryCode", cell.MCC);
            tower.put("mobileNetworkCode", cell.MNC);
            tower.put("age", 0);
            tower.put("signalStrength", cell.signalStrength);
            tower.put("timingAdvance", 0);

            JSONArray towerarray = new JSONArray();
            towerarray.put(tower);
            holder.put("cellTowers", towerarray);

            /*
            JSONObject wifi = new JSONObject();
            wifi.put("macAddress", "01:23:45:67:89:AB");
            wifi.put("signalStrength", 8);
            wifi.put("age", 0);
            wifi.put("signalToNoiseRatio", -65);
            wifi.put("channel", 8);

            JSONArray wifiarray = new JSONArray();
            wifiarray.put(wifi);
            holder.put("wifiAccessPoints", wifiarray);
            */
            
            
            HttpEntity query = new StringEntity(holder.toString());
            post.setEntity(query);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            
            Log.i("Sent JSON:", holder.toString());
            
            /** 发出POST数据并获取返回数据 */
            HttpResponse response = client.execute(post);

			String resEntity = EntityUtils.toString(response.getEntity());
			// 生成 JSON 对象
			JSONObject json = new JSONObject(resEntity);

			Log.i("Received JSON:", json.toString());

            /** 解析返回的JSON数据获得经纬度 */
            JSONObject subjson = new JSONObject(json.getString("location"));
           
            myLocation.latitude = subjson.getString("lat");
            myLocation.longitude = subjson.getString("lng");
            myLocation.accuracy = json.getString("accuracy");
             
            //Log.i("Itude", itude.latitude + itude.longitude);
             
        } catch (Exception e) {
            Log.e(e.getMessage(), e.toString());
            throw new Exception("获取经纬度出现错误: "+e.getMessage());
        } finally{
            post.abort();
            client = null;
        }
         
        return myLocation;
    }
	
	public String getAddress(MyLocation myLoc) throws Exception{
		String address=null;
		
        /** 采用Android默认的HttpClient */
        HttpClient client = new DefaultHttpClient();
         
        /** 采用POST方法 */
        HttpPost post = new HttpPost("http://maps.googleapis.com/maps/api/geocode/json?latlng="+ myLoc.latitude +"," + myLoc.longitude + "&sensor=false&language=zh-CN");
        try {
            post.setHeader("Content-type", "application/json");
           /** 发出POST数据并获取返回数据 */
            HttpResponse response = client.execute(post);

			String resEntity = EntityUtils.toString(response.getEntity());
			// 生成 JSON 对象
			JSONObject json = new JSONObject(resEntity);

			Log.i("Received Address JSON:", json.toString());

			if(json.getString("status").equals("OK")){
	            /** 解析返回的JSON数据获得地址 */
	            JSONArray results = json.getJSONArray("results");
	            address = results.getJSONObject(0).getString("formatted_address");
			}
             
            Log.i("formatted_address", address);
             
        } catch (Exception e) {
            Log.e(e.getMessage(), e.toString());
            throw new Exception("获取经纬度出现错误: "+e.getMessage());
        } finally{
            post.abort();
            client = null;
        }
		
		return address;
	}
	
	
	public MyLocation getMyLocation_Sina(SCell cell) throws Exception {
	   	
    	MyLocation myLocation = new MyLocation();
 
        /** 采用Android默认的HttpClient */
        HttpClient client = new DefaultHttpClient();
         
        /** 采用POST方法 */
        HttpPost post = new HttpPost("https://api.weibo.com/2/location/mobile/get_location.json?source=792359858");
        try {
            /** 构造POST的JSON数据 */
            JSONObject holder = new JSONObject();
            holder.put("version", "2.0");			//请求版本信息
            holder.put("host", "api.weibo.com");	//请求地址信息
            holder.put("radio_type", "gsm");		//请求类型
            holder.put("request_address", true);	//是否需要返回详细地址，可选，默认：false
            holder.put("decode_pos", true);			//返回坐标是否偏移处理，偏移后坐标适合在新浪地图上使用（http://map.sina.com.cn），
			  										//不适用于其他地图（各地图偏移量不同，请谨慎处理）；可选，默认：false

            JSONObject tower = new JSONObject();
            tower.put("cell_id", cell.CID);
            tower.put("location_area_code", cell.LAC);
            tower.put("mobile_country_code", cell.MCC);
            tower.put("mobile_network_code", cell.MNC);
            tower.put("signalStrength", cell.signalStrength);

            JSONArray towerarray = new JSONArray();
            towerarray.put(tower);
            holder.put("cell_towers", towerarray);

            HttpEntity query = new StringEntity(holder.toString());
            post.setEntity(query);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            
            Log.i("Sent JSON:", holder.toString());
            
            /** 发出POST数据并获取返回数据 */
            HttpResponse response = client.execute(post);

			String resEntity = EntityUtils.toString(response.getEntity());
			// 生成 JSON 对象
			JSONObject json = new JSONObject(resEntity);

			Log.i("Received JSON:", json.toString());

            /** 解析返回的JSON数据获得经纬度 */
            JSONObject subjson = new JSONObject(json.getString("location"));  //ToDO: 判断是否有Location
           
            myLocation.latitude = subjson.getString("longitude");
            myLocation.longitude = subjson.getString("latitude");
            myLocation.accuracy = json.getString("accuracy");
            
            
             
            //Log.i("Itude", itude.latitude + itude.longitude);
             
        } catch (Exception e) {
            Log.e(e.getMessage(), e.toString());
            throw new Exception("获取经纬度出现错误: "+e.getMessage());
        } finally{
            post.abort();
            client = null;
        }
         
        return myLocation;
    }
}
