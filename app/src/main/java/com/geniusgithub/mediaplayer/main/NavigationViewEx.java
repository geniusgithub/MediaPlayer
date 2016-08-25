package com.geniusgithub.mediaplayer.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;

public class NavigationViewEx extends LinearLayout implements View.OnClickListener{

    public static interface INavClickListener{
        public void onStartClick();
        public void onRestartClick();
        public void onStopClick();
        public void onExitClick();
    }

    private Context mContext;
    private View mRootView;

    private View mSearch;
    private View mRest;
    private View mStop;
    private View mExit;
    private TextView mTVLocalAddress;

    private INavClickListener mNavListener;

    public NavigationViewEx(Context context) {
        super(context);
    }

    public NavigationViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        mRootView = LayoutInflater.from(context).inflate(R.layout.navigation_header, this,true);
    }

    public void setmNavListener(INavClickListener listener){
        mNavListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initView();
    }

    private void initView(){
        mTVLocalAddress = (TextView) mRootView.findViewById(R.id.tv_localAddress);
        mSearch = mRootView.findViewById(R.id.ll_search);
        mRest = mRootView.findViewById(R.id.ll_restart);
        mStop = mRootView.findViewById(R.id.ll_stop);
        mExit = mRootView.findViewById(R.id.ll_exit);
        mSearch.setOnClickListener(this);
        mRest.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mExit.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
              if (mNavListener != null){
                  mNavListener.onStartClick();
              }
                break;
            case R.id.ll_restart:
                if (mNavListener != null){
                    mNavListener.onRestartClick();
                }
                break;
            case R.id.ll_stop:
                if (mNavListener != null){
                    mNavListener.onStopClick();
                }
                break;
            case R.id.ll_exit:
                if (mNavListener != null){
                    mNavListener.onExitClick();
                }
                break;
        }
    }


   public void  updateLocalAddress(String value){
       mTVLocalAddress.setText(value);
   }
}
