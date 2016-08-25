package com.geniusgithub.mediaplayer.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.view.View;

import com.geniusgithub.common.util.AlwaysLog;
import com.geniusgithub.mediaplayer.base.IToolBar;
import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.activity.AboutActivity;
import com.geniusgithub.mediaplayer.base.BaseActivity;
import com.geniusgithub.mediaplayer.browse.view.BrowserMediaFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFrameActivity extends BaseActivity{

    public static final String TAG = MainFrameActivity.class.getSimpleName();

    private Context mContext;


    private View mRootView;
    private MainPresenter mMainPresenter;
    private MainContract.IView mMainView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfram_layout);
        mContext = this;

        initData();
    }

    @Override
    protected void onDestroy(){
        mMainPresenter.onDestroy();
        super.onDestroy();

    }

    private void initData() {
        mRootView = findViewById(R.id.ll_root);

        mMainPresenter = new MainPresenter();
        mMainView = new MainView();
        mMainView.setupView(mRootView);
        mMainPresenter.bindView(mMainView);


        mMainPresenter.onCreate(this);


    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mMainView.toggleDrawLayout();
                return true;
            case R.id.item_setting:
                goAboutActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        boolean back = mMainView.onBackPressed();
        if (!back) {
            super.onBackPressed();
        }
    }


    private void goAboutActivity(){
        Intent intent = new Intent();
        intent.setClass(this, AboutActivity.class);
        startActivity(intent);
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





    private class MainView implements  MainContract.IView, IToolBar, NavigationViewEx.INavClickListener{

        private MainContract.IPresenter mPresenter;
        private View mRootView;

         private ActionBarDrawerToggle mDrawerToggle;
         private DrawerLayout mDrawerLayout;
         private Toolbar mToolbar;
         private NavigationViewEx mNavigationView;


         private ViewPager mViewPager;
         private TabLayout mTabLayout;
         private TabLayout.Tab mTabLibrary;


         private BrowserMediaFragment mMediaServiceFragment;

         @Override
        public void bindPresenter(MainContract.IPresenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void setupView(View rootView) {
            mRootView = rootView;

            initToolBar();
            initDrawLayout();
            setupViewPager();
        }

         @Override
         public void updateToolTitle(String title) {
             mToolbar.setTitle(title);
         }

         @Override
         public void updateLocalAddress(String value) {
            mNavigationView.updateLocalAddress(value);
         }

         @Override
         public void toggleDrawLayout() {
             if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                 mDrawerLayout.closeDrawers();
             } else {
                 mDrawerLayout.openDrawer(GravityCompat.START);
             }

         }

        @Override
        public boolean onBackPressed() {
            return mMediaServiceFragment.onBackPressed();
        }


        @Override
        public void onStartClick() {
            mPresenter.onStart();
        }

        @Override
        public void onRestartClick() {
            mPresenter.onRestart();
        }

        @Override
        public void onStopClick() {
            mPresenter.onStop();
        }

        @Override
        public void onExitClick() {
            mPresenter.onExit();
            finish();
        }


         private void initToolBar() {
            mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
            mToolbar.setTitle("DLNA");
            mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            setSupportActionBar(mToolbar);


            final ActionBar ab = getSupportActionBar();
           // ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);


        }

        private void initDrawLayout() {

            mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer);
            mDrawerToggle = new ActionBarDrawerToggle(MainFrameActivity.this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
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


            mNavigationView = (NavigationViewEx) findViewById(R.id.nv_main_navigation);
            if (mNavigationView != null) {
                mNavigationView.setmNavListener(this);
            }
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



    }
}