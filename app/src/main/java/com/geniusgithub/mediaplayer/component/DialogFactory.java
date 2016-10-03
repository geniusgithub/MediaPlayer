package com.geniusgithub.mediaplayer.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;

import org.cybergarage.upnp.Device;

public class DialogFactory {
	protected static final String TAG = DialogFactory.class.getSimpleName();

    public static void closeAllDialog(){

        if (mDeviceDetailDialog != null){
            mDeviceDetailDialog.dismiss();
        }
    	if (mTipAlertDialogDialDialog != null){
    		mTipAlertDialogDialDialog.dismiss();
    	}

    }

    public static void releaseDialogResource(){
    	mTipAlertDialogDialDialog = null;

        mDeviceDetailDialog = null;
        mDeviceViewHolder = null;
    }

    protected static AlertDialog mDeviceDetailDialog;
    protected static DeviceViewHolder mDeviceViewHolder;
    protected static class DeviceViewHolder{
        public TextView mTVManuFacture;
        public TextView mTVModelName;
        public TextView mTVModelDescription;
        public TextView mTVLocation;
        public TextView mTVUDN;

        public DeviceViewHolder(View view){
            bindView(view);
        }

        public void bindView(View view){
            mTVManuFacture = (TextView) view.findViewById(R.id.tv_mfact);
            mTVModelName = (TextView) view.findViewById(R.id.tv_mname);
            mTVModelDescription = (TextView) view.findViewById(R.id.tv_desc);
            mTVLocation = (TextView) view.findViewById(R.id.tv_url);
            mTVUDN = (TextView) view.findViewById(R.id.tv_udn);
        }

        public void update(Device device){
            mTVManuFacture.setText(device.getManufacture());
            mTVModelName.setText(device.getModelName());
            mTVModelDescription.setText(device.getModelDescription());
            mTVLocation.setText(device.getLocation());
            mTVUDN.setText(device.getUDN());
        }
    }
    public static void popupDeviceDetailDialog(Activity activity, Device device){
        if (mDeviceDetailDialog == null){
            Builder builder = new Builder(activity);
            View view = activity.getLayoutInflater().inflate(R.layout.device_detail_layout, null);
            builder.setView(view);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.close, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });

            mDeviceDetailDialog = builder.create();
            mDeviceViewHolder = new DeviceViewHolder(view);
        }
        mDeviceViewHolder.update(device);
        mDeviceDetailDialog.setTitle(device.getFriendlyName());
        mDeviceDetailDialog.show();
    }

    protected static AlertDialog mTipAlertDialogDialDialog;
    protected static AlertDialog getTipDialog(Activity activity, String title, String message){
    	  if (mTipAlertDialogDialDialog == null){
              Builder builder = new Builder(activity);
              builder.setCancelable(true);
              builder.setPositiveButton(R.string.close, new OnClickListener() {

      			@Override
      			public void onClick(DialogInterface dialog, int which) {
      				// TODO Auto-generated method stub

      			}
      		});
              mTipAlertDialogDialDialog = builder.create();
          }
		  mTipAlertDialogDialDialog.setTitle(title);
    	  mTipAlertDialogDialDialog.setMessage(message);
    	  return mTipAlertDialogDialDialog;
    }

    protected static String getString(Context context,final int resID){
    	return context.getString(resID);
    }
    
    protected static String getString(Context context,final int resID,  Object... formatArgs){
    	return context.getString(resID, formatArgs);
    }
}
