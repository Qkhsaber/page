package love.xiaobai.face;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import love.xiaobai.f.ever.R;
import love.xiaobai.server.BMapUtil;
import love.xiaobai.server.GeoCoderDemo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity {

	int num = 0;

	// __________________________________________________________________________________________________________
	private enum E_BUTTON_TYPE {
		LOC, // 手动请求定位图层模式
		COMPASS, // 罗盘定位图层模式
		FOLLOW// 跟随定位图层模式
	}

	private E_BUTTON_TYPE mCurBtnType;// 声明一个定位图层模式枚举对象
	LocationClient mLocClient;// 定位SDK的核心类
	LocationClient mLocClient1;
	// 声明保存定位位置信息对象，信息包括：定位精度、方向角度、百度纬度坐标、百度经度坐标、卫星数目、速度
	LocationData locData = null;
	LocationData locData1 = null;
	// 创建定位监听对象
	public MyLocationListenner myListener = new MyLocationListenner();
	public MyLocationListenner myListener1 = new MyLocationListenner();
	// 声明显示用户当前位置的泡泡图层对象
	locationOverlay myLocationOverlay = null;
	locationOverlay myLocationOverlay1 = null;
	// 声明一个pop弹窗对象，弹出泡泡图层
	private PopupOverlay pop = null;// 弹出式泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	private View viewCache = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MyLocationMapView mMapView = null; // 地图View
	MyLocationMapView mMapView1 = null;

	private MapController mMapController = null;
	private MapController mMapController1 = null;

	// UI相关
	OnCheckedChangeListener radioButtonListener = null;// radioButton监听器
	Button requestLocButton = null;// 界面中设置定位模式的铵钮
	Button sendLocationButton = null; // 发送当前位置按钮
	Button getbotton = null;
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	private static final int DIALOG_Phone_Number = 1;
	String phoneNum = null;
	private static final int PICK_CONTACT = 0;
	private static final int GET_LOCATION = 1;
	Intent intent = new Intent(Intent.ACTION_PICK,
			ContactsContract.Contacts.CONTENT_URI);
	TextView e;
	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mTitle;
	private String[] mPlanetTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mPlanetTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());// 设置监听
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {// 关闭的时候的标题文字
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {// 打开的时候的标题文字
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			selectItem(0);
		}
		// _________________________________________________________________________________
		mCurBtnType = E_BUTTON_TYPE.LOC;// ??

		// ________________________________________________________________________________________________________
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {// 监听左上角按钮
			return true;
		}
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
			switch(position)
			{
			case 1:
				Intent intent =new Intent(MainActivity.this,GeoCoderDemo.class);
				startActivity(intent);
				break;
			default:
				break;
			}
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	private void selectItem(int position) {// 切换界面
		Fragment fragment = new PlanetFragment();
		Bundle args = new Bundle();
		args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		fragment.setArguments(args);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		mDrawerList.setItemChecked(position, true);
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@SuppressLint("ValidFragment")
	public class PlanetFragment extends Fragment {
		public static final String ARG_PLANET_NUMBER = "planet_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView1 = inflater
					.inflate(R.layout.page_1, container, false);
			// 在此处写代码*******************************************************************************************************************************//
			requestLocButton = (Button) rootView1.findViewById(R.id.button1);
			sendLocationButton = (Button) rootView1.findViewById(R.id.button2);
			getbotton = (Button) rootView1.findViewById(R.id.button3);

			mMapView = (MyLocationMapView) rootView1
					.findViewById(R.id.bmapView);// 获取布局中的地图显示视图组件
			// 返回地图的MapController，这个对象可用于控制和驱动平移和缩放。

			mMapController = mMapView.getController();

			// 返回当前地图的缩放级别，取值范围是[3,19]，3为细节最小，19为细节最大
			mMapView.getController().setZoom(16);
			mMapView.getController().enableClick(true);//
			mMapView.setBuiltInZoomControls(true);// 设置是否启用内置的缩放按钮控件,

			// 创建 弹出泡泡图层,点定位小图标后会出现“我的位置”文字图层
			createPaopao();

			// 定位初始化
			mLocClient = new LocationClient(MainActivity.this);// 创建定位SDK的核心类对象
			locData = new LocationData();// 创建保存定位位置信息对象
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();

			// 设置是否打开gps，使用gps前提是用户硬件打开gps。默认是不打开gps的。
			option.setOpenGps(true);

			// 设置返回值的坐标类型,
			// 国测局经纬度坐标系 coor=gcj02
			// 百度墨卡托坐标系 coor=bd09
			// 百度经纬度坐标系 coor=bd09ll
			// 百度手机地图对外接口中的坐标系默认是bd09ll
			option.setCoorType("bd09ll"); // 设置坐标类型

			option.setScanSpan(5000);// 设置定位时间间隔,单位ms
			mLocClient.setLocOption(option);// 为定位对象设置参数
			mLocClient.start();// 启动定位SDK

			// 定位图层初始化
			myLocationOverlay = new locationOverlay(mMapView);
			// 设置定位数据
			myLocationOverlay.setData(locData);
			// 添加定位图层
			mMapView.getOverlays().add(myLocationOverlay);
			myLocationOverlay.enableCompass();
			// 修改定位数据后刷新图层生效
			mMapView.refresh();

			getbotton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					startActivityForResult(intent, GET_LOCATION);

				}
			});
			sendLocationButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivityForResult(intent, PICK_CONTACT);

				}
			});
			// 设置定位模式按钮的点击事件监听器
			OnClickListener btnClickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (mCurBtnType) {// 判断当前定位图层模式
					case LOC:// 当前是手动定位
						requestLocClick();// 触发一次手动定位请求
						break;
					case COMPASS:// 当前是指南针（即罗盘）模式
						myLocationOverlay.setLocationMode(LocationMode.NORMAL);// 设置为正常定位图层模式
						requestLocButton.setText("定位");// 设置定位模式按钮的显示文本
						mCurBtnType = E_BUTTON_TYPE.LOC;// 置为手动定位图层模式
						break;
					case FOLLOW:// 当前是跟随模式
						myLocationOverlay.setLocationMode(LocationMode.COMPASS);// 设置为罗盘定位图层模式
						requestLocButton.setText("罗盘");// 设置定位模式按钮的显示文本
						mCurBtnType = E_BUTTON_TYPE.COMPASS;// 置为软盘定位图层模式
						break;
					}
				}
			};
			requestLocButton.setOnClickListener(btnClickListener);

			// 在此处写代码*******************************************************************************************************************************//

			
			
//			
			View rootView2 = inflater
					.inflate(R.layout.page_1, container, false);
			Button button =(Button)rootView2.findViewById(R.id.textView1);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent =new Intent(MainActivity.this,GeoCoderDemo.class);
					startActivity(intent);
					
				}
			});
////			
			
			
			
//			mMapView1 = (MyLocationMapView) rootView2
//					.findViewById(R.id.bmapView1);// 获取布局中的地图显示视图组件
//			// 返回地图的MapController，这个对象可用于控制和驱动平移和缩放。
////
//			mMapController1 = mMapView1.getController();
////
////			// 返回当前地图的缩放级别，取值范围是[3,19]，3为细节最小，19为细节最大
//			mMapView1.getController().setZoom(16);
//			mMapView1.getController().enableClick(true);//
//			mMapView1.setBuiltInZoomControls(true);// 设置是否启用内置的缩放按钮控件,
////
////			// 创建 弹出泡泡图层,点定位小图标后会出现“我的位置”文字图层
////			createPaopao();
////
////			// 定位初始化
//			mLocClient1 = new LocationClient(MainActivity.this);// 创建定位SDK的核心类对象
//			locData1 = new LocationData();// 创建保存定位位置信息对象
//			mLocClient1.registerLocationListener(myListener1);
//			LocationClientOption option1 = new LocationClientOption();
////
////			// 设置是否打开gps，使用gps前提是用户硬件打开gps。默认是不打开gps的。
//			option1.setOpenGps(true);
////
////			// 设置返回值的坐标类型,
////			// 国测局经纬度坐标系 coor=gcj02
////			// 百度墨卡托坐标系 coor=bd09
////			// 百度经纬度坐标系 coor=bd09ll
////			// 百度手机地图对外接口中的坐标系默认是bd09ll
//			option1.setCoorType("bd09ll"); // 设置坐标类型
////
//			option1.setScanSpan(5000);// 设置定位时间间隔,单位ms
//			mLocClient1.setLocOption(option1);// 为定位对象设置参数
//			mLocClient1.start();// 启动定位SDK
//
//			// 定位图层初始化
//			myLocationOverlay1 = new locationOverlay(mMapView1);
//			// 设置定位数据
//			myLocationOverlay1.setData(locData);
//			// 添加定位图层
//			mMapView.getOverlays().add(myLocationOverlay1);
//			myLocationOverlay1.enableCompass();
//			// 修改定位数据后刷新图层生效
//			mMapView.refresh();
//			// 在此处写代码*******************************************************************************************************************************//

			// 在此处写代码*******************************************************************************************************************************//

			View rootView3 = inflater
					.inflate(R.layout.page_1, container, false);
			// 在此处写代码*******************************************************************************************************************************//

			// 在此处写代码*******************************************************************************************************************************//

			int i = getArguments().getInt(ARG_PLANET_NUMBER);// 切换rootView
			String planet = getResources()
					.getStringArray(R.array.planets_array)[i];
			getActivity().setTitle(planet);
			if (i == 0) {
				return rootView1;
			}
			if (i == 1) {
				return rootView2;

			}
			if (i == 2) {
				return rootView3;
			} else {
				return rootView1;
			}

		}

		@Override
		public void onDestroy() {
			// 退出时销毁定位
			if (mLocClient != null)
				mLocClient.stop();
			mMapView.destroy();
			super.onDestroy();
		}

		@Override
		public void onActivityResult(int reqCode, int resultCode, Intent data) {
			super.onActivityResult(reqCode, resultCode, data);
			switch (reqCode) {
			case (PICK_CONTACT):
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();

					Cursor c = managedQuery(contactData, null, null, null, null);
					if (c.moveToFirst()) {
						String name = c
								.getString(c
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						phoneNum = this.getContactPhone(c);

						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						// 设置对话框标题
						builder.setTitle("系统提示");
						// 设置对话框消息
						builder.setMessage("是否选择"+phoneNum);
						// 添加选择按钮并注册监听
						builder.setPositiveButton("确定", listener);
						builder.setNegativeButton("取消", listener);
						// 显示对话框
						builder.show();
						// Context self = null;
						// new AlertDialog.Builder(self)
						// .setTitle("确认")
						// .setMessage("确定吗？")
						// .setPositiveButton("是", null)
						// .setNegativeButton("否", null)
						// .show();

						// phoneNumber ="13962141072";
						/* 构建一取得default instance的SmsManager对象 */
						SmsManager smsManager = SmsManager.getDefault();
						/*
						 * 先构建一个PendingIntent对象并使用getBroadcast广播
						 * *将pendingIntent，电话号码，短信文字等参数
						 * *传入sendTextMessage()方法发送短信
						 */
						PendingIntent mPI = PendingIntent.getBroadcast(
								MainActivity.this, 0, new Intent(), 0);
						JSONObject locationInfo = new JSONObject();
						try {
							locationInfo.put("纬度", locData.latitude);
							locationInfo.put("经度", locData.longitude);
							locationInfo.put("精度", locData.accuracy);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (num == 1) {
							String messageText = locationInfo.toString();

							smsManager.sendTextMessage(phoneNum, null,
									messageText, mPI, null);
						} else {
						}
					}
				}
				break;
			case GET_LOCATION: {
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					Cursor c = managedQuery(contactData, null, null, null, null);
					if (c.moveToFirst()) {
						phoneNum = this.getContactPhone(c);

						// // 创建退出对话框
						// AlertDialog isExit = new
						// AlertDialog.Builder(this).create();
						// // 设置对话框标题
						// isExit.setTitle("系统提示");
						// // 设置对话框消息
						// isExit.setMessage("是否选择联系人");
						// // 添加选择按钮并注册监听
						// isExit.setButton("确定", listener);
						// isExit.setButton2("取消", listener);
						// // 显示对话框
						// isExit.show();
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						// 设置对话框标题
						builder.setTitle("系统提示");
						// 设置对话框消息
						builder.setMessage("是否选择"+phoneNum);
						// 添加选择按钮并注册监听
						builder.setPositiveButton("确定", listener);
						builder.setNegativeButton("取消", listener);
						// 显示对话框
						builder.show();

						 if(num==1){
						SmsManager smsManager = SmsManager.getDefault();
						PendingIntent mPI = PendingIntent.getBroadcast(
								MainActivity.this, 0, new Intent(), 0);
						smsManager.sendTextMessage(phoneNum, null,
								"getlocation", mPI, null);
						 }
						 else{
						 SmsManager smsManager = SmsManager.getDefault();
						 PendingIntent mPI = PendingIntent.getBroadcast(
						 MainActivity.this, 0, new Intent(), 0);
						 }
					}

				}
			}
				break;
			}
		}

		// 获取联系人电话
		private String getContactPhone(Cursor cursor) {
			int phoneColumn = cursor
					.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
			int phoneNum = cursor.getInt(phoneColumn);
			String phoneResult = "";
			// System.out.print(phoneNum);
			if (phoneNum > 0) {
				// 获得联系人的ID号
				int idColumn = cursor.getColumnIndex(BaseColumns._ID);
				String contactId = cursor.getString(idColumn);
				// 获得联系人的电话号码的cursor;
				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				// int phoneCount = phones.getCount();
				// allPhoneNum = new ArrayList<String>(phoneCount);
				if (phones.moveToFirst()) {
					// 遍历所有的电话号码
					for (; !phones.isAfterLast(); phones.moveToNext()) {
						int index = phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

						int typeindex = phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
						int phone_type = phones.getInt(typeindex);
						String phoneNumber = phones.getString(index);
						switch (phone_type) {
						case 2:
							phoneResult = phoneNumber;
							break;
						}
						// allPhoneNum.add(phoneNumber);
					}
					if (!phones.isClosed()) {
						phones.close();

					}
				}
			}

			return phoneResult;
		}

	}

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:// "确认"按钮退出程序
				num = 1;
				break;
			case DialogInterface.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框

				break;
			default:
				break;

			}
		}
	};

	// ______________________________________________________________________________________________________________________________________

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();

	}

	/**
	 * 修改位置图标
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// 当传入marker为null时，使用默认图标绘制
		myLocationOverlay.setMarker(marker);
		// 修改图层，需要刷新MapView生效
		mMapView.refresh();
	}

	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao() {
		// 将xml界面布局加载到当前视图中
		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapView.pop = pop;
	}

	/**
	 * 定位SDK监听器 获取定位结果，获取POI信息
	 */
	public class MyLocationListenner implements BDLocationListener {
		// 监听方法，获取定位结果
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 测试能否取得经纬度
			// Toast.makeText(getApplicationContext(),
			// "latitude:"+String.valueOf(locData.latitude)
			// +"\n"+"locData.longitude"+String.valueOf(locData.longitude) ,
			// Toast.LENGTH_LONG).show();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();// 获取定位精度半径，单位是米
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				Log.d("LocationOverlay", "receive location, animate to it");
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				requestLocButton.setText("跟随");
				mCurBtnType = E_BUTTON_TYPE.FOLLOW;
			}
			// 首次定位完成
			isFirstLoc = false;
		}

		// 获取POI信息的接口,这里没有实现具体功能
		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * 显示用户当前位置的覆盖层 继承MyLocationOverlay，重写dispatchTap实现点击处理
	 * 绘制用户当前在地图上的位置（精准度），和/或一个嵌入的指南针。 子类可通过覆盖方法dispatchTap()处理对用户当前位置的点击事件。
	 * MyLocationOverlay不绑定位置数据来源，可通过 setData() 方法设置位置信息,通过setMarker()更新位置图标
	 * 
	 * @Commenter 贾震斌
	 * @since 2013.12.10
	 */
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {// 处理点击事件,弹出泡泡
			// popupText是一个TextView对象
			popupText.setBackgroundResource(R.drawable.popup);// 设置弹出的文本框背景图片
			popupText.setText("我的位置：\n" + "纬度:"
					+ String.valueOf(locData.latitude) + "\n" + "经度："
					+ String.valueOf(locData.longitude) + "\n" + "精度："
					+ String.valueOf(locData.accuracy));// 设置文本框文本
			// 在弹出式覆盖层（PopupOverlay）中显示弹窗口
			pop.showPopup(BMapUtil.getBitmapFromView(popupText),// 弹窗位图
					new GeoPoint((int) (locData.latitude * 1e6),
							(int) (locData.longitude * 1e6)),// 弹窗的位置,定位锚点指向弹出小窗口下边中点
					20);// 弹窗下边在定位锚点上方的距离,取值范围yOffset>=0。单位：像素
			return true;
		}

	}

	@Override
	protected void onPause() {

		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {

		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author hejin
 * 
 */
class MyLocationMapView extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}
}