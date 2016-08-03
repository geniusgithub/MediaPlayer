package com.geniusgithub.mediaplayer.player.picture;

import com.geniusgithub.mediaplayer.base.BasePresenterFragment;
import com.geniusgithub.mediaplayer.base.IBaseFragmentPresent;

public class PictureFragment extends BasePresenterFragment<IBaseFragmentPresent> {


    @Override
    public IBaseFragmentPresent createPresenter(){
        return new PicturePlayPresenter();
    }

}
