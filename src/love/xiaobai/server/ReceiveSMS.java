package love.xiaobai.server;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.baidu.mapapi.map.LocationData;

public class ReceiveSMS extends BroadcastReceiver {
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	LocationData locData = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(mACTION)) {
			String address = null;// 收到的短信来自哪个电话号码
			String content = null;// 收到的短信内容
			String time = null;// 收到的短信时间戳
			boolean isLocationSMS = false;// 收到的短信是否是本软件的位置信息
			boolean hasLatitude = false;// 短信内容中是否包含纬度字段
			boolean hasLongitude = false;// 短信内容中是否包含经度字段
			boolean hasPrecision = false;// 短信内容中是否包含精度字段

			StringBuilder sb = new StringBuilder();// 短信内容字条串缓冲
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] myOBJpdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
				for (int i = 0; i < myOBJpdus.length; i++) {
					messages[i] = SmsMessage
							.createFromPdu((byte[]) myOBJpdus[i]);
				}
				for (SmsMessage currentMessage : messages) {
					sb.append("来自:\n");
					address = currentMessage.getDisplayOriginatingAddress();
					sb.append(address);
					sb.append("\n内容：\n");
					content = currentMessage.getDisplayMessageBody();
					sb.append(content);
					sb.append("\n时间：\n");
					SimpleDateFormat sdf = new SimpleDateFormat();
					time = sdf.format(currentMessage.getTimestampMillis());
					sb.append(time);

				}
			}

			Intent i = new Intent(context, BaseMapDemo.class);
			String location = content.toString();
			try {
				JSONObject object = new JSONObject(location);
				if (object.has("纬度")) {
					i.putExtra("latitude", object.getDouble("纬度"));
					hasLatitude = true;
				}
				if (object.has("经度")) {
					i.putExtra("longitude", object.getDouble("经度"));
					hasLongitude = true;
				}
				if (object.has("精度")) {
					i.putExtra("precision", object.getDouble("精度"));
					hasPrecision = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			isLocationSMS = hasLatitude && hasLongitude && hasPrecision;
			if (isLocationSMS) {// 如果是本软件格式的位置短信，则发送启动地图意图
				i.setFlags(1);
				// 这是从Activity外执行startActivity()方法必须为意图添加的标志
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
			if (location.equals("getlocation")) {
				Intent send = new Intent(context,SendLocation.class);
				Bundle bd = new Bundle();
				bd.putString("location", address);
				send.putExtra("location", bd);
				context.startService(send);
				
				

			}
		}
	}
}