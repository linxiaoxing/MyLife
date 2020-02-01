package com.example.mylife.activity.login;


import com.example.mylife.base.BasePresenter;
import com.example.mylife.bean.DataResponse;
import com.example.mylife.bean.User;
import com.example.mylife.net.ApiService;
import com.example.mylife.net.RetrofitManager;
import com.example.mylife.utils.RxSchedulers;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

/**
 * Created by OnexZgj on 2018/9/11:09:37.
 * des:
 */

public class LoginActivityImp extends BasePresenter<LoginActivityContract.View> implements LoginActivityContract.Presenter {

    @Inject
    public LoginActivityImp() {

    }

    @Override
    public void Login(String account, String password) {

        mView.showLoading();

        RetrofitManager.create( ApiService.class).login(account,password).compose(mView.<DataResponse<User>>bindToLife())
                .compose( RxSchedulers.<DataResponse<User>>applySchedulers())
                .subscribe(new Consumer<DataResponse<User>>() {
                    @Override
                    public void accept(DataResponse<User> userDataResponse) throws Exception {
                        if (userDataResponse.getErrorCode()!=0){
                            //表示出错
                            mView.showFaild(userDataResponse.getErrorMsg().toString());
                        }else{
                            mView.showLoginSuccess();
                        }

                        mView.hideLoading();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.showFaild("请检查网络,稍后重试!");
                    }
                });
    }
}
