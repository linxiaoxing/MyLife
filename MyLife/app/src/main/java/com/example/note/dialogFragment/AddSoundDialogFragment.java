package com.example.note.dialogFragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.note.activity.EditActivity;

public class AddSoundDialogFragment extends DialogFragment{

    private SoundDialogListener mSoundDialogListener;

    public void setSoundDialogListener(final SoundDialogListener soundDialogListener) {
        this.mSoundDialogListener = soundDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加声音");
        builder.setItems(new CharSequence[]{"录音", "选择声音"},new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, EditActivity.PERMISSION_REQUEST_RECORD_AUDIO);
                    } else {
                        mSoundDialogListener.recordSoundCallBack();
                    }
                }
            }
        });
        return builder.create();
    }

    public interface SoundDialogListener {
        void recordSoundCallBack();
    }
}
