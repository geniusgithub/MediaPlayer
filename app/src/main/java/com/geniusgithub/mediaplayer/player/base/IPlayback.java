package com.geniusgithub.mediaplayer.player.base;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 6:02 PM
 * Desc: IPlayer
 */
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

//    void setPlayMode(PlayMode playMode);

/*

    void removeCallbacks();

  */

/*    interface Callback {

        void onSwitchLast(@Nullable Song last);

        void onSwitchNext(@Nullable Song next);

        void onComplete(@Nullable Song next);

        void onPlayStatusChanged(boolean isPlaying);
    }*/
}
