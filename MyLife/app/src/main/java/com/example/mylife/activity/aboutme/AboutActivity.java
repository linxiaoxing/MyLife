package com.example.mylife.activity.aboutme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.mylife.R;
import com.example.mylife.base.BaseSwipeBackActivity;
import com.example.mylife.widget.SwipeBackLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于界面
 */
public class AboutActivity extends BaseSwipeBackActivity{

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_about);
        ButterKnife.bind(this);
        setDragEdge( SwipeBackLayout.DragEdge.LEFT);
        initView();
    }

    //初始化视图控件
    private void initView() {
        tvVersion.setText("Version " +"1.0.0");
        collapsingToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.onBackPressed();
            }
        });
    }

}