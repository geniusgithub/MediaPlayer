package com.geniusgithub.mediaplayer.player.base;

public interface IPlayback {

    void setPlayList(PlayList list);

    void setPlayMode(PlayMode mode);

    void setInitPlayProgress(int progress);

    boolean play();

    boolean play(PlayList list);

    boolean play(PlayList list, int startIndex);

    boolean playLast();

    boolean playNext();

    boolean pause();

    boolean isPause();

    void stop();

    int getProgress();

    int getDuration();

    boolean seekTo(int progress);

    void releasePlayer();

    void registerCallback(PlayStateCallback callback);

    void unregisterCallback(PlayStateCallback callback);

}
