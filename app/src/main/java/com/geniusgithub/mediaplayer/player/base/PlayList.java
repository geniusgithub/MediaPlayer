package com.geniusgithub.mediaplayer.player.base;

import com.geniusgithub.mediaplayer.dlna.control.model.MediaEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayList<T extends MediaEntry> {

    public static final int NO_POSITION = -1;
    private int playingIndex = -1;

    private List<T> mediaList = new ArrayList<>();
    private PlayMode playMode = PlayMode.LOOP;


    public PlayList(){

    }



    public T getCurrentMedia() {
        if (playingIndex != NO_POSITION && playingIndex < mediaList.size()) {
            return mediaList.get(playingIndex);
        }
        return null;
    }

    public List<T> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<T> list){
        mediaList = list;
    }

    public int getMediaCount (){
        return mediaList.size();
    }



    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }



    public boolean hasLast() {

        if (getMediaCount() > 0){
            return true;
        }
        return false;
    }

    public MediaEntry last() {

        switch (playMode) {
            case LOOP:
            case SINGLE:
                int newIndex = playingIndex - 1;
                if (newIndex < 0) {
                    newIndex = mediaList.size() - 1;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return mediaList.get(playingIndex);

    }

    public boolean hasNext(boolean fromComplete){
        if (mediaList.isEmpty()) return false;
        return true;
    }



    public MediaEntry next() {

        switch (playMode) {
            case LOOP:
            case SINGLE:
                int newIndex = playingIndex + 1;
                if (newIndex >= mediaList.size()) {
                    newIndex = 0;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return mediaList.get(playingIndex);

    }



    private int randomPlayIndex() {
        int randomIndex = new Random().nextInt(mediaList.size());
        // Make sure not play the same song twice if there are at least 2 songs
        if (mediaList.size() > 1 && randomIndex == playingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }
}
