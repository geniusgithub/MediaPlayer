package com.geniusgithub.mediaplayer.browse;

import android.content.Context;
import android.os.AsyncTask;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.dlna.ParseUtil;
import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnPStatus;

import java.util.List;

public class BrowseDMSProxy {

	public static final String TAG = BrowseDMSProxy.class.getSimpleName();
	public static interface BrowseRequestCallback
	{
		public void onRequestBegin();
		public void onRequestCancel();
		public void onRequestSuccess(final List<MediaItem> list);
		public void onRequestFail();
	}
	
	public static  BrowseContentAsnyTask syncGetDirectory(final Context context, Device device, final BrowseRequestCallback callback) {
		return syncGetItems(context, device, null, callback);
	}
	
	public static BrowseContentAsnyTask syncGetItems(final Context context,  Device device, final String id, final BrowseRequestCallback callback) {
		BrowseContentAsnyTask task =  new BrowseContentAsnyTask(context, id, callback);
		task.bindCallback(callback);
		task.execute(device);
		return task;
	}

	public static class BrowseContentAsnyTask extends AsyncTask<Device, Void, List<MediaItem>>{

		private Context mContext;
		private String mReuqestID;
		private BrowseRequestCallback mCallback;

		public BrowseContentAsnyTask(Context context, String id, BrowseRequestCallback callback){
			mContext = context;
			mReuqestID = id;
			bindCallback(callback);
		}

		public void bindCallback(BrowseRequestCallback callback){
			mCallback = callback;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (mCallback != null){
				mCallback.onRequestBegin();
			}
		}

		@Override
		protected List<MediaItem> doInBackground(Device... params) {

			Device device = params[0];
			List<MediaItem> list = null;
			try {
				list = getItems(mContext, device, mReuqestID);
			} catch (Exception e) {
				e.printStackTrace();
			}


			return list;
		}

		@Override
		protected void onPostExecute(List<MediaItem> mediaItemList) {
			super.onPostExecute(mediaItemList);

			if (mCallback != null){
				if (mediaItemList != null){
					mCallback.onRequestSuccess(mediaItemList);
				}else{
					mCallback.onRequestFail();
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			if (mCallback != null){
				mCallback.onRequestCancel();
			}
		}

		private static List<MediaItem> getItems(Context context, Device selDevice, String id) throws Exception{

			org.cybergarage.upnp.Service service = selDevice
					.getService("urn:schemas-upnp-org:service:ContentDirectory:1");
			if (service == null)
			{
				AlwaysLog.e(TAG, "no service for ContentDirectory!!!");
				return null;
			}

			Action action = service.getAction("Browse");
			if(action == null)
			{
				AlwaysLog.e(TAG, "action for Browse is null");
				return null;
			}

			//	action.getActionNode().print();

			ArgumentList argumentList = action.getArgumentList();
			if (id != null){
				argumentList.getArgument("ObjectID").setValue(id);
			}else{
				argumentList.getArgument("ObjectID").setValue(0);
			}

			argumentList.getArgument("BrowseFlag").setValue("BrowseDirectChildren");
			argumentList.getArgument("StartingIndex").setValue("0");
			argumentList.getArgument("RequestedCount").setValue("0");
			argumentList.getArgument("Filter").setValue("*");
			argumentList.getArgument("SortCriteria").setValue("");

			if (action.postControlAction()) {
				ArgumentList outArgList = action.getOutputArgumentList();
				Argument result = outArgList.getArgument("Result");
				AlwaysLog.d(TAG, "result value = \n" + result.getValue());

				List<MediaItem> items = ParseUtil.parseResult(result);
				return items;
			} else {
				UPnPStatus err = action.getControlStatus();
				System.out.println("Error Code = " + err.getCode());
				System.out.println("Error Desc = " + err.getDescription());
			}
			return null;
		}
	}

}
