package com.example.musicplayerapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NewSongDialogFragment.OnSaveNewSong{
    final String DIALOG_FRAGMENT_TAG = "dialog_fragment";
    final String IMAGE_FRAGMENT_TAG = "image_dialog_fragment";
    final String PREFS_NAME = "MyPrefsFile";

    public static ArrayList<Song> songList;
    NewSongDialogFragment fragment;
    BigPictureSongDialogFragment imageFragment;
    SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton new_song_btn = findViewById(R.id.add_new_song_button);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSongs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            songList.add(new Song("רחוק מכולם", "עומר אדם", "https://upload.wikimedia.org/wikipedia/he/thumb/7/73/%D7%A8%D7%97%D7%95%D7%A7_%D7%9E%D7%9B%D7%95%D7%9C%D7%9D.jpg/1200px-%D7%A8%D7%97%D7%95%D7%A7_%D7%9E%D7%9B%D7%95%D7%9C%D7%9D.jpg", "https://www.syntax.org.il/xtra/bob.m4a"));
            songList.add(new Song("אחרי החתונה", "עומר אדם", "https://i.ytimg.com/vi/DgJIu0FBOB8/maxresdefault.jpg", "https://www.syntax.org.il/xtra/bob1.m4a"));
            songList.add(new Song("קוקוריקו", "עומר אדם", "https://upload.wikimedia.org/wikipedia/he/c/ce/%D7%A7%D7%95%D7%A7%D7%95%D7%A8%D7%99%D7%A7%D7%95.jpg", "https://www.syntax.org.il/xtra/bob2.mp3"));
            songList.add(new Song("מבטיח", "עדן חסון", "https://upload.wikimedia.org/wikipedia/he/2/27/%D7%A2%D7%93%D7%9F_%D7%97%D7%A1%D7%95%D7%9F_-_%D7%9E%D7%91%D7%98%D7%99%D7%97.jpg", "https://www.syntax.org.il/xtra/bob.m4a"));
            songList.add(new Song("שימי פס", "עדן חסון ואלה לי", "https://upload.wikimedia.org/wikipedia/he/thumb/f/f6/%D7%A9%D7%99%D7%9E%D7%99_%D7%A4%D7%A1.jpg/1200px-%D7%A9%D7%99%D7%9E%D7%99_%D7%A4%D7%A1.jpg", "https://www.syntax.org.il/xtra/bob1.m4a"));
            songList.add(new Song("פרצופים", "עומר אדם", "https://upload.wikimedia.org/wikipedia/he/7/72/%D7%A2%D7%95%D7%9E%D7%A8_%D7%90%D7%93%D7%9D_-_%D7%A4%D7%A8%D7%A6%D7%95%D7%A4%D7%99%D7%9D.jpeg", "https://www.syntax.org.il/xtra/bob2.mp3"));
            songList.add(new Song("גדל לי קצת זקן", "עדן חסון", "https://upload.wikimedia.org/wikipedia/he/thumb/8/86/%D7%92%D7%93%D7%9C_%D7%9C%D7%99_%D7%A7%D7%A6%D7%AA_%D7%96%D7%A7%D7%9F.jpg/1200px-%D7%92%D7%93%D7%9C_%D7%9C%D7%99_%D7%A7%D7%A6%D7%AA_%D7%96%D7%A7%D7%9F.jpg", "https://www.syntax.org.il/xtra/bob.m4a"));
            settings.edit().putBoolean("my_first_time", false).commit();
        }else {
            try {
                FileInputStream fis = openFileInput("Songs");
                ObjectInputStream ois = new ObjectInputStream(fis);
                songList = (ArrayList<Song>) ois.readObject();
                ois.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        songAdapter = new SongAdapter(songList, this);
        songAdapter.setListener(new SongAdapter.MySongListener() {
            @Override
            public void onSongClicked(int position, View view) {
                Song song = songList.get(position);
                imageFragment = BigPictureSongDialogFragment.newInstance(song.getSongName(),song.getSongWriter(),
                        song.getLinkPicture());
                imageFragment.show(getSupportFragmentManager(), IMAGE_FRAGMENT_TAG);
                Intent intent=new Intent(MainActivity.this,MusicService.class);
                intent.putExtra("command","new_instance");
                intent.putExtra("position",position);
                startService(intent);
            }
        });
        ItemTouchHelper.Callback callback = new MyItemTouchHelper(songAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(songAdapter);

        new_song_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = NewSongDialogFragment.newInstance(songList.size());
                fragment.show(getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });
    }
    @Override
    public void onSave(Song song) {
        songList.add(song);
        fragment.dismiss();
        songAdapter.notifyItemInserted(songList.size()-1);
        Toast.makeText(this, getResources().getString(R.string.song_save), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            FileOutputStream fos = openFileOutput("Songs",MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(songList);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}