package com.geniusgithub.mediaplayer.player.music;

import android.content.Context;
import android.content.Intent;

public abstract class MusicPlayerPresenter implements MusicPlayerContact.IPresenter {

    protected MusicPlayerContact.IView mView;

    public static final String PLAY_INDEX = "player_index";

    @Override
    public void bindView(MusicPlayerContact.IView view) {
        mView = view;
        mView.bindPresenter(this);
    }

    @Override
    public void unBindView() {

    }


    public abstract  void onUiCreate(Context context);

    public abstract  void onUiDestroy();

    public abstract void onNewIntent(Intent intent);
}
