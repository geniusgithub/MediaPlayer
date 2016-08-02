package com.geniusgithub.mediaplayer.player.music;

import com.geniusgithub.mediaplayer.base.BasePresenterFragment;
import com.geniusgithub.mediaplayer.base.IBaseFragmentPresent;
import com.geniusgithub.mediaplayer.player.music.MusicPlayerPresenter;

public class MusicPlayerFragment extends BasePresenterFragment<IBaseFragmentPresent> {


    public static final String PLAY_INDEX = "player_index";

    @Override
    public IBaseFragmentPresent createPresenter(){
        return new MusicPlayerPresenter();
    }

}
