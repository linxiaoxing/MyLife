package com.example.note.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylife.R;
import com.example.note.Type;
import com.example.note.activity.EditActivity;
import com.example.note.activity.NoteActivity;
import com.example.note.data.dbhelper.AppDatabase;
import com.example.note.data.entity.Image;
import com.example.note.data.entity.Note;
import com.example.note.data.entity.Sound;
import com.example.note.dialogFragment.ImageLargeDialogFragment;
import com.example.note.utils.KeybordUtil;
import com.example.note.view.searchview.BCallBack;
import com.example.note.view.searchview.ICallBack;
import com.example.note.view.searchview.SearchView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_DETAIL = 2;
    private List<Note> mDataset;
    private List<ArrayList<Image>> mImageDataset;
    private List<ArrayList<Sound>> mSoundDataset;
    private ItemAdapterListener mItemAdapterListener;
    private FragmentManager mFragmentManager;
    private Activity mContext;

    public interface ItemAdapterListener{
        void soundPlayer(final Sound sound);
        void searchAction(final String searchText);
        void hideKeyboard();
    }

    public void setItemClickListener(ItemAdapterListener itemClickListener) {
        this.mItemAdapterListener = itemClickListener;
    }

    public ItemRecyclerViewAdapter(final Activity context,
                                   final FragmentManager fragmentManager,
                                   final List<Note> mDataset,
                                   final List<ArrayList<Image>> mImageDataset,
                                   final List<ArrayList<Sound>> mSoundDataset) {
        this.mContext = context;
        this.mDataset = mDataset;
        this.mImageDataset = mImageDataset;
        this.mSoundDataset = mSoundDataset;
        this.mFragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_note, parent, false);
            return new RecyclerViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return VIEW_TYPE_HEADER;
            default:
                return VIEW_TYPE_DETAIL;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public SearchView searchView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            searchView = itemView.findViewById(R.id.search_view);
            // 4. 设置点击键盘上的搜索按键后的操作（通过回调接口）
            // 参数 = 搜索框输入的内容
            searchView.setOnClickSearch(new ICallBack() {
                @Override
                public void searchAciton(String string) {
                    searchView.setFocusable(false);
                    mItemAdapterListener.searchAction(string);
                }
            });

            searchView.setOnClickBack(new BCallBack(){
                @Override
                public void backAciton() {
                    mItemAdapterListener.hideKeyboard();
                }
            } );
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView textView_type;
        public TextView textView_title;
        public TextView textView_body;
        public GridLayout gridLayout_image;
        public GridLayout gridLayout_sound;

        public RecyclerViewHolder(final View v) {
            super(v);
            cardView = v.findViewById( R.id.cardView);
            textView_type = v.findViewById(R.id.textView_type);
            textView_title = v.findViewById(R.id.textView_title);
            textView_body = v.findViewById(R.id.textView_body);
            gridLayout_image = v.findViewById(R.id.gridLayout_image);
            gridLayout_sound = v.findViewById(R.id.gridLayout_sound);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition() - 1;
                    Context context = v.getContext();
                    Activity activity = (Activity) context;
                    Intent intent = new Intent(context, EditActivity.class);
                    Note note = mDataset.get(position);
                    ArrayList<Image> imageArrayList = mImageDataset.get(position);
                    ArrayList<Sound> soundArrayList = mSoundDataset.get(position);
                    intent.putExtra(NoteActivity.EXTRA_NOTE_POSITION, position);
                    intent.putExtra(NoteActivity.EXTRA_NOTE, note);
                    intent.putExtra(NoteActivity.EXTRA_IMAGE_LIST, imageArrayList);
                    intent.putExtra(NoteActivity.EXTRA_SOUND_LIST, soundArrayList);
                    activity.startActivityForResult(intent, position);
                    mItemAdapterListener.hideKeyboard();
                }
            });

            gridLayout_image.setOnClickListener(new View.OnClickListener( ){
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition() - 1;
                    int image_width = gridLayout_image.getWidth() * 2 / 3;
                    List<Bitmap> thumbnails = new ArrayList<>();
                    Bitmap bitmap, thumbnail;
                    for (Image image : mImageDataset.get(position)) {
                        int height;
                        bitmap = BitmapFactory.decodeFile(image.path);
                        height = bitmap.getWidth() / (bitmap.getWidth() / image_width);
                        thumbnail = ThumbnailUtils.extractThumbnail(bitmap, image_width, height);
                        thumbnails.add(thumbnail);
                    }
                    mItemAdapterListener.hideKeyboard();
                    if (mFragmentManager.findFragmentByTag("grid_image" + position) != null) {
                        return;
                    }
                    ImageLargeDialogFragment fragment = new ImageLargeDialogFragment();
                    fragment.show(mFragmentManager, "grid_image" + position);
                    fragment.setImageView(thumbnails);
                }
            } );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewholder, final int p) {
        if (p == 0 || p > mDataset.size() || mDataset.size() == 0) {
            return;
        }
        int position = p - 1;
        RecyclerViewHolder holder = (RecyclerViewHolder)viewholder;
        Note note = mDataset.get(position);
        if (note.maintype != -1) {
            if (note.subtype == -1) {
                holder.textView_type.setText("分类：" + Type.MainType.values()[note.maintype]);
            } else {
                holder.textView_type.setText("分类：" + Type.MainType.values()[note.maintype] + "+"
                        + Type.SubJapaneseType.values()[note.subtype]);
            }
        }
        if (!note.title.isEmpty()) {
            holder.textView_title.setText("标题：" + note.title);
        }
        if (!note.body.isEmpty()) {
            holder.textView_body.setText("内容：" + note.body);
        }
        holder.gridLayout_sound.removeAllViewsInLayout();
        for (Sound sound : mSoundDataset.get(position)) {
            TextView textView = new TextView(holder.gridLayout_sound.getContext());
            File file = new File(sound.path);
            textView.setText(file.getName());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rightMargin = 4;
            params.leftMargin = 4;
            params.topMargin = 4;
            params.bottomMargin = 4;
            holder.gridLayout_sound.addView(textView, params);

            ImageView imageView = new ImageView(holder.gridLayout_sound.getContext());
            imageView.setImageResource(R.drawable.sound_play);
            GridLayout.LayoutParams imageViewParams = new GridLayout.LayoutParams();
            imageViewParams.height = 60;
            imageViewParams.width = 60;
            imageViewParams.rightMargin = 4;
            imageViewParams.leftMargin = 4;
            imageViewParams.topMargin = 4;
            imageViewParams.bottomMargin = 4;
            imageViewParams.setGravity(Gravity.RIGHT);
            imageView.setOnClickListener(new View.OnClickListener( ){
                @Override
                public void onClick(View v) {
                    if (mItemAdapterListener != null) {
                        mItemAdapterListener.soundPlayer( sound );
                    }
                }
            } );
            holder.gridLayout_sound.addView(imageView, imageViewParams);
        }
        holder.gridLayout_image.removeAllViewsInLayout();
        holder.gridLayout_image.post(new Runnable() {
            @Override
            public void run() {
                int image_width = holder.gridLayout_image.getWidth() / 3;
                Bitmap bitmap, thumbnail;
                for (Image image : mImageDataset.get(position)) {
                    ImageView imageView;
                    int height;
                    GridLayout.LayoutParams params;
                    bitmap = BitmapFactory.decodeFile(image.path);
                    height = bitmap.getWidth() / (bitmap.getWidth() / image_width);
                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, image_width, height);
                    imageView = new ImageView(holder.gridLayout_image.getContext());
                    imageView.setImageBitmap(thumbnail);
                    params = new GridLayout.LayoutParams();
                    holder.gridLayout_image.addView(imageView, params);
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallBack());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }


    //region Item滑动事件处理
    public class ItemTouchHelperCallBack extends ItemTouchHelper.Callback {
        private int fromPos = -1, toPos = -1;
        private boolean first = true;

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition == toPosition) {
                return false;
            } else {
                if (first == true) {
                    fromPos = fromPosition;
                    first = false;
                }
                toPos = toPosition;
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            (new Thread() {
                @Override
                public void run() {
                    if (fromPos == -1 || toPos == -1) {
                        return;
                    }
                    int from = fromPos, to = toPos;
                    if (from > to) {
                        from = toPos;
                        to = fromPos - 1;
                    }
                    for (int i = from; i < to; i++) {
                        int temp = mDataset.get(i).order;
                        mDataset.get(i).order = mDataset.get(i + 1).order;
                        mDataset.get(i + 1).order = temp;
                        Collections.swap(mDataset, i, i + 1);
                        Collections.swap(mImageDataset, i, i + 1);
                    }
                    AppDatabase appDatabase = AppDatabase.getInstance();
                    List<Note> sublist = mDataset.subList(from, to + 1);
                    appDatabase.noteDAO().update(sublist);

                    first = true;
                    fromPos = -1;
                    toPos = -1;
                }
            }).start();
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deleteNote(position);
        }
    }
    //endregion

    //region Note业务逻辑
    public void addNote(final Note note, final ArrayList<Image> imageArrayList, final ArrayList<Sound> soundArrayList) {
        if (note.title.isEmpty() && note.body.isEmpty() && imageArrayList.isEmpty() && soundArrayList.isEmpty()) {
            Snackbar.make(NoteActivity.coordinatorLayout_snackbar, "已舍弃空白记事", Snackbar.LENGTH_SHORT).show();
            return;
        }

        //Model
        note.order = mDataset.size() == 0 ? 0 : mDataset.get(0).order + 1;
        note.timestamp = new Date();

        //Database
        (new Thread() {
            @Override
            public void run() {
                super.run();
                AppDatabase appDatabase = AppDatabase.getInstance();
                appDatabase.noteDAO().insert(note);
                note.id = appDatabase.noteDAO().getmaxid();
            }
        }).start();

        mDataset.add(0, note);
        mImageDataset.add(0, imageArrayList);
        mSoundDataset.add(0, soundArrayList);

        //View
        notifyItemInserted(0);
    }

    private void deleteBlankNote(int position) {
        //Model
        final Note note = mDataset.remove(position);
        final ArrayList<Image> imageArrayList = mImageDataset.remove(position);
        final ArrayList<Sound> soundArrayList = mSoundDataset.remove(position);

        //Database
        (new Thread() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance();
                appDatabase.noteDAO().delete(note);
                appDatabase.imageDAO().delete(imageArrayList);
                appDatabase.soundDAO().delete(soundArrayList);
            }
        }).start();

        //View
        notifyItemRemoved(position);
        Snackbar.make(NoteActivity.coordinatorLayout_snackbar, "已舍弃空白记事", Snackbar.LENGTH_SHORT).show();
    }

    public void deleteNote(final int position) {
        //Model
        final Note note = mDataset.remove(position);
        final ArrayList<Image> imageArrayList = mImageDataset.remove(position);
        final ArrayList<Sound> soundArrayList = mSoundDataset.remove(position);

        //File
        for (Image image : imageArrayList) {
            File file = new File(image.path);
            file.delete();
        }
        for (Sound sound : soundArrayList) {
            File file = new File(sound.path);
            file.delete();
        }

        //Database
        (new Thread() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance();
                appDatabase.noteDAO().delete(note);
                appDatabase.imageDAO().delete(imageArrayList);
                appDatabase.soundDAO().delete(soundArrayList);
            }
        }).start();

        //View
        notifyItemRemoved(position);
        Snackbar snackbar = Snackbar.make(NoteActivity.coordinatorLayout_snackbar, "已删除记事", Snackbar.LENGTH_SHORT);
        snackbar.setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Model
                mDataset.add(position, note);
                mImageDataset.add(position, imageArrayList);
                mSoundDataset.add(position, soundArrayList);

                //Database
                (new Thread() {
                    @Override
                    public void run() {
                        AppDatabase appDatabase = AppDatabase.getInstance();
                        appDatabase.noteDAO().insert(note);
                        appDatabase.imageDAO().insert(imageArrayList);
                        appDatabase.soundDAO().insert(soundArrayList);
                    }
                }).start();

                //View
                notifyItemInserted(position);
            }
        });
        snackbar.show();
    }

    public void reviseNote(final int position, final Note note,
                           final ArrayList<Image> imageArrayList, final ArrayList<Sound> soundArrayList) {
        if (note.title.isEmpty() && note.body.isEmpty() && imageArrayList.isEmpty() && soundArrayList.isEmpty()) {
            deleteBlankNote(position);
            return;
        }

        //Model
        note.order = mDataset.get(0).order + 1;
        note.timestamp = new Date();
        mDataset.set(position, note);
        mImageDataset.set(position, imageArrayList);
        mSoundDataset.set(position, soundArrayList);

        //Database

        (new Thread() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance();
                appDatabase.noteDAO().update(note);
                appDatabase.imageDAO().update(imageArrayList);
                appDatabase.soundDAO().update(soundArrayList);
            }
        }).start();

        //View
        notifyItemChanged(position);
        Note rnote = mDataset.remove(position);
        ArrayList<Image> rImageList = mImageDataset.remove(position);
        ArrayList<Sound> rSoundList = mSoundDataset.remove(position);
        mDataset.add(0, rnote);
        mImageDataset.add(0, rImageList);
        mSoundDataset.add(0, rSoundList);
        notifyItemMoved(position, 0);
    }
}
