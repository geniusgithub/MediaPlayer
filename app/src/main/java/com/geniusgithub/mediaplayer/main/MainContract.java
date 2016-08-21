package com.geniusgithub.mediaplayer.main;

import com.geniusgithub.mediaplayer.base.BasePresenter;
import com.geniusgithub.mediaplayer.base.BaseView;


public class MainContract {
    public interface IView extends BaseView<IPresenter> {
        public void updateLocalAddress(String value);
        public void toggleDrawLayout();
        public boolean onBackPressed();
    }

    public interface IPresenter extends BasePresenter<IView> {
        public void onStart();
        public void onRestart();
        public void onStop();
        public void onExit();
    }
}
