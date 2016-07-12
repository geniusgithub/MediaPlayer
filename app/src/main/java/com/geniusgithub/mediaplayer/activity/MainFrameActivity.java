package com.geniusgithub.mediaplayer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.geniusgithub.mediaplayer.R;

public class MainFrameActivity extends AppCompatActivity {


    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfram_layout);

        initView();
    }


    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar);

        initDrawLayout(toolbar);

//         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//         fab.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View view) {
//                 Snackbar.make(view, "Snackbar comes out", Snackbar.LENGTH_LONG)
//                         .setAction("Action", new View.OnClickListener() {
//                             @Override
//                             public void onClick(View v) {
//                                 Toast.makeText(
//                                         APIActivity.this,
//                                         "Toast comes out",
//                                         Toast.LENGTH_SHORT).show();
//                             }
//                         }).show();
//             }
//         });


    }


    private void initToolBar(Toolbar toolbar){

        setSupportActionBar(toolbar);
        toolbar.setTitle("DLNA");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);



    }

    private void initDrawLayout(Toolbar toolbar){
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,  R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        };


        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);



        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_main_navigation);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }


    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        switch(menuItem.getItemId()){
                            case R.id.nav_start:
                                break;
                            case R.id.nav_restart:
                                break;
                            case R.id.nav_stop:
                                break;
                        }
                        return true;
                    }
                });
    }

}
