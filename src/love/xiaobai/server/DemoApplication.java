package love.xiaobai.server;


import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

/**
 * 本类是定制的 Application子类，用于对全局状态的管理
 * @author 贾震斌
 *
 */
public class DemoApplication extends Application {
	
    private static DemoApplication mInstance = null;// 声明本类的静态对象名
    public boolean m_bKeyRight = true;////  全局应用参数授权码是否验证通过标志
    BMapManager mBMapManager = null;//地图引擎管理类
    
    // 定义向百度申请的地图应用Key
    public static final String strKey = "8BQ4pGojT47dDrvLh3OA5s8O";
	
	@Override
    public void onCreate() {// 本回调方法将在Avtivity、Service、BroadCastReceiver启动前调用
	    super.onCreate();
		mInstance = this;// 本类实例对象转为静态对象
		initEngineManager(this);
		Log.i("DemoApplication", "onCreate()正在启动");
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);// 创建地图引擎管理类实例对象
        }
        //     strKey - 申请的授权验证码        listener - 该监听接口返回网络状态，授权验证等结果，用户需要实现该接口以处理相应事件
        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(DemoApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	// 取得本类静态对象
	public static DemoApplication getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override//返回网络错误
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        /**
         * 返回授权验认证状态码
         * 参数：
         *    iError - 0： 认证通过， 非零值表表示认证失败。
         *    -300: 无法建立与服务端的连接。
         *    -200: 服务端数据错误，无法解析服务端返回数据。
         *    其他返回值
         */
        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                
                DemoApplication.getInstance().m_bKeyRight = false;//  设置全局应用参数授权码为错误
            }
            else{
            	DemoApplication.getInstance().m_bKeyRight = true;//  设置全局应用参数授权码为正确
            	
            }
        }
    }
}