package com.geniusgithub.mediaplayer.component;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

public class MyGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        CacheManager.getInstance().initConfigure(builder);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
