a MediaPlay  run in Android Platform 
===========
MediaPlayer is the DLNA device(DMP), which can be in the search for DMS devices, browse resources and play locally

Example screenshot below:

![github](https://github.com/geniusgithub/MediaPlayer/blob/master/storage/main.jpg?raw=true "github")  

![github](https://github.com/geniusgithub/MediaPlayer/blob/master/storage/browse.jpg?raw=true "github")  

![github](https://github.com/geniusgithub/MediaPlayer/blob/master/storage/media.jpg?raw=true "github")  


Code fragment
--------------------


public class DlnaService extends Service implements IBaseEngine,DeviceChangeListener,ControlCenterWorkThread.ISearchDeviceListener{
	
	private static final CommonLog log = LogFactory.createLog();
	private static final String TAG = DlnaService.class.getSimpleName();

	public static final String SEARCH_DEVICES = "com.geniusgithub.allshare.search_device";
	public static final String RESET_SEARCH_DEVICES = "com.geniusgithub.allshare.reset_search_device";
	
	private static final int NETWORK_CHANGE = 0x0001;
	private boolean firstReceiveNetworkChangeBR = true;
	private  NetworkStatusChangeBR mNetworkStatusChangeBR;
	
	
	private ControlPointImpl mControlPoint;
	private  ControlCenterWorkThread mCenterWorkThread;
	private  AllShareProxy mAllShareProxy;
	private  Handler mHandler;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		AlwaysLog.i(TAG, "DlnaService onCreate");
		init();
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (intent != null && intent.getAction() != null){
			String action = intent.getAction();
			if (DlnaService.SEARCH_DEVICES.equals(action)) {
				startEngine();
			}else if (DlnaService.RESET_SEARCH_DEVICES.equals(action)){
				restartEngine();
			}
		}else{
			log.e("intent = " + intent);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		log.e("DlnaService onDestroy");
		unInit();
		super.onDestroy();
	}
	
	
	private void init(){
		mAllShareProxy = AllShareProxy.getInstance(this);
		
		mControlPoint = new ControlPointImpl();
		AllShareApplication.getInstance().setControlPoint(mControlPoint);
		mControlPoint.addDeviceChangeListener(this);
		mControlPoint.addSearchResponseListener(new SearchResponseListener() {		
			public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
			}
		});
	

		mCenterWorkThread = new ControlCenterWorkThread(this, mControlPoint);
		mCenterWorkThread.setSearchListener(this);
		
		mHandler = new Handler(){

			public void handleMessage(Message msg) {
				switch(msg.what){
					case NETWORK_CHANGE:
						mAllShareProxy.resetSearch();
						break;
				}
			}
			
		};
		
		registerNetworkStatusBR();


		DelCacheFileManager mDelthumbnailManager = new DelCacheFileManager();
		mDelthumbnailManager.clearThumbnailCache();


		boolean ret = CommonUtil.openWifiBrocast(this);
		AlwaysLog.i(TAG, "openWifiBrocast = " + ret);
	}
	
	private void unInit(){
		unRegisterNetworkStatusBR();
		stopEngine();
		AllShareApplication.getInstance().setControlPoint(null);

		DelCacheFileManager mDelthumbnailManager = new DelCacheFileManager();
		mDelthumbnailManager.clearThumbnailCache();

	}

	
	@Override
	public boolean startEngine() {
		AlwaysLog.i(TAG, "startEngine");
		if (AllShareApplication.getInstance().getControlStatus() != IControlPointStatu.STATUS_STARTED){
			AllShareApplication.getInstance().updateControlStauts(IControlPointStatu.STATUS_STARTING);
		}
		awakeWorkThread();
		return true;
	}


	@Override
	public boolean stopEngine() {
		AlwaysLog.i(TAG, "stopEngine");
		exitWorkThread();
		return true;
	}


	@Override
	public boolean restartEngine() {
		AlwaysLog.i(TAG, "restartEngine");
		AllShareApplication.getInstance().updateControlStauts(IControlPointStatu.STATUS_STARTING);

		mCenterWorkThread.setCompleteFlag(false);
		awakeWorkThread();

		return true;
	}




	@Override
	public void deviceAdded(Device dev) {
		mAllShareProxy.addDevice(dev);
	}


	@Override
	public void deviceRemoved(Device dev) {
		mAllShareProxy.removeDevice(dev);
	}
	
	
	private void awakeWorkThread(){

		if (mCenterWorkThread.isAlive()){
			mCenterWorkThread.awakeThread();
		}else{
			mCenterWorkThread.start();
		}
	}
	
	private void exitWorkThread(){
		if (mCenterWorkThread != null && mCenterWorkThread.isAlive()){
			mCenterWorkThread.exit();
			long time1 = System.currentTimeMillis();
			while(mCenterWorkThread.isAlive()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long time2 = System.currentTimeMillis();
			log.e("exitCenterWorkThread cost time:" + (time2 - time1));
			mCenterWorkThread = null;
		}
	}
	
	
	@Override
	public void onSearchComplete(boolean searchSuccess) {

/*		if (!searchSuccess){
			sendSearchDeviceFailBrocast(this);
		}*/
	}

	@Override
	public void onStartComplete(boolean startSuccess) {

		mControlPoint.flushLocalAddress();
	//	sendStartDeviceEventBrocast(this, startSuccess);
		AlwaysLog.i(TAG, "onStartComplete startSuccess = " + startSuccess);
		if (startSuccess){
			AllShareApplication.getInstance().updateControlStauts(IControlPointStatu.STATUS_STARTED);
		}else{

		}

	}

	@Override
	public void onStopComplete() {
		AlwaysLog.i(TAG, "onStopComplete");
		AllShareApplication.getInstance().updateControlStauts(IControlPointStatu.STATUS_SOTP);
	}

/*	public static final String SEARCH_DEVICES_FAIL = "com.geniusgithub.allshare.search_devices_fail";
	public static void sendSearchDeviceFailBrocast(Context context){
		log.e("sendSearchDeviceFailBrocast");
		Intent intent = new Intent(SEARCH_DEVICES_FAIL);
		context.sendBroadcast(intent);
	}*/

/*	public static final String START_DEVICES_EVENT = "com.geniusgithub.allshare.start_devices_event";
	public static void sendStartDeviceEventBrocast(Context context, boolean startSuccess){
		log.e("sendStartDeviceEventBrocast startSuccess = " + startSuccess);
		Intent intent = new Intent(START_DEVICES_EVENT);
		context.sendBroadcast(intent);
	}*/
	
	private class NetworkStatusChangeBR extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent != null){
				String action = intent.getAction();
				if (action != null){
					if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)){
						sendNetworkChangeMessage();
					}
				}
			}
			
		}
		
	}
	
	private void registerNetworkStatusBR(){
		if (mNetworkStatusChangeBR == null){
			mNetworkStatusChangeBR = new NetworkStatusChangeBR();
			registerReceiver(mNetworkStatusChangeBR, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}
	
	private void unRegisterNetworkStatusBR(){
		if (mNetworkStatusChangeBR != null){
			unregisterReceiver(mNetworkStatusChangeBR);
		}
	}
	
	private void sendNetworkChangeMessage(){
		if (firstReceiveNetworkChangeBR){
			log.e("first receive the NetworkChangeMessage, so drop it...");
			firstReceiveNetworkChangeBR = false;
			return ;
		}
		
		mHandler.removeMessages(NETWORK_CHANGE);
		mHandler.sendEmptyMessageDelayed(NETWORK_CHANGE, 500);
	}

}


public class ControlCenterWorkThread extends Thread{

	private final static String TAG = ControlCenterWorkThread.class.getSimpleName();

	private static final int REFRESH_DEVICES_INTERVAL = 30 * 1000; 
	
	public static interface ISearchDeviceListener{
		public void onSearchComplete(boolean searchSuccess);
		public void onStartComplete(boolean startSuccess);
		public void onStopComplete();
	}
	
	private ControlPointImpl mCP = null;
	private Context mContext = null;
	private boolean mStartComplete = false;
	private boolean mIsExit = false;
	private ISearchDeviceListener mSearchDeviceListener;
	
	public ControlCenterWorkThread(Context context, ControlPointImpl controlPoint){
		mContext = context;
		mCP = controlPoint; 
	}
	
	public void  setCompleteFlag(boolean flag){
		mStartComplete = flag;
	}
	
	public void setSearchListener(ISearchDeviceListener listener){
		mSearchDeviceListener = listener;
	}
	
	public void awakeThread(){
		synchronized (this) {
			notifyAll();
		}
	}
	
	
	public void reset(){
		setCompleteFlag(false);
		awakeThread();
	}
	
	public void exit(){
		mIsExit = true;
		awakeThread();
	}
	
	
	@Override
	public void run() {
		AlwaysLog.i(TAG, "ControlCenterWorkThread run...");
		
		while(true)
		{
			if (mIsExit){
				mCP.stop();
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onStopComplete();
				}
				break;
			}
			
			refreshDevices();
			
			synchronized(this)
			{		
				try
				{
					wait(REFRESH_DEVICES_INTERVAL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}				
			}
		}

		AlwaysLog.i(TAG, "ControlCenterWorkThread over...");
	}
	
	private void refreshDevices(){
		AlwaysLog.d(TAG, "refreshDevices...");
		if (!CommonUtil.checkNetworkState(mContext)){
			return ;
		}

		try {
			if (mStartComplete){
				boolean searchRet = mCP.search();
				AlwaysLog.i(TAG, "mCP.search() ret = "  + searchRet);
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onSearchComplete(searchRet);
				}
			}else{
				boolean startRet = mCP.start();
				AlwaysLog.i(TAG, "mCP.start() ret = "  + startRet);
				if (startRet){
					mStartComplete = true;
				}
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onStartComplete(startRet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}



}


Run requirements
------------------------------
Android OS 5.0 and up<br />

## Acknowledgements
The upnp framework is CyberGarage,fork from [CyberLink4Java](https://github.com/cybergarage/CyberLink4Java)!



Contributing
------------------------------
Feel free to drop me pull requests. If you plan to implement something more than a few lines, then open the pull request early so that there aren't any nasty surprises later.
If you want to add something that will require some for of persistence incl. persistent configuration or API keys, etc., then open a pull request/issue especially early!


### Links
csdn blog : [http://blog.csdn.net/geniuseoe2012](http://blog.csdn.net/lancees/article/details/8477513)<br /> 
cnblog : [http://www.cnblogs.com/lance2016](http://www.cnblogs.com/lance2016/archive/2013/01/07/5204247.html)<br /> 

dlna doc: [DLNA Document](http://download.csdn.net/detail/geniuseoe2012/4969961)<br />


### Development
If you think this article useful Nepal , please pay attention to me<br />
Your support is my motivation, I will continue to strive to do better

