package com.geniusgithub.mediaplayer.browse;


import com.geniusgithub.mediaplayer.IToolBar;
import com.geniusgithub.mediaplayer.base.BasePresenterFragment;
import com.geniusgithub.mediaplayer.base.IBaseFragmentPresent;



public class BrowserMediaFragment extends BasePresenterFragment<IBaseFragmentPresent> {


    @Override
    public IBaseFragmentPresent createPresenter() {
        return new BrowsePresenter();
    }

    private IToolBar mExternToolbar;
    public void bindToolbar(IToolBar toolbar){
        mExternToolbar = toolbar;
    }

    public void onViewSwitch(String title){
        if (mExternToolbar != null){
            mExternToolbar.updateToolTitle(title);
        }
    }


}
