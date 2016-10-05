package com.geniusgithub.mediaplayer.dlna.browse;

import android.content.Context;
import android.os.AsyncTask;

import com.geniusgithub.mediaplayer.dlna.model.MediaItem;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
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
	
	public static  BrowseContentAsnyTask asyncBrowseDirectory(final Context context, Device device, final BrowseRequestCallback callback) {
		return asyncBrowseItems(context, device, null, callback);
	}
	
	public static BrowseContentAsnyTask asyncBrowseItems(final Context context, Device device, final String id, final BrowseRequestCallback callback) {
		BrowseContentAsnyTask task =  new BrowseContentAsnyTask(context, id, callback);
		task.bindCallback(callback);
		task.execute(device);
		return task;
	}

	public static class BrowseContentAsnyTask extends AsyncTask<Device, Void, List<MediaItem>>{

		private Context mContext;
		private String mReuqestID;
		private BrowseRequestCallback mCallback;
		private IBrowseController mController;

		public BrowseContentAsnyTask(Context context, String id, BrowseRequestCallback callback){
			mContext = context;
			mReuqestID = id;
			mController = new BrowseController();
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
			List<MediaItem> list = new ArrayList<MediaItem>();
			mController.browseItem(device, mReuqestID, list);
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

	}

}
