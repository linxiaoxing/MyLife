package com.example.note.dialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylife.R;
import com.example.note.adapter.ImageDialogViewPagerAdapter;

import java.util.List;

public class ImageLargeDialogFragment extends DialogFragment{

    private List<Bitmap> mThumbnails;
    private RecyclerView mRecyclerView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_image_large, null);
        builder.setView(content);

        builder.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
        });
        mRecyclerView = (RecyclerView) content.findViewById(R.id.image_dialog_recyclerview);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageDialogViewPagerAdapter adapter = new ImageDialogViewPagerAdapter(mThumbnails);
        LinearLayoutManager ms= new LinearLayoutManager(getContext());
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mRecyclerView.setLayoutManager(ms);
        mRecyclerView.setAdapter(adapter);
    }

    public void setImageView(final List<Bitmap> thumbnails) {
        mThumbnails = thumbnails;
    }
}
