package com.example.note.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.mylife.R;
import com.example.note.data.entity.Sound;
import com.example.note.utils.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SoundPlayerDialogFragment extends DialogFragment{

    private Dialog dialog = null;
    private ImageView sound_player_image;
    private TextView sound_player_title;
    private SeekBar sound_player_progress;
    private TextView sound_player_time;
    private ImageButton sound_player_play;
    private ImageButton sound_player_stop;

    private Sound mSound;

    private boolean isStart;
    private boolean isPlaying;
    private MediaPlayer mediaPlayer;
    private String duration;
    private final long period = 200;

    Handler timer = new Handler();
    Runnable onTimer = new Runnable() {
        @Override
        public void run() {
            int pos = mediaPlayer.getCurrentPosition();
            sound_player_progress.setProgress(pos);
            sound_player_time.setText(new SimpleDateFormat("mm:ss").format(pos) + "/" + duration);
            timer.postDelayed(this, period);
        }
    };

    public SoundPlayerDialogFragment() {
        super();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        this.dialog.hide();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (dialog != null) {
            return dialog;
        }

        View view = getActivity().getLayoutInflater().inflate( R.layout.dialog_sound_player, null);
        sound_player_image = view.findViewById(R.id.sound_player_image);
        sound_player_title = view.findViewById(R.id.sound_player_title);
        sound_player_progress = view.findViewById(R.id.sound_player_progress);
        sound_player_time = view.findViewById(R.id.sound_player_time);
        sound_player_play = view.findViewById(R.id.sound_player_play);
        sound_player_stop = view.findViewById(R.id.sound_player_stop);

        setSoundMetadata(null);

        sound_player_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b == false) {
                    return;
                }
                mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sound_player_play.setOnClickListener(new OnClickListener_control());
        sound_player_stop.setOnClickListener(new OnClickListener_cancel());
        sound_player_play.setImageResource(android.R.drawable.ic_media_play);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        dialog = builder.create();

        isStart = false;
        isPlaying = false;

        initPlaySound("2.flac");

        return dialog;
    }

    public void setSoundData(final Sound sound) {
        if (sound != null) {
            mSound = sound;
        } else {
            (new Thread() {
                @Override
                public void run() {
                    setPlayerFile();
                }
            }).start();
        }
    }

    private void setPlayerFile() {
        File file = new File(getActivity().getExternalFilesDir(null).getAbsoluteFile() + "/sound/" + "2.flac");

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.piano2);
            OutputStream output
                    = new FileOutputStream(file); // 出力ファイルを開く
            int c;
            while ((c = inputStream.read()) != -1)       // 入力
                output.write(c);                   // 出力
            inputStream.close();          // 入力ファイルを閉じる
            output.close();         // 出力ファイルを閉じる
        } catch (IOException e) {   // 入出力エラーをつかまえる
            System.err.println(e);  // エラーメッセージ出力
        }
    }

    class OnClickListener_control implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (isStart) {
                if (isPlaying) {
                    pausePlaying();
                } else {
                    resumePlaying();
                }
            } else {
                startPlayerSound();
            }
        }
    }

    class OnClickListener_cancel implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (mediaPlayer != null) {
                stopPlaySound();
            }
        }
    }

    private void initPlaySound(String filename) {
        File file;
        if (mSound.path.isEmpty()) {
            file = new File( getActivity( ).getExternalFilesDir( null ).getAbsoluteFile( ) + "/sound/" + filename );
        } else {
            file = new File(mSound.path);
        }
        Uri soundUri = Tool.getUri(getActivity(), file);
        if (soundUri == null) {
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlaySound();
                }
            });
            mediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getActivity().getApplicationContext(), soundUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        setSoundMetadata(filename);
    }

    private void setSoundMetadata(String filename) {
        if (filename == null) {
            sound_player_image.setImageResource(android.R.color.holo_blue_dark);
            sound_player_title.setText("");
            sound_player_time.setText("00:00/00:00");
            sound_player_progress.setMax(-1);
        } else {
            String title;
            String artist;
            if (mSound.path.isEmpty()) {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(getActivity().getExternalFilesDir(null).getAbsolutePath() + "/sound/" + filename);
                byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
                if (picture != null) {
                    Bitmap albumCover = BitmapFactory.decodeByteArray( picture, 0, picture.length );
                    sound_player_image.setImageBitmap( albumCover );
                }
                title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                mediaMetadataRetriever.release();
            } else {
                title = mSound.title;
                artist = String.valueOf(mSound.note_id);
            }
            sound_player_title.setText(title == null ? title : "" + " - " + artist == null ? artist : "");
            duration = new SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getDuration()));
            sound_player_time.setText("00:00/" + duration);
            sound_player_progress.setMax(mediaPlayer.getDuration());
        }
    }

    private void startPlayerSound() {
        mediaPlayer.start();
        isStart = true;
        isPlaying = true;
        sound_player_play.setImageResource(android.R.drawable.ic_media_pause);
        sound_player_progress.setMax(mediaPlayer.getDuration());
        timer.postDelayed(onTimer, period);
    }

    private void stopPlaySound() {
        mediaPlayer.stop();
        isStart = false;
        isPlaying = false;
        sound_player_play.setImageResource(android.R.drawable.ic_media_play);
        timer.removeCallbacks(onTimer);
        mediaPlayer.release();
        mediaPlayer = null;
        setSoundMetadata(null);
        dismiss();
    }

    private void pausePlaying() {
        mediaPlayer.pause();
        isPlaying = false;
        sound_player_play.setImageResource(android.R.drawable.ic_media_play);
        timer.removeCallbacks(onTimer);
    }

    private void resumePlaying() {
        mediaPlayer.start();
        isPlaying = true;
        sound_player_play.setImageResource(android.R.drawable.ic_media_pause);
        timer.postDelayed(onTimer, period);
    }
}