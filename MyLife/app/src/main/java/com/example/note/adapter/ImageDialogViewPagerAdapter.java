package com.example.note.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylife.R;

import java.util.List;

public class ImageDialogViewPagerAdapter extends RecyclerView.Adapter<ImageDialogViewPagerAdapter.ItemViewHolder>{

    private List<Bitmap> mThumbnails;

    public ImageDialogViewPagerAdapter(final List<Bitmap> thumbnails) {
        this.mThumbnails = thumbnails;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_dialog_item,
                parent, false );
        return new ItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.imageView.setImageBitmap(mThumbnails.get(position));
    }

    @Override
    public int getItemCount() {
        return mThumbnails.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.large_image);
        }
    }
}
