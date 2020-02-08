package com.example.note.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mylife.R;
import com.example.mylife.base.BaseActivity;
import com.example.note.dialogFragment.ColorSelectorDialogFragment;
import com.example.note.dialogFragment.WidthSelectorDialogFragment;
import com.example.note.utils.Tool;
import com.example.note.view.DrawView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;

public class DrawActivity extends BaseActivity{

    @BindView(R.id.view_drawing)
    DrawView drawView;
    @BindView(R.id.radioButton_erase)
    RadioButton radioButton_erase;
    @BindView(R.id.radioButton_pen)
    RadioButton radioButton_pen;
    @BindView(R.id.radioGroup_drawing)
    RadioGroup radioGroup_drawing;
    @BindView(R.id.imageButton_color)
    ImageButton imageButton_color;
    @BindView(R.id.imageButton_width)
    ImageButton imageButton_width;

    ColorSelectorDialogFragment colorSelectorDialogFragment;
    WidthSelectorDialogFragment widthSelectorDialogFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draw;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initView() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        radioButton_pen.setForeground(getDrawable(R.drawable.shape_card));
        radioButton_pen.setChecked(true);

        radioButton_pen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imageButton_color.setEnabled(true);
                    drawView.setPaintColor(imageButton_color.getImageTintList().getDefaultColor());
                    drawView.setPaintClear(false);
                    radioButton_pen.setForeground(getDrawable(R.drawable.shape_card));
                    radioButton_erase.setForeground(null);

                }
            }
        });
        radioButton_erase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imageButton_color.setEnabled(false);
                    drawView.setPaintColor( Color.WHITE);
                    drawView.setPaintClear(true);
                    radioButton_pen.setForeground(null);
                    radioButton_erase.setForeground(getDrawable(R.drawable.shape_card));
                }
            }
        });
        imageButton_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSelectorDialogFragment = new ColorSelectorDialogFragment();
                colorSelectorDialogFragment.show(getSupportFragmentManager(), "color-selector");
            }
        });
        imageButton_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widthSelectorDialogFragment = new WidthSelectorDialogFragment();
                widthSelectorDialogFragment.show(getSupportFragmentManager(), "line-selector");
            }
        });
    }

    public void onSelectColor(View view) {
        imageButton_color.setImageTintList(view.getBackgroundTintList());
        drawView.setPaintColor(view.getBackgroundTintList().getDefaultColor());
        colorSelectorDialogFragment.dismiss();
    }

    public void onSelectWidth(View view) {
        switch (view.getId()) {
            case R.id.imageButton_width_1:
                drawView.setPaintWidth(10);
                imageButton_width.setImageResource(R.drawable.shape_line_1);
                break;
            case R.id.imageButton_width_2:
                drawView.setPaintWidth(20);
                imageButton_width.setImageResource(R.drawable.shape_line_2);
                break;
            case R.id.imageButton_width_3:
                drawView.setPaintWidth(30);
                imageButton_width.setImageResource(R.drawable.shape_line_3);
                break;
            default:
                break;
        }
        widthSelectorDialogFragment.dismiss();
    }

    @Override
    public void onBackPressed() {
        saveDrawing();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        saveDrawing();
        finish();
        return true;
    }

    private void saveDrawing() {
        Bitmap bitmap = drawView.getDrawing();
        if (bitmap == null) {
            Toast.makeText(this, "已舍弃空白绘图", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            return;
        }
        File file = Tool.createFile(this, "PNG", ".png", "drawing");
        if (file == null) {
            setResult(RESULT_CANCELED);
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            Intent intent = new Intent();
            intent.putExtra(EditActivity.EXTRA_DRAWING_PATH, file.getAbsolutePath());
            setResult(RESULT_OK, intent);
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            return;
        }
    }
}
