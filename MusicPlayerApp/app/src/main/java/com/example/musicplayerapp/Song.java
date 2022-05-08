package com.example.musicplayerapp;

import java.io.Serializable;
import java.net.URL;

public class Song implements Serializable {
    private String songName;
    private String songWriter;
    private String linkPicture;
    private String linkSong;

    public Song(String songName, String songWriter,String linkPicture,String linkSong) {
        this.songName = songName;
        this.songWriter = songWriter;
        this.linkPicture = linkPicture;
        this.linkSong = linkSong;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongWriter() {
        return songWriter;
    }

    public void setSongWriter(String songWriter) {
        this.songWriter = songWriter;
    }

    public String getLinkPicture() { return linkPicture; }

    public void setLinkPicture(String linkPicture) { this.linkPicture = linkPicture; }
    public String getLinkSong() { return linkSong; }

    public void setLinkSong(String linkSong) { this.linkSong = linkSong; }

}
