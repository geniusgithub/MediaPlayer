package com.geniusgithub.mediaplayer.base;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface IBaseFragmentPresent {

    public void bindFragment(Fragment fragment);
    public void onNewIntent(Intent intent);
    public void onAttach(Context context);
    public void onCreate(Bundle savedInstanceState);
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    public void onViewCreated(View view, Bundle savedInstanceState);
    public void onResume();
    public void onPause();
    public void onDestroy();
    public boolean onBackPressed();
}
