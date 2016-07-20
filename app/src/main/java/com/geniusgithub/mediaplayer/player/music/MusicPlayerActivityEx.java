package com.geniusgithub.mediaplayer.player.music;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.geniusgithub.mediaplayer.R;
import com.geniusgithub.mediaplayer.activity.BaseActivity;

public class MusicPlayerActivityEx extends BaseActivity {

    public static final String TAG_MUSIC_FRAGMENT = "tag_music_fragment";
    private Toolbar mToolbar;
    private MusicPlayerFragment mMusicPlayerFragment;
    private MenuItem mOptionItem;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mMusicPlayerFragment.refreshIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.music_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {






       return  super.onPrepareOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.switch_lrc:
                mMusicPlayerFragment.toggleLRCView();
                break;
/*            case R.id.me_album:
                mMusicPlayerFragment.switchLRCView(false);
                break;
            case R.id.me_lrc:
                mMusicPlayerFragment.switchLRCView(true);
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.music_activity_layout);
        mMusicPlayerFragment = new MusicPlayerFragment();
        getFragmentManager().beginTransaction().add(R.id.content_container, mMusicPlayerFragment, TAG_MUSIC_FRAGMENT).commit();

        initToolBar();
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("MUSIC");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }
}
