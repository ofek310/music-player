package com.example.musicplayerapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{
    public static MediaPlayer mediaPlayer;
    ArrayList<Song> songs;
    public static boolean IS_ACTIVITY_RUNNING=false;
    //who player in the list
    public static int currentPlaying=0;
    static final int NOTIF_ID=1;
    static RemoteViews remoteView;
    static NotificationManager manager;
    static NotificationCompat.Builder builder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.reset();

        IS_ACTIVITY_RUNNING = true;
        //create notification
        manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String channelId ="channel_id";
        String channelName="Music channel";
        if(Build.VERSION.SDK_INT>=26) {//create channel to the notification if he need
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            //create  notification channel to this channel
            manager.createNotificationChannel(channel);
        }
        builder=new NotificationCompat.Builder(this,channelId);

        //because we designed the layer it should be used
        //infulter the notification layout
        remoteView = new RemoteViews(getPackageName(),R.layout.music_notif);

        //create pending intent that start the notif
        //each one of them put extra that he will know in the function which button was press
        Intent playIntent = new Intent(this,MusicService.class);
        playIntent.putExtra("command","play");
        PendingIntent playPendingIntent = PendingIntent.getService(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.play_song,playPendingIntent);

        Intent pauseIntent = new Intent(this,MusicService.class);
        pauseIntent.putExtra("command","pause");
        PendingIntent pausePendingIntent = PendingIntent.getService(this,1,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.pause_song,pausePendingIntent);

        Intent nextIntent = new Intent(this,MusicService.class);
        nextIntent.putExtra("command","next");
        PendingIntent nextPendingIntent = PendingIntent.getService(this,2,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.next_song,nextPendingIntent);

        Intent prevIntent = new Intent(this,MusicService.class);
        prevIntent.putExtra("command","prev");
        PendingIntent prevPendingIntent = PendingIntent.getService(this,3,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.back_song,prevPendingIntent);

        Intent closeIntent = new Intent(this,MusicService.class);
        closeIntent.putExtra("command","close");
        PendingIntent closePendingIntent = PendingIntent.getService(this,4,closeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.cancel_notification,closePendingIntent);

        builder.setCustomContentView(remoteView);
        builder.setSmallIcon(android.R.drawable.ic_media_play);

        //put the this notfication in the foreground
        startForeground(NOTIF_ID,builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");
        //check which button press
        switch (command){
            case "new_instance"://first time press
                //loading song list
                if(!mediaPlayer.isPlaying()) {
                    songs = MainActivity.songList;
                    currentPlaying = intent.getIntExtra("position",0);
                    remoteView.setTextViewText(R.id.name_song_noti,songs.get(currentPlaying).getSongName());
                    remoteView.setTextViewText(R.id.writer_song_noti,songs.get(currentPlaying).getSongWriter());
                    manager.notify(NOTIF_ID,builder.build());
                    try {//pass the path of song being played
                        mediaPlayer.setDataSource(songs.get(currentPlaying).getLinkSong());
                        //So that it does not get stuck the ui
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "play"://And if there is no song playing, play
                if(!mediaPlayer.isPlaying())
                    mediaPlayer.start();
                break;
            case "next":
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                playSong(true);
                break;
            case "prev":
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                playSong(false);
                break;
            case "pause":
                if(mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;
            case "close":
                stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void playSong(boolean isNext){
        if(isNext){
            currentPlaying++;
            if(currentPlaying==songs.size())//When we get to the end of the list, we will continue in a circular fashion to the first song
                currentPlaying=0;
        }else {
            currentPlaying--;
            if(currentPlaying<0)//When we get to the beginning of the list and want to go back we will go to the last song
                currentPlaying=songs.size()-1;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(songs.get(currentPlaying).getLinkSong());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        remoteView.setTextViewText(R.id.name_song_noti,songs.get(currentPlaying).getSongName());
        remoteView.setTextViewText(R.id.writer_song_noti,songs.get(currentPlaying).getSongWriter());
        manager.notify(NOTIF_ID,builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
        IS_ACTIVITY_RUNNING=false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Finished playing the song and thus the above action takes place
        playSong(true);
    }
    public static void Update(){
        remoteView.setTextViewText(R.id.name_song_noti,MainActivity.songList.get(currentPlaying).getSongName());
        remoteView.setTextViewText(R.id.writer_song_noti,MainActivity.songList.get(currentPlaying).getSongWriter());
        manager.notify(NOTIF_ID,builder.build());
        mediaPlayer.reset();
    }
}
