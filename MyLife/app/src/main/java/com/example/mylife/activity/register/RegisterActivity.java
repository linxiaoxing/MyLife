package com.example.mylife.activity.register;

import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.example.mylife.R;
import com.example.mylife.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/activity/RegisterActivity")
public class RegisterActivity extends BaseActivity<RegisterActivityImp> implements RegisterActivityContract.View {

    @BindView(R.id.account)
    TextInputEditText account;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.repassword)
    TextInputEditText repassword;
    @BindView(R.id.regitster)
    Button regitster;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initInjector() {
        ARouter.getInstance().inject(this);
        mActivityComponent.inject(this);
    }

    @Override
    protected void initView() {

    }


    @Override
    public void showRegisterSuccess() {
        showSuccess("注册成功,重新登录!");

        SPUtils.getInstance().put("account",account.getText().toString().trim());

        //跳转到登录页面
        ARouter.getInstance().build("/activity/LoginActivity")
                .navigation();
    }


    @OnClick(R.id.regitster)
    public void onViewClicked() {
        mPresenter.register(account.getText().toString().trim(),password.getText().toString().trim(),repassword.getText().toString().trim());
    }
}
