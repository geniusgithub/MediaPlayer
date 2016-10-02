package com.geniusgithub.mediaplayer.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geniusgithub.mediaplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainNavigationView extends LinearLayout implements View.OnClickListener {

    public static interface INavClickListener{
        public void onStartClick();
        public void onRestartClick();
        public void onStopClick();
        public void onExitClick();
    }

    private Context mContext;
    private View mRootView;


    @BindView(R.id.ll_search)
    View mSearch;

    @BindView(R.id.ll_restart)
    View mRest;

    @BindView(R.id.ll_stop)
    View mStop;

    @BindView(R.id.ll_exit)
    View mExit;

    @BindView(R.id.tv_localAddress)
    TextView mTVLocalAddress;

    private INavClickListener mNavListener;

    public MainNavigationView(Context context) {
        super(context);
    }

    public MainNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        mRootView = LayoutInflater.from(context).inflate(R.layout.main_navigation_layout, this,true);
        ButterKnife.bind(this, mRootView);
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
