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

public class AddImageDialogFragment extends DialogFragment{

    private ImageDialogListener mImageDialogListener;

    public void setSoundDialogListener(final ImageDialogListener imageDialogListener) {
        this.mImageDialogListener = imageDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加图片");
        builder.setItems(new CharSequence[]{"拍照", "选择图片"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, EditActivity.PERMISSION_REQUEST_CAMERA);
                    } else {
                        mImageDialogListener.takePhotoCallBack();
                    }
                }
            }
        });
        return builder.create();
    }

    public interface ImageDialogListener {
        void takePhotoCallBack();
    }
}