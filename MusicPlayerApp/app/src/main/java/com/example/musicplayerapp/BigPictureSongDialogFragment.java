package com.example.musicplayerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

public class BigPictureSongDialogFragment extends DialogFragment {
    TextView name_song_tv,writer_song_tv;
    ImageView picture_song_iv;
    ImageButton cancel_image_btn;

    public static BigPictureSongDialogFragment newInstance(String s_song_name,String s_song_writer,String s_song_picture){
        BigPictureSongDialogFragment bigPictureSongDialogFragment = new BigPictureSongDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("b_song_name",s_song_name);
        bundle.putString("b_song_writer",s_song_writer);
        bundle.putString("b_song_picture",s_song_picture);
        bigPictureSongDialogFragment.setArguments(bundle);
        return  bigPictureSongDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_layout,container,false);
        getDialog().setCanceledOnTouchOutside(false);

        name_song_tv=rootView.findViewById(R.id.song_name_tv);
        writer_song_tv=rootView.findViewById(R.id.song_writer_tv);
        picture_song_iv = rootView.findViewById(R.id.image_view_big_size);
        cancel_image_btn = rootView.findViewById(R.id.cancel_image_button);

        name_song_tv.setText(getArguments().getString("b_song_name"));
        writer_song_tv.setText(getArguments().getString("b_song_writer"));

        Glide.with(getContext())
                .load(getArguments().getString("b_song_picture"))
                .into(picture_song_iv);

        cancel_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return rootView;
    }
}
