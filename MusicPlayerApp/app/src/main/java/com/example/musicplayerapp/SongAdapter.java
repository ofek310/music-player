package com.example.musicplayerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    public static ArrayList<Song> songList;
    private Context context;
    private MySongListener listener;

    interface MySongListener{
        void onSongClicked(int position,View view);
    }
    public void setListener(MySongListener listener){
        this.listener =listener;
    }

    public SongAdapter(ArrayList<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder  {
        TextView songNameTv;
        TextView songWriterTv;
        ImageView songPictureIv;

        public SongViewHolder(View itemView) {
            super(itemView);
            songNameTv = itemView.findViewById(R.id.song_name);
            songWriterTv = itemView.findViewById(R.id.song_writer);
            songPictureIv = itemView.findViewById(R.id.song_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    if (MusicService.IS_ACTIVITY_RUNNING){
                        MusicService.mediaPlayer.stop();
                        MusicService.mediaPlayer.reset();
                    }
                    listener.onSongClicked(getAdapterPosition(),v);
                }
            });
        }

    }
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.song_cell,parent,false);
        SongViewHolder songViewHolder = new SongViewHolder(view);
        return songViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songNameTv.setText(song.getSongName());
        holder.songWriterTv.setText(song.getSongWriter());
        Glide.with(this.context)
                .load(song.getLinkPicture())
                .circleCrop()
                .into(holder.songPictureIv);
    }

    @Override
    public int getItemCount() { return songList.size(); }

    @Override
    public int getItemViewType(int position) { return super.getItemViewType(position); }
    public void onItemMove(int fromPosition, int toPosition) {
        if(MusicService.currentPlaying==fromPosition) {
            MusicService.currentPlaying = toPosition;
        }else if(MusicService.currentPlaying>fromPosition&&MusicService.currentPlaying<=toPosition){
            MusicService.currentPlaying--;
        }else if(MusicService.currentPlaying<fromPosition&&MusicService.currentPlaying>=toPosition){
            MusicService.currentPlaying++;
        }
        Song fromSong = songList.get(fromPosition);
        songList.remove(fromPosition);
        songList.add(toPosition,fromSong);
        notifyItemMoved(fromPosition,toPosition);
    }

    public void onItemSwiped(int position) {
         Song song = songList.remove(position);
         notifyItemRemoved(position);
         android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
         if(MusicService.currentPlaying==position) {
             if (MusicService.IS_ACTIVITY_RUNNING) {
                 MusicService.mediaPlayer.reset();
             }
         }else if(MusicService.currentPlaying>position){
             MusicService.currentPlaying--;
         }
         builder.setTitle(context.getResources().getString(R.string.confirmation))
                 .setMessage(context.getResources().getString(R.string.sure_to_delete))
                 .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         if(MusicService.IS_ACTIVITY_RUNNING) {
                             if (songList.size() == 0) {
                                 Intent intent = new Intent(context, MusicService.class);
                                 context.stopService(intent);
                             } else {
                                 if (MusicService.currentPlaying == MainActivity.songList.size()) {
                                     Intent intent = new Intent(context, MusicService.class);
                                     intent.putExtra("command", "new_instance");
                                     intent.putExtra("position", 0);
                                     context.startService(intent);
                                 } else {
                                     Intent intent = new Intent(context, MusicService.class);
                                     intent.putExtra("command", "new_instance");
                                     intent.putExtra("position", position);
                                     context.startService(intent);
                                 }
                             }
                         }
                     }
                 })
                 .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         songList.add(position,song);
                         notifyItemInserted(position);
                         if(MusicService.IS_ACTIVITY_RUNNING) {
                             if (MusicService.currentPlaying == MainActivity.songList.size()) {
                                 Intent intent = new Intent(context, MusicService.class);
                                 intent.putExtra("command", "new_instance");
                                 intent.putExtra("position", 0);
                                 context.startService(intent);
                             } else {
                                 Intent intent = new Intent(context, MusicService.class);
                                 intent.putExtra("command", "new_instance");
                                 intent.putExtra("position", position);
                                 context.startService(intent);
                             }
                         }
                     }
                 })
                 .setCancelable(false).show();
    }
}

