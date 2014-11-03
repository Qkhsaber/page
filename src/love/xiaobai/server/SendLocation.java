package love.xiaobai.server;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.SmsManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class SendLocation extends Service {
	String address ="";
	private Vibrator mVibrator01 = null;
	public LocationClient mLocClient = null;
	public MyLocationListenner myListener = new MyLocationListenner(); //
	ContextWrapper mContext = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Bundle b = intent.getBundleExtra("location");
		address = b.getString("location");

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		mVibrator01 = (Vibrator) getApplication().getSystemService(
				Context.VIBRATOR_SERVICE);
		setLocationOption();
		mLocClient.start();
		return startId;
	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); 
		option.setCoorType("bd09ll");
		option.setPoiExtraInfo(true); 
		option.setAddrType("all");
		option.setScanSpan(3000);
		option.setPriority(LocationClientOption.GpsFirst);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}

	protected boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public class MyLocationListenner implements BDLocationListener {
		JSONObject resultObj = null;
		JSONArray votes = null;

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			
			JSONObject locationInfo = new JSONObject();
			try {
				locationInfo.put("纬度", location.getLatitude());
				locationInfo.put("经度", location.getLongitude());
				locationInfo.put("精度", location.getRadius());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String messageText = locationInfo.toString();
			
			
			SmsManager smsManager = SmsManager.getDefault(); 
    		PendingIntent mPI = PendingIntent.getBroadcast(SendLocation.this, 0, new Intent(), 0);
    		smsManager.sendTextMessage(address, null,messageText, mPI, null);
			/*以上的发送的内容要再修改一下！！！*/
    		/*以上的发送的内容要再修改一下！！！*/
    		/*以上的发送的内容要再修改一下！！！*/
    		/*以上的发送的内容要再修改一下！！！*/
    		/*以上的发送的内容要再修改一下！！！*/
			
			
			
			mLocClient.stop();
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	public class NotifyLister extends BDNotifyListener {
		@Override
		public void onNotify(BDLocation mlocation, float distance) {
			mVibrator01.vibrate(1000);
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

}
