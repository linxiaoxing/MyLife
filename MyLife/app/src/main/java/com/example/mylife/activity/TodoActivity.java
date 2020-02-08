package com.example.mylife.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SPUtils;
import com.example.mylife.R;
import com.example.mylife.activity.aboutme.AboutActivity;
import com.example.mylife.activity.login.LoginActivity;
import com.example.mylife.base.BaseActivity;
import com.example.mylife.base.BaseFragment;
import com.example.mylife.fragment.done.DoneFragment;
import com.example.mylife.fragment.undo.UndoneFragment;
import com.example.note.activity.NoteActivity;
import com.example.ocr.module.CharActivity;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Route(path = "/activity/MainActivity")
public class TodoActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    private List<BaseFragment> mFragments = new ArrayList<>();

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    /**
     * 上一次的Fragment实例的索引
     */
    private int mLastFgIndex = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initView() {
        mNavigation.setOnNavigationItemSelectedListener(this);
        getPersimmions();
        initFragment();
        switchFragment(0);
    }

    private void initFragment() {
        mFragments.add(UndoneFragment.getInstance(0));
        mFragments.add(DoneFragment.getInstance(1));
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
                                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(TodoActivity.this));
                        cookieJar.clear();
                        SPUtils.getInstance().put("study", false);
                        startActivity(new Intent(TodoActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                builder.show();
                break;
            case R.id.menuMe:
                startActivity(new Intent(TodoActivity.this, AboutActivity.class));
                break;
            case R.id.menuNote:
                startActivity(new Intent(TodoActivity.this, NoteActivity.class));
                break;
            case R.id.menuChar:
                startActivity(new Intent(TodoActivity.this, CharActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }


    //    @RequiresApi(api = Build.VERSION_CODES.M)
    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                mToolbar.setTitle(R.string.app_name);
                mToolbar.setVisibility( View.VISIBLE);
                switchFragment(0);
                break;
            case R.id.navigation_knowledgesystem:
                mToolbar.setTitle(R.string.no_complete);
                mToolbar.setVisibility(View.VISIBLE);
                switchFragment(1);
                break;
        }
        return true;
    }

    private void switchFragment(int position) {
        if (position >= mFragments.size()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BaseFragment targetFg = mFragments.get(position);

        Slide slideTransition;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Gravity.START部分机型崩溃java.lang.IllegalArgumentException: Invalid slide direction
            slideTransition = new Slide( Gravity.BOTTOM);
            slideTransition.setDuration(500);
            targetFg.setEnterTransition(slideTransition);
            targetFg.setExitTransition(slideTransition);
        }

        if (mLastFgIndex != -1) {
            Fragment lastFg = mFragments.get(mLastFgIndex);
            mLastFgIndex = position;
            ft.hide(lastFg);
        } else {
            mLastFgIndex = position;
        }
        if (!targetFg.isAdded()) {
            ft.add(R.id.layout_fragment, targetFg);
        }
        ft.show(targetFg);
        ft.commitAllowingStateLoss();
    }
}