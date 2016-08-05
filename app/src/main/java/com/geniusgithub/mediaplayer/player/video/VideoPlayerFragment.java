package com.geniusgithub.mediaplayer.player.video;

import com.geniusgithub.mediaplayer.base.BasePresenterFragment;
import com.geniusgithub.mediaplayer.base.IBaseFragmentPresent;

public class VideoPlayerFragment extends BasePresenterFragment<IBaseFragmentPresent> {

    @Override
    public IBaseFragmentPresent createPresenter() {
        return new VideoPlayerPresenter();
    }
}
