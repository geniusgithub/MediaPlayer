a MediaPlay  run in Android Platform 
===========
MediaPlayer is a DLNA device(DMP) that can find DMS in Local Area Networkand ant play it native<br /> 
It uses the library of dlna_framework, so you should download it first

Example screenshot below:

![github](http://img.my.csdn.net/uploads/201307/17/1374056183_8683.png "github")  

![github](http://my.csdn.net/my/album/detail/1650016#1650018 "github")  

![github](http://img.my.csdn.net/uploads/201307/17/1374056183_8947.png "github")  

![github](http://img.my.csdn.net/uploads/201307/17/1374056184_3780.png "github")  

![github](http://img.my.csdn.net/uploads/201307/17/1374056184_5031.png "github")  


Code fragment
--------------------


    public class DlnaService extends Service implements IBaseEngine,
  												DeviceChangeListener,
													ControlCenterWorkThread.ISearchDeviceListener{
	
	private static final CommonLog log = LogFactory.createLog();
	
	public static final String SEARCH_DEVICES = "com.geniusgithub.allshare.search_device";
	public static final String RESET_SEARCH_DEVICES = "com.geniusgithub.allshare.reset_search_device";
	
	private static final int NETWORK_CHANGE = 0x0001;
	private boolean firstReceiveNetworkChangeBR = true;
	private  NetworkStatusChangeBR mNetworkStatusChangeBR;
	
	
	private  ControlPoint mControlPoint;
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
		log.e("DlnaService onCreate");
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
		
		mControlPoint = new ControlPoint();
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
	}
	
	private void unInit(){
		unRegisterNetworkStatusBR();
		AllShareApplication.getInstance().setControlPoint(null);
		mCenterWorkThread.setSearchListener(null);
		mCenterWorkThread.exit();
	}

	
	@Override
	public boolean startEngine() {
		awakeWorkThread();
		return true;
	}


	@Override
	public boolean stopEngine() {
		exitWorkThread();
		return true;
	}


	@Override
	public boolean restartEngine() {
		mCenterWorkThread.reset();
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

		if (!searchSuccess){
			sendSearchDeviceFailBrocast(this);
		}
	}
	
	public static final String SEARCH_DEVICES_FAIL = "com.geniusgithub.allshare.search_devices_fail";
	public static void sendSearchDeviceFailBrocast(Context context){
		log.e("sendSearchDeviceFailBrocast");
		Intent intent = new Intent(SEARCH_DEVICES_FAIL);
		context.sendBroadcast(intent);
	}
	
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

	private static final CommonLog log = LogFactory.createLog();
  
	private static final int REFRESH_DEVICES_INTERVAL = 30 * 1000; 
	
	public static interface ISearchDeviceListener{
		public void onSearchComplete(boolean searchSuccess);
	}
	
	private ControlPoint mCP = null;
	private Context mContext = null;
	private boolean mStartComplete = false;
	private boolean mIsExit = false;
	private ISearchDeviceListener mSearchDeviceListener;
	
	public ControlCenterWorkThread(Context context, ControlPoint controlPoint){
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
		log.e("ControlCenterWorkThread run...");		
		
		while(true)
		{
			if (mIsExit){
				mCP.stop();
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
		
		log.e("ControlCenterWorkThread over...");		
	}
	
	private void refreshDevices(){
		log.e("refreshDevices...");
		if (!CommonUtil.checkNetworkState(mContext)){
			return ;
		}

		try {
			if (mStartComplete){
				boolean searchRet = mCP.search();	
				log.e("mCP.search() ret = "  + searchRet);
				if (mSearchDeviceListener != null){
					mSearchDeviceListener.onSearchComplete(searchRet);
				}
			}else{
				boolean startRet = mCP.start();
				log.e("mCP.start() ret = "  + startRet);
				if (startRet){
					mStartComplete = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}



    }

Run requirements
------------------------------
Android OS 2.3x and up<br />
Tested with: Samsung, HTC, HuaWei Phone and so on...

## Acknowledgements
It uses open source libraries below:
* [dlna_framework](https://github.com/geniusgithub/dlna_framework)


Contributing
------------------------------
Feel free to drop me pull requests. If you plan to implement something more than a few lines, then open the pull request early so that there aren't any nasty surprises later.
If you want to add something that will require some for of persistence incl. persistent configuration or API keys, etc., then open a pull request/issue especially early!


### Links
csdn blog : [http://blog.csdn.net/geniuseoe2012](http://blog.csdn.net/geniuseoe2012)<br /> 
dlna doc: [DLNA Document](http://download.csdn.net/detail/geniuseoe2012/4969961)<br />


### Development
If you think this article useful Nepal , please pay attention to me<br />
Your support is my motivation, I will continue to strive to do better

