package com.example.note.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.gridlayout.widget.GridLayout;

import com.example.mylife.R;
import com.example.mylife.base.BaseActivity;
import com.example.note.Type;
import com.example.note.data.dbhelper.AppDatabase;
import com.example.note.data.entity.Image;
import com.example.note.data.entity.Note;
import com.example.note.data.entity.Sound;
import com.example.note.dialogFragment.AddImageDialogFragment;
import com.example.note.dialogFragment.AddSoundDialogFragment;
import com.example.note.dialogFragment.PermissonDialogFragment;
import com.example.note.dialogFragment.RecorderDialogFragment;
import com.example.note.utils.KeybordUtil;
import com.example.note.utils.Tool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class EditActivity extends BaseActivity implements
        AddSoundDialogFragment.SoundDialogListener,
        AddImageDialogFragment.ImageDialogListener {

    public static final int PERMISSION_REQUEST_CAMERA = 0;
    public static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;

    public static final int REQUEST_SETTING_PERMISSION = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_RECORD_SOUND = 2;
    public static final int REQUEST_DRAW = 3;

    public static final String EXTRA_DRAWING_PATH = "io.github.mkckr0.mynote.EXTRA_DRAWING_PATH";

    @BindView(R.id.gridLayout_edit_image)
    GridLayout gridLayout_image;
    @BindView(R.id.gridLayout_edit_sound)
    GridLayout gridLayout_sound;
    @BindView(R.id.editText_title)
    EditText editText_title;
    @BindView(R.id.editText_body)
    EditText editText_body;
    @BindView(R.id.textView_time)
    TextView textView_time;
    @BindView(R.id.mainType)
    Spinner spinner;
    @BindView(R.id.subType)
    Spinner subSpinner;
    @BindView(R.id.btn_save)
    Button saveButtton;

    private static int position;
    private static Note note;
    private static ArrayList<Image> imageArrayList;
    private static ArrayList<Sound> soundArrayList;
    private String imagePath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText_title.clearFocus();
        editText_body.clearFocus();

        Note xnote = (Note) getIntent().getSerializableExtra(NoteActivity.EXTRA_NOTE);
        if (xnote != null) {
            note = xnote;
            position = getIntent().getIntExtra(NoteActivity.EXTRA_NOTE_POSITION, -1);
            imageArrayList = (ArrayList<Image>) getIntent().getSerializableExtra(NoteActivity.EXTRA_IMAGE_LIST);
            soundArrayList = (ArrayList<Sound>) getIntent().getSerializableExtra(NoteActivity.EXTRA_SOUND_LIST);
        }

        String title;
        if (note.timestamp == null) {
            title = "添加";
            saveButtton.setText("添加");
        } else {
            title = "修改";
            saveButtton.setText("修改");
        }
        getSupportActionBar( ).setTitle(title);
        editText_title.setText(note.title);
        editText_body.setText(note.body);

        Date date = note.timestamp == null ? new Date() : note.timestamp;
        textView_time.setText(new SimpleDateFormat("yyyy年MM月dd日").format(date));

        gridLayout_image.post(new Runnable() {
            @Override
            public void run() {
                for (Image image : imageArrayList) {
                   // addImageToGridLayout(image.path);
                }
            }
        });

        gridLayout_sound.post(new Runnable() {
            @Override
            public void run() {
                for (Sound sound : soundArrayList) {
                    //addSoundToGridLayout(sound.path);
                }
            }
        });

        initType();
    }

    private void initType() {
        ArrayAdapter adapter = new ArrayAdapter(this.getApplicationContext(),
                android.R.layout.simple_spinner_item, Type.MainType.values());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // spinner に adapter をセット
        // Kotlin Android Extensions
        spinner.setAdapter(adapter);

        // リスナーを登録
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener( ){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if (spinner.getSelectedItem().toString().equals(Type.MainType.RYX.toString())) {
                   subSpinner.setVisibility(View.VISIBLE);
                   ArrayAdapter adapter = new ArrayAdapter(EditActivity.this.getApplicationContext(),
                           android.R.layout.simple_spinner_item, Type.SubJapaneseType.values());

                   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                   subSpinner.setAdapter(adapter);
                   if (note.subtype != -1) {
                       subSpinner.setSelection(note.subtype);
                   }
               } else {
                   subSpinner.setVisibility(View.INVISIBLE);
                   subSpinner.setAdapter(null);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (note.maintype != -1) {
            spinner.setSelection(note.maintype);
        }
    }

    @OnClick(R.id.button_drawing)
    public void drawingClick() {
        Intent intent = new Intent(this, DrawActivity.class);
        startActivityForResult(intent, REQUEST_DRAW);
    }

    @OnClick(R.id.button_sound)
    public void soundClick() {
        AddSoundDialogFragment addSoundDialogFragment = new AddSoundDialogFragment();
        addSoundDialogFragment.show(getSupportFragmentManager(), "add-sound");
        addSoundDialogFragment.setSoundDialogListener(this);
    }

    @OnClick(R.id.button_image)
    public void imageClick() {
        AddImageDialogFragment addImageDialogFragment = new AddImageDialogFragment();
        addImageDialogFragment.show(getSupportFragmentManager(), "add-image");
        addImageDialogFragment.setSoundDialogListener(this);
    }

    //region 媒体功能
    public void setPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, EditActivity.REQUEST_SETTING_PERMISSION);
    }

    @Override
    public void recordSoundCallBack() {
        recordSound();
    }

    @Override
    public void takePhotoCallBack() {
        takePhoto();
    }

    public void addSoundToData(final String filename) {
        addSoundToGridLayout(filename);
        (new Thread() {
            @Override
            public void run() {
                Sound sound = new Sound();
                sound.path = filename;
                AppDatabase appDatabase = AppDatabase.getInstance();
                if (position == -1) {
                    sound.note_id = appDatabase.noteDAO().getmaxid() + 1;
                } else {
                    sound.note_id = note.id;
                }
                appDatabase.soundDAO().insert(sound);
                sound.note_id = appDatabase.soundDAO().getmaxid();
                soundArrayList.add(sound);
            }
        }).start();
        KeybordUtil.hideKeyboard(this);
    }

    private void takePhoto() {
        File file = Tool.createImage(this);
        Uri photoURI = Tool.getUri(this, file);
        if (photoURI == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        String photoPath = file.getAbsolutePath();
        imagePath = photoPath;
    }

    private void recordSound() {
        RecorderDialogFragment recorderDialogFragment = new RecorderDialogFragment();
        recorderDialogFragment.show(getSupportFragmentManager(), "recorder");
    }

    //region 权限申请结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    (new PermissonDialogFragment()).show(getSupportFragmentManager(), "request-permisson");
                }
                break;
            case PERMISSION_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordSound();
                } else {
                    (new PermissonDialogFragment()).show(getSupportFragmentManager(), "request-permisson");
                }
                break;
        }
    }

    //region Activity回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_DRAW:
                if (resultCode == RESULT_OK) {
                    imagePath = data.getStringExtra(EXTRA_DRAWING_PATH);
                } else {
                    break;
                }
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    addImageToGridLayout(imagePath);
                    (new Thread() {
                        @Override
                        public void run() {
                            final Image image = new Image();
                            image.path = imagePath;
                            AppDatabase appDatabase = AppDatabase.getInstance();
                            if (position == -1) {
                                image.note_id = appDatabase.noteDAO().getmaxid() + 1;
                            } else {
                                image.note_id = note.id;
                            }
                            appDatabase.imageDAO().insert(image);
                            image.id = appDatabase.imageDAO().getmaxid();
                            imageArrayList.add(image);
                        }
                    }).start();
                } else {
                    imagePath = null;
                }
                break;
            case REQUEST_RECORD_SOUND:
                break;
            case REQUEST_SETTING_PERMISSION:
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addSoundToGridLayout(String filepath) {
        TextView textView = new TextView(gridLayout_sound.getContext());
        File file = new File(filepath);
        textView.setText(file.getName());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = gridLayout_sound.getWidth();
        params.height = 100;
        params.rightMargin = 4;
        params.leftMargin = 4;
        params.topMargin = 4;
        params.bottomMargin = 4;
        gridLayout_sound.addView(textView, params);
        KeybordUtil.hideKeyboard(this);
    }

    //region 将媒体添加到控件
    public void addImageToGridLayout(String filepath) {
        Bitmap bitmap, thumbnail;
        ImageView imageView;
        int width = gridLayout_image.getWidth() / 3 - 10;
        int height;
        GridLayout.LayoutParams params;
        bitmap = BitmapFactory.decodeFile(filepath);
        height = bitmap.getWidth() / (bitmap.getWidth() / width);
        thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, height);
        imageView = new ImageView(gridLayout_image.getContext());
        imageView.setImageBitmap(thumbnail);
        params = new GridLayout.LayoutParams();
        params.rightMargin = 4;
        params.leftMargin = 4;
        params.topMargin = 4;
        params.bottomMargin = 4;
        gridLayout_image.addView(imageView, params);
        KeybordUtil.hideKeyboard(this);
    }

    @Override
    public void onBackPressed() {
        KeybordUtil.hideKeyboard(this);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        saveAll();
        finish();
        return true;
        //return super.onSupportNavigateUp();
    }

    @OnClick(R.id.btn_save)
    public void saveClick() {
        saveAll();
        KeybordUtil.hideKeyboard(this);
        finish();
    }

    private void saveAll() {
        EditText editText_title = findViewById(R.id.editText_title);
        EditText editText_body = findViewById(R.id.editText_body);

        note.title = editText_title.getText().toString().trim();
        note.body = editText_body.getText().toString().trim();
        note.maintype = spinner.getSelectedItemPosition();
//        if (subSpinner.getVisibility() == View.INVISIBLE) {
//            note.subtype = -1;
//        } else {
//            note.subtype = subSpinner.getSelectedItemPosition();
//        }
        note.subtype = subSpinner.getSelectedItemPosition();

        getIntent().putExtra(NoteActivity.EXTRA_NOTE_POSITION, position);
        getIntent().putExtra(NoteActivity.EXTRA_NOTE, note);
        getIntent().putExtra(NoteActivity.EXTRA_IMAGE_LIST, imageArrayList);
        getIntent().putExtra(NoteActivity.EXTRA_SOUND_LIST, soundArrayList);
        setResult(RESULT_OK, getIntent());
    }

}