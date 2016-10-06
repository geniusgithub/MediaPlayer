package com.geniusgithub.mediaplayer.dlna.control.browsecontrol;

import android.content.Context;
import android.os.AsyncTask;

import com.geniusgithub.mediaplayer.dlna.control.model.MediaItem;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.container.RootNode;
import org.cybergarage.util.AlwaysLog;

import java.util.ArrayList;
import java.util.List;

public class BrowseControllerProxy {

	public static final String TAG = BrowseControllerProxy.class.getSimpleName();
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

			ContainerNode rootNode = new RootNode();
			boolean ret = mController.browseItem(device, mReuqestID, rootNode);
			if (ret){
				List<MediaItem> list = new ArrayList<MediaItem>();
				boolean parsetRet = parseResult(rootNode, list);
				if (parsetRet) {
					return list;
				}else{
					AlwaysLog.e(TAG, "parseResult fail!!!");
				}
			}else{
				AlwaysLog.e(TAG, "browseItem fail!!!");
			}

			return null;
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


	public static boolean parseResult(ContainerNode rootNode, List<MediaItem> list){

		//	AlwaysLog.i(TAG, "parseResult rootNode = \n" + rootNode.toString());
		int childCount = rootNode.getChildCount();
		ContentNode node = null;
		for(int i = 0; i < childCount; i++){
			node = rootNode.getContentNode(i);

			MediaItem item = MediaItem.Builder.create(node);
			if (item != null){
				list.add(item);
			}else{
				AlwaysLog.e(TAG, "unknow node??? index = " + i);
			}
		}

		return true;
	}

}
