package love.xiaobai.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 开机广播
 * 
 *
 *
 */

public class ServiceOMP extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			Intent send = new Intent(context, SendLocation2.class);
			send.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失败

			Bundle bd = new Bundle();
			bd.putString("location", "18662817528");
			send.putExtra("location", bd);
			context.startService(send);
			Log.v("----------------------", "手机开机啦~");
			Toast.makeText(context, "Open The Phone", Toast.LENGTH_SHORT)
					.show();

			//

		}

	}

	
	
	

}
