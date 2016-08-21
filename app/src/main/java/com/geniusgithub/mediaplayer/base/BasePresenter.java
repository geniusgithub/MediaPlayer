package com.geniusgithub.mediaplayer.base;

public interface BasePresenter <T extends BaseView> {
    public void bindView(T view);

    public void unBindView();
}
