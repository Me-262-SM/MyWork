package com.edu.sicnu.cs.zzy.mywork;

import java.io.Serializable;

public class Music implements Serializable {
    private String musicName;
    private String musicPath;
    private String musicArtist;
    private int duration;

    public Music(String musicName, String musicPath, String musicArtist,int duration) {
        this.musicName = musicName;
        this.musicPath = musicPath;
        this.musicArtist = musicArtist;
        this.duration = duration;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public int getDuration() {
        return duration;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public void setMusicArtist(String musicArtist) {
        this.musicArtist = musicArtist;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
