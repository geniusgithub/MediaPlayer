package com.geniusgithub.mediaplayer.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.AllShareApplication;
import com.geniusgithub.mediaplayer.IControlPointStatu;
import com.geniusgithub.mediaplayer.IToolBar;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.base.BaseActivity;
import com.geniusgithub.mediaplayer.browse.BrowserMediaFragment;
import com.geniusgithub.mediaplayer.dlna.model.ControlStatusChangeBrocastFactory;
import com.geniusgithub.mediaplayer.dlna.model.IStatusChangeListener;
import com.geniusgithub.mediaplayer.dlna.proxy.AllShareProxy;

import java.util.ArrayList;
import java.util.List;

public class MainFrameActivity extends BaseActivity implements IToolBar, View.OnClickListener, IStatusChangeListener {

    public static final String TAG = MainFrameActivity.class.getSimpleName();
    public static final String TAG_DMS_FRAGMENT = "tag_dms_fragment";

    private Context mContext;
    private Resources mResource;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private TabLayout.Tab mTabLibrary;
    private TabLayout.Tab mTabEmpty;

    private View mSearch;
    private View mRest;
    private View mStop;

    private TextView mTVLocalAddress;

    private BrowserMediaFragment mMediaServiceFragment;

    private AllShareProxy mAllShareProxy;

    private ControlStatusChangeBrocastFactory mBrocastFactory;

    @Override
    public void updateToolTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfram_layout);
        mContext = this;
        mResource = mContext.getResources();
        initView();
        initData();

    }


    private void initView() {
        initToolBar();
        initDrawLayout();
        setupViewPager();

        AllShareApplication.getInstance().setStatus(true);
    }

    private void initData() {
        mAllShareProxy = AllShareProxy.getInstance(this);
        mBrocastFactory = new ControlStatusChangeBrocastFactory(this);
        mBrocastFactory.registerListener(this);
        updateLocalAddress();
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("DLNA");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


    }

    private void initDrawLayout() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                AlwaysLog.i(TAG, "onDrawerOpened");

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                AlwaysLog.i(TAG, "onDrawerClosed");
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_main_navigation);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }


    }


/*
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.item_setting);
        menuItem.setIcon(R.drawable.device_details);
        return true;
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                toggleDrawLayout();
                return true;
            case R.id.item_setting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleDrawLayout() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

     /*                   switch (menuItem.getItemId()) {
                            case R.id.nav_start:
                                break;
                            case R.id.nav_restart:
                                break;
                            case R.id.nav_stop:
                                break;
                        }*/
                        return true;
                    }
                });

        View headView = navigationView.getHeaderView(0);
        mTVLocalAddress = (TextView) headView.findViewById(R.id.tv_localAddress);
        mSearch = headView.findViewById(R.id.ll_search);
        mRest = headView.findViewById(R.id.ll_restart);
        mStop = headView.findViewById(R.id.ll_stop);

        mSearch.setOnClickListener(this);
        mRest.setOnClickListener(this);
        mStop.setOnClickListener(this);
    }

    public void updateLocalAddress() {
        updateLocalAddress(AllShareApplication.getInstance().getControlStatus());
    }

    public void updateLocalAddress(int status) {
        String value = mContext.getResources().getString(R.string.status_stop);
        switch(status){
            case IControlPointStatu.STATUS_SOTP:
                value = mContext.getResources().getString(R.string.status_stop);
                break;
            case IControlPointStatu.STATUS_STARTED:
                value = mContext.getResources().getString(R.string.status_started);
                value += "(" + AllShareApplication.getInstance().getLocalAddress() + ")";
                break;
            case IControlPointStatu.STATUS_STARTING:
                value = mContext.getResources().getString(R.string.status_starting);
                break;
        }

        mTVLocalAddress.setText(value);
    }

    private void setupViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);


        List<String> titles = new ArrayList<String>();
        titles.add("LIBRARY");
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));

        mMediaServiceFragment = new BrowserMediaFragment();
        mMediaServiceFragment.bindToolbar(this);
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(mMediaServiceFragment);

        MainFragmentAdapter adapter = new MainFragmentAdapter(getFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
        mViewPager.setOffscreenPageLimit(mTabLayout.getTabCount());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                mAllShareProxy.startSearch();
                break;
            case R.id.ll_restart:
                mAllShareProxy.resetSearch();
                break;
            case R.id.ll_stop:
                mAllShareProxy.exitSearch();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onStatusChange(int status) {
        updateLocalAddress(status);
    }

    private class MainFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;
        private List<String> mTitles;

        public MainFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            mFragments = fragments;
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }


    @Override
    public void onBackPressed() {
        boolean back = mMediaServiceFragment.onBackPressed();
        if (!back) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}