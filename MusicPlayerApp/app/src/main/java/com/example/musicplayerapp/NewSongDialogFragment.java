package com.example.musicplayerapp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

public class NewSongDialogFragment extends DialogFragment {
    ImageView picture;
    ImageButton cameraBtn,galleryBtn,saveBtn,cancelBtn;
    EditText songNameEt,songWriterEt,songLinkEt;
    File file;
    Boolean fromGallery=false;
    String imageG;

    private ActivityResultLauncher<Uri> takeImageResult = registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
        if (isSuccess && file != null) {
            Glide.with(getContext())
                    .load(file.getAbsolutePath())
                    .into(picture);
            fromGallery=false;
        }
    });
    private ActivityResultLauncher<String> takeImageGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Glide.with(getContext())
                    .load(result)
                    .into(picture);
            imageG=result.toString();
            fromGallery=true;
        }
    });
    interface OnSaveNewSong{
        void onSave(Song song);
    }
    OnSaveNewSong callBack;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callBack = (OnSaveNewSong) context;
        } catch (ClassCastException ex){
            throw new ClassCastException("the activity must implement OnSaveNewSong intarface");
        }
    }
    public static NewSongDialogFragment newInstance(int place_in_array){
        NewSongDialogFragment newSongDialogFragment = new NewSongDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("place",place_in_array);
        newSongDialogFragment.setArguments(bundle);
        return  newSongDialogFragment;
    }
    private Uri getTmpFileUri() {
        try {
            File tmpFile = File.createTempFile("tmp_image_file"+getArguments().getInt("place"), ".png", getActivity().getCacheDir());
            tmpFile.createNewFile();
            tmpFile.deleteOnExit();
            file = tmpFile;
            return FileProvider.getUriForFile(getActivity().getApplicationContext(), "com.example.musicplayerapp.provider", tmpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_a_song,container,false);
        getDialog().setCanceledOnTouchOutside(false);

        picture = rootView.findViewById(R.id.picture);
        cameraBtn = rootView.findViewById(R.id.camera_button);
        saveBtn = rootView.findViewById(R.id.save_button);
        galleryBtn = rootView.findViewById(R.id.gallery_button);
        cancelBtn = rootView.findViewById(R.id.cancel_button);
        songNameEt = rootView.findViewById(R.id.edit_text_song_name);
        songWriterEt = rootView.findViewById(R.id.edit_text_song_link);
        songLinkEt = rootView.findViewById(R.id.edit_text_song_link);

        cameraBtn.setOnClickListener(v -> {
            File file = new File(getActivity().getFilesDir(), "picFromCamera");
            Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
            takeImageResult.launch(getTmpFileUri());
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songName = songNameEt.getText().toString();
                String songWriter = songWriterEt.getText().toString();
                String songLink = songLinkEt.getText().toString();
                if (songName.matches("") || songWriter.matches("") || songLink.matches("") ||
                        picture.getDrawable() == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.not_finish_to_fill), Toast.LENGTH_SHORT).show();
                } else {
                    //need remove this frgment and add a new song to the list
                    Song newSong;
                    if(fromGallery)
                        newSong = new Song(songName,songWriter,imageG,songLink);
                    else
                        newSong = new Song(songName,songWriter,file.getAbsolutePath(),songLink);
                    callBack.onSave(newSong);
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImageGallery.launch("image/*");
            }
        });
        return rootView;
    }
}