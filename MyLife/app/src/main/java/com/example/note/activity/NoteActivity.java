package com.example.note.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.example.mylife.R;
import com.example.mylife.activity.TodoActivity;
import com.example.mylife.activity.aboutme.AboutActivity;
import com.example.mylife.activity.login.LoginActivity;
import com.example.mylife.base.BaseActivity;
import com.example.note.adapter.ItemRecyclerViewAdapter;
import com.example.note.adapter.ItemRecyclerViewDecoration;
import com.example.note.data.dbhelper.AppDatabase;
import com.example.note.data.entity.Image;
import com.example.note.data.entity.Note;
import com.example.note.data.entity.Sound;
import com.example.note.dialogFragment.SoundPlayerDialogFragment;
import com.example.note.utils.KeybordUtil;
import com.example.ocr.module.CharActivity;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NoteActivity extends BaseActivity implements ItemRecyclerViewAdapter.ItemAdapterListener{
    @BindView(R.id.recyclerview)
    RecyclerView itemRecyclerView;
    @BindView(R.id.fab_fu_add_task)
    FloatingActionButton fabFuAddTask;

    public static final String EXTRA_NOTE_POSITION = "mynote.EXTRA_NOTE_POSITION";
    public static final String EXTRA_NOTE = "mynote.EXTRA_NOTE";
    public static final String EXTRA_IMAGE_LIST = "mynote.EXTRA_IMAGE_LIST";
    public static final String EXTRA_SOUND_LIST = "mynote.EXTRA_SOUND_LIST";
    public static final int NOTE_REQUEST_BACK = 10;

    public static ArrayList<Note> mDataset = new ArrayList<>();
    public static ArrayList<ArrayList<Image>> mImageDataset = new ArrayList<>();
    public static ArrayList<ArrayList<Sound>> mSoundDataset = new ArrayList<>();
    public static CoordinatorLayout coordinatorLayout_snackbar;
    private ItemRecyclerViewAdapter itemRecyclerViewAdapter;
    private AppDatabase appDatabase;

    private Handler mHandler = new Handler( Looper.getMainLooper());

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note;
    }

    @Override
    protected void initInjector() {
//        mActivityComponent.inject(this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("记事本");
        coordinatorLayout_snackbar = findViewById(R.id.coordinatorlayout);

        (new Thread() {
            @Override
            public void run() {
                super.run();
                appDatabase = AppDatabase.getInstance();
                mDataset.clear();
                mImageDataset.clear();
                mSoundDataset.clear();
                mDataset.addAll(appDatabase.noteDAO().getAll());
                for (Note note : mDataset) {
                    ArrayList<Image> imageArrayList = new ArrayList<>();
                    imageArrayList.addAll(appDatabase.imageDAO().getAll(note.id));
                    mImageDataset.add(imageArrayList);
                    ArrayList<Sound> soundArrayList = new ArrayList<>();
                    soundArrayList.addAll(appDatabase.soundDAO().getAll(note.id));
                    mSoundDataset.add(soundArrayList);
                }
            }
        }).start();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(layoutManager);
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(this, getSupportFragmentManager(),
                mDataset, mImageDataset, mSoundDataset);
        itemRecyclerViewAdapter.setItemClickListener(this);
        itemRecyclerView.setAdapter(itemRecyclerViewAdapter);
        itemRecyclerView.addItemDecoration(new ItemRecyclerViewDecoration(20));
    }

    @OnClick(R.id.fab_fu_add_task)
    public void onViewClicked() {
        //TODO
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EXTRA_NOTE_POSITION, -1);
        intent.putExtra(EXTRA_NOTE, new Note());
        intent.putExtra(EXTRA_IMAGE_LIST, new ArrayList<Image>());
        intent.putExtra(EXTRA_SOUND_LIST, new ArrayList<Sound>());
        startActivityForResult(intent, NOTE_REQUEST_BACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHot:
                //清除Cookie
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage("确定要退出登录吗?");
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ClearableCookieJar cookieJar =
                                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(NoteActivity.this));
                        cookieJar.clear();
                        SPUtils.getInstance().put("study", false);
                        startActivity(new Intent(NoteActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                builder.show();
                break;
            case R.id.menuMe:
                startActivity(new Intent(NoteActivity.this, AboutActivity.class));
                break;
            case R.id.menuTodo:
                startActivity(new Intent(NoteActivity.this, TodoActivity.class));
                break;
            case R.id.menuChar:
                startActivity(new Intent(NoteActivity.this, CharActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) {
            return;
        }
        int position = data.getIntExtra(EXTRA_NOTE_POSITION, -1 );
        Note note = (Note) data.getSerializableExtra( EXTRA_NOTE );
        ArrayList<Image> imageArrayList = (ArrayList<Image>) data.getSerializableExtra( EXTRA_IMAGE_LIST );
        ArrayList<Sound> soundArrayList = (ArrayList<Sound>) data.getSerializableExtra( EXTRA_SOUND_LIST );
        if (position == -1) {
            itemRecyclerViewAdapter.addNote( note, imageArrayList, soundArrayList );
        } else {
            itemRecyclerViewAdapter.reviseNote( position, note, imageArrayList, soundArrayList );
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    @Override
    public void soundPlayer(Sound sound) {
        SoundPlayerDialogFragment soundPlayerDialogFragment = new SoundPlayerDialogFragment();
        soundPlayerDialogFragment.setSoundData(sound);
        soundPlayerDialogFragment.show(getSupportFragmentManager(), "play-sound");
    }

    @Override
    public void searchAction(final String searchText) {
        (new Thread() {
            @Override
            public void run() {
                super.run();

                appDatabase = AppDatabase.getInstance();
                List<Note> notes = new ArrayList<>();
                if (searchText.isEmpty()) {
                    notes = appDatabase.noteDAO().getAll();
                } else {
                    notes = appDatabase.noteDAO().get("%" + searchText + "%");
                }
                final List<Note> noteList = notes;
                mDataset.clear();
                mImageDataset.clear();
                mSoundDataset.clear();
                if (noteList.size() != 0) {
                    mDataset.addAll(noteList);
                    for (Note note : mDataset) {
                        ArrayList<Image> imageArrayList = new ArrayList<>();
                        imageArrayList.addAll(appDatabase.imageDAO().getAll(note.id));
                        mImageDataset.add(imageArrayList);
                        ArrayList<Sound> soundArrayList = new ArrayList<>();
                        soundArrayList.addAll(appDatabase.soundDAO().getAll(note.id));
                        mSoundDataset.add(soundArrayList);
                    }
                    mHandler.postDelayed(new Runnable( ){
                        @Override
                        public void run() {
                            itemRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }
            }
        }).start();
    }

    @Override
    public void hideKeyboard() {
        if (getCurrentFocus() == null) {
            KeybordUtil.closeKeybord(this);
            return;
        }
        KeybordUtil.hideKeyboard(this);
    }
}
