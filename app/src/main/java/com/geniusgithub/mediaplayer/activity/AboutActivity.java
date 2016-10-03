package com.geniusgithub.mediaplayer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.geniusgithub.common.util.CommonUtil;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
     Toolbar mToolbar;

    @BindView(R.id.tv_version)
     TextView mTVVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abount_layout);
        ButterKnife.bind(this);

        initToolBar();
        initData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        mToolbar.setTitle(getResources().getString(R.string.menu_about));
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


    }
    private void initData(){
        updateVersion();
    }

    private void updateVersion(){
        String value = getResources().getString(R.string.app_name) + " V" + CommonUtil.getSoftVersion(this);
        mTVVersion.setText(value);
    }

}
