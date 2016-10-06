package com.geniusgithub.mediaplayer.dlna.control.rendercontrol;

import android.os.AsyncTask;

import com.geniusgithub.mediaplayer.dlna.control.base.TransportState;
import com.geniusgithub.mediaplayer.dlna.util.DlnaUtil;

import org.cybergarage.upnp.Device;
import org.cybergarage.util.AlwaysLog;

public class RemotePlayer implements  IRemotePlayback{

    public static final String TAG = RemotePlayer.class.getSimpleName();
    private IRenderController mRenderController;

    private IRemotePlayback.Callback mCallback;

    public RemotePlayer(){
        mRenderController = new RenderController();
    }

    @Override
    public boolean play(Device device, String path) {
        RenderControlAsnyTask task =  new RenderRePlayAsnyTask(mRenderController, mCallback,  path);
        task.execute(device);
        return true;
    }

    @Override
    public boolean play(Device device) {
        RenderControlAsnyTask task =  new RenderPlayAsnyTask(mRenderController, mCallback);
        task.execute(device);
        return true;
    }

    @Override
    public boolean pause(Device device) {
        RenderControlAsnyTask task =  new RenderPauseAsnyTask(mRenderController, mCallback);
        task.execute(device);
        return true;
    }

    @Override
    public boolean stop(Device device) {
        RenderControlAsnyTask task =  new RenderStopAsnyTask(mRenderController, mCallback);
        task.execute(device);
        return true;
    }

    @Override
    public boolean getProgress(Device device) {
        RenderControlAsnyTask task =  new RenderGetProgressAsnyTask(mRenderController, mCallback);
        task.execute(device);
        return true;
    }

    @Override
    public boolean getDuration(Device device) {
        RenderControlAsnyTask task =  new RenderGetDurationAsnyTask(mRenderController, mCallback);
        task.execute(device);
        return true;
    }

    @Override
    public boolean getTransportState(Device device) {
        RenderControlAsnyTask task =  new RenderGetTransportAsnyTask(mRenderController, mCallback);
        task.execute(device);
        return true;
    }


    @Override
    public boolean seekTo(Device device, int progress) {
        RenderControlAsnyTask task =  new RenderSeekAsnyTask(mRenderController, mCallback, progress);
        task.execute(device);
        return true;
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void unregisterCallback() {
        mCallback = null;
    }

    public static abstract  class RenderControlAsnyTask extends AsyncTask<Device, Void, Boolean> {

        protected IRenderController mRenderController;
        protected IRemotePlayback.Callback mCallback;
        public RenderControlAsnyTask(IRenderController control, IRemotePlayback.Callback callback){
            mRenderController = control;
            mCallback = callback;
        }


        @Override
        protected Boolean doInBackground(Device... params) {
            Device device = params[0];
            return onTaskRun(device, mRenderController);
        }
        protected abstract boolean onTaskRun(Device device, IRenderController control);


        @Override
        protected void onPostExecute(Boolean ret) {
            super.onPostExecute(ret);
            onTaskComplete(ret);
        }
        protected abstract void onTaskComplete(Boolean ret);

    }

    public static class RenderRePlayAsnyTask extends  RenderControlAsnyTask{

        public String mUrl;

        public RenderRePlayAsnyTask(IRenderController control, Callback callback, String url) {
            super(control, callback);
            mUrl = url;
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            if (mUrl != null){
                boolean ret = control.setAVTransportURI(device, mUrl);
                if (ret){
                    return control.play(device);
                }
            }

            return false;
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderRePlayAsnyTask ret = " + ret);
            mCallback.onRePlayComplete(ret);
        }
    }

    public static class RenderPlayAsnyTask extends  RenderControlAsnyTask{


        public RenderPlayAsnyTask(IRenderController control, Callback callback) {
            super(control, callback);
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            return control.play(device);
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderPlayAsnyTask ret = " + ret);
            mCallback.onPlayComplete(ret);
        }
    }

    public static class RenderPauseAsnyTask extends  RenderControlAsnyTask{


        public RenderPauseAsnyTask(IRenderController control, Callback callback) {
            super(control, callback);
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            return control.pause(device);
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderPauseAsnyTask ret = " + ret);
            mCallback.onPauseComplete(ret);
        }

    }

    public static class RenderStopAsnyTask extends  RenderControlAsnyTask{


        public RenderStopAsnyTask(IRenderController control, Callback callback) {
            super(control, callback);
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            return control.stop(device);
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderStopAsnyTask ret = " + ret);
            mCallback.onStopComplete(ret);
        }


    }


    public static class RenderGetProgressAsnyTask extends  RenderControlAsnyTask{

        private int progress = 0;
        public RenderGetProgressAsnyTask(IRenderController control, Callback callback) {
            super(control, callback);
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            String positionString = control.getPositionInfo(device);
            if (positionString != null){
                progress = DlnaUtil.formatDurationString(positionString);
                return true;
            }
            return false;
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderGetProgressAsnyTask ret = " + ret + ", progress = " + progress);
            mCallback.onGetProgressComplete(ret, progress);
        }
    }

    public static class RenderGetDurationAsnyTask extends  RenderControlAsnyTask{

        private int duration = 0;
        public RenderGetDurationAsnyTask(IRenderController control, Callback callback) {
            super(control, callback);
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            String durationString = control.getMediaDuration(device);
            if (durationString != null){
                duration = DlnaUtil.formatDurationString(durationString);
                return true;
            }
            return false;
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderGetDurationAsnyTask ret = " + ret + ", duration = " + duration);
            mCallback.onGetDurationComplete(ret, duration);
        }
    }

    public static class RenderGetTransportAsnyTask extends  RenderControlAsnyTask{

        private String mTransportState;
        public RenderGetTransportAsnyTask(IRenderController control, Callback callback) {
            super(control, callback);
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            String transportState = control.getTransportState(device);
            if (transportState != null){
                mTransportState = transportState;
                return true;
            }
            return false;
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderGetDurationAsnyTask ret = " + ret + ", mTransportState = " + mTransportState);

            int state = TransportState.getState(mTransportState);
            mCallback.onGetTransportComplete(ret, state);
        }
    }


    public static class RenderSeekAsnyTask extends  RenderControlAsnyTask{

        public int mProgress;
        public RenderSeekAsnyTask(IRenderController control, Callback callback, int progress) {
            super(control, callback);
        }

        @Override
        protected boolean onTaskRun(Device device, IRenderController control) {
            return control.seek(device, DlnaUtil.formatDurationString(mProgress));
        }

        @Override
        protected void onTaskComplete(Boolean ret) {
            AlwaysLog.d(TAG, "RenderSeekAsnyTask ret = " + ret);
            mCallback.onSeekComplete(ret);
        }
    }
}