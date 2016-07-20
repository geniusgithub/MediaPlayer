package com.geniusgithub.mediaplayer.browse.ui;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.geniusgithub.mediaplayer.activity.IToolBar;
import com.geniusgithub.mediaplayer.browse.BrowsePresenter;
import com.geniusgithub.mediaplayer.browse.IBaseFragmentPresent;



public class MediaServiceFragment extends Fragment {

    private static final String TAG = MediaServiceFragment.class.getSimpleName();

    private Context mContext;
    private IToolBar mExternToolbar;

    private IBaseFragmentPresent mBrwsePresenter;

    public MediaServiceFragment(IToolBar toolbar) {
        mExternToolbar = toolbar;
        mBrwsePresenter = createPresenter();
        mBrwsePresenter.bindFragment(this);
    }



    public IBaseFragmentPresent createPresenter(){
        return new BrowsePresenter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        mBrwsePresenter.onAttach(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBrwsePresenter.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return mBrwsePresenter.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBrwsePresenter.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mBrwsePresenter.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

        mBrwsePresenter.onPause();
    }

    @Override
    public void onDestroy() {

        mBrwsePresenter.onDestroy();

        super.onDestroy();
    }


    public boolean back(){
        return mBrwsePresenter.onBackPressed();
    }



    public void onViewSwitch(String title){
        if (mExternToolbar != null){
            mExternToolbar.updateToolTitle(title);
        }
    }

}
