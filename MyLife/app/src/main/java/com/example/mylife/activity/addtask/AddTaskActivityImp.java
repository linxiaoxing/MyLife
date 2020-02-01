package com.example.mylife.activity.addtask;

import com.example.mylife.base.BasePresenter;
import com.example.mylife.bean.DataResponse;
import com.example.mylife.bean.TodoTaskDetail;
import com.example.mylife.net.ApiService;
import com.example.mylife.net.RetrofitManager;
import com.example.mylife.utils.RxSchedulers;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class AddTaskActivityImp extends BasePresenter<AddTaskActivityContract.View> implements AddTaskActivityContract.Presenter {

    @Inject
    public AddTaskActivityImp() {

    }


    @Override
    public void addTask(String title, String content, String date, String state) {
        RetrofitManager.create( ApiService.class).addTask(title,content,date,state)
                .compose( RxSchedulers.<DataResponse<TodoTaskDetail.DatasBean>>applySchedulers())
                .compose(mView.<DataResponse<TodoTaskDetail.DatasBean>>bindToLife())
                .subscribe(new Consumer<DataResponse<TodoTaskDetail.DatasBean>>() {
                    @Override
                    public void accept(DataResponse<TodoTaskDetail.DatasBean> dataResponse) throws Exception {

                        if (dataResponse.getErrorCode() == 0) {
                            mView.showAddTaskSuccess(dataResponse.getData());

                        }else{
                            mView.showFaild(dataResponse.getErrorMsg());
                        }
                        mView.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFaild("添加待办失败,请重试...");
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void updateTask(int id,String title, String content, String date, int state,int type) {
        RetrofitManager.create(ApiService.class).updateTodo(id,title,content,date,state,type)
                .compose(mView.<DataResponse<TodoTaskDetail.DatasBean>>bindToLife())
                .compose(RxSchedulers.<DataResponse<TodoTaskDetail.DatasBean>>applySchedulers())
                .subscribe(new Consumer<DataResponse<TodoTaskDetail.DatasBean>>() {
                    @Override
                    public void accept(DataResponse<TodoTaskDetail.DatasBean> dataResponse) throws Exception {
                        if (dataResponse.getErrorCode()==0){
                            mView.showUpdateSuccess(dataResponse.getData());
                        }else {
                            mView.showFaild(dataResponse.getErrorMsg());
                        }
                        mView.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFaild("更新失败,请重试...");
                        mView.hideLoading();
                    }
                });
    }
}