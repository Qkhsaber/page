package love.xiaobai.server;


import love.xiaobai.f.ever.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 演示MapView的基本用法
 */
public class BaseMapDemo extends Activity {

	final static String TAG = "MainActivity";
	 /*  MapView 是地图主控件  */
	private MapView mMapView = null;
	/*  用MapController完成地图控制  */
	private MapController mMapController = null;
	/*  MKMapViewListener 用于处理地图事件回调	 */
	MKMapViewListener mMapListener = null;
	/* 位置标记图层  */
	MyLocationOverlay myLocationOverlay = null;
	/* 位置数据对象  */
	LocationData locData = null;
	/* 预定义一组默认的地理坐标*/
	double cLat = 32.331706 ;// 纬度
	double cLon = 120.648635 ;// 经度
	double cPre = 0.0; // 精度 
	GeoPoint p ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
//              如果BMapManager没有初始化则初始化BMapManager
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
        setContentView(R.layout.activity_main_old);
        mMapView = (MapView)findViewById(R.id.bmapView);
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(17);
        mMapView.setBuiltInZoomControls(true);
        /**
         * 将地图移动至指定点
         * 使用百度经纬度坐标，可以通过http://api.map.baidu.com/lbsapi/getpoint/index.html查询地理坐标
         * 如果需要在百度地图上显示使用其他坐标系统的位置，请发邮件至mapapi@baidu.com申请坐标转换接口
         */
//        GeoPoint p ;
//       
//        Intent  intent = getIntent();
//        if ( intent.hasExtra("latitude") && intent.hasExtra("longitude")&& intent.getFlags()==1 ){
//        	//当用intent参数时，设置中心点为指定点
//        	Bundle b = intent.getExtras();
//        	cLat = Double.parseDouble(b.getString("latitude"));
//        	cLon = Double.parseDouble(b.getString("longitude"));
//        	p = new GeoPoint((int)(cLat*1E6), (int)(cLon*1E6));
//        }else{
//        	//设置默认坐标
//        	 p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
//        }
        
//        mMapController.setCenter(p);
        
        // 创建MapView的位置标记图层
        myLocationOverlay = new MyLocationOverlay(mMapView);
        // 为位置标记图层设置位置图标
        myLocationOverlay.setMarker(this.getResources().getDrawable(R.drawable.icon_geo));
//        // 创建位置数据对象
//        locData = new LocationData();
//        locData.latitude = cLat;// 设置位置数据对象中纬度
//        locData.longitude = cLon;// 设置位置数据对象中经度
//        myLocationOverlay.setData(locData);// 设置位置标记图层所在经纬度坐标
//        mMapView.getOverlays().add(myLocationOverlay);//将位置图层添加到地图视图的覆盖层管理列表中
//        mMapView.refresh();// 刷新地图视图，让覆盖层显示
//        mMapView.getController().animateTo(p);
        
        /**
    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
    	 */
        mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调
				 * 缩放，平移等操作完成后，此回调被触发
				 */
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件
				 * 显示底图poi名称并移动至该点
				 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					mMapController.animateTo(mapPoiInfo.geoPt);
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
				 *  可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
			}
            /**
             * 在此处理地图载完成事件 
             */
			@Override
			public void onMapLoadFinish() {
				
			}
		};
		mMapView.regMapViewListener(DemoApplication.getInstance().mBMapManager, mMapListener);
		
		
//		// 实现对地图状态改变的处理
//		MKMapStatusChangeListener listener = new MKMapStatusChangeListener() {
//			public void onMapStatusChange(MKMapStatus mapStatus) {
//				 mMapView.refresh();// 刷新地图视图，让覆盖层显示
//				mMapView.getController().animateTo(p);
//			}
//		};
//		// 为 mapview 注册地图状态监听者。
//		mMapView.regMapStatusChangeListener(listener);
//        

    }
    
    @Override 
    protected void onStart(){
    	super.onStart();
        Intent  intent = getIntent();
        if ( intent.hasExtra("latitude") && intent.hasExtra("longitude")){
        	//当用intent参数时，设置中心点为指定点
        	Bundle b = intent.getExtras();
        	cLat = b.getDouble("latitude");
        	cLon = b.getDouble("longitude");
        	cPre = b.getDouble("precision");
        	p = new GeoPoint((int)(cLat*1E6), (int)(cLon*1E6));
        }else{
        	//设置默认坐标
        	 p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
        }
        
        mMapController.setCenter(p);
        
//         创建MapView的位置标记图层
//        myLocationOverlay = new MyLocationOverlay(mMapView);
//         为位置标记图层设置位置图标
//        myLocationOverlay.setMarker(this.getResources().getDrawable(R.drawable.icon_geo));
//         创建位置数据对象
        locData = new LocationData();
        locData.latitude = cLat;// 设置位置数据对象中纬度
        locData.longitude = cLon;// 设置位置数据对象中经度
        myLocationOverlay.setData(locData);// 设置位置标记图层所在经纬度坐标
        mMapView.getOverlays().add(myLocationOverlay);//将位置图层添加到地图视图的覆盖层管理列表中
        mMapView.refresh();// 刷新地图视图，让覆盖层显示
        mMapView.getController().animateTo(p);
    }
    
    @Override
    protected void onPause() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
    	 */
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
    	 */
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
    	 */
        mMapView.destroy();
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
}
