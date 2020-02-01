package com.example.mylife.fragment.undo;


import com.example.mylife.base.BasePresenter;
import com.example.mylife.bean.DataResponse;
import com.example.mylife.bean.TodoTaskDetail;
import com.example.mylife.constant.LoadType;
import com.example.mylife.net.ApiService;
import com.example.mylife.net.RetrofitManager;
import com.example.mylife.utils.RxSchedulers;
import com.orhanobut.logger.Logger;


import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by OnexZgj on 2018/4/12:22:43.
 * des:
 */

public class UndonePresenterImp extends BasePresenter<UndoneContract.View> implements UndoneContract.Presenter {


    private int mType = 0;
    private int mIndexPage = 1;
    private boolean mIsRefresh = true;

    @Inject
    public UndonePresenterImp() {
    }

    @Override
    public void getUndoneTask(int type) {

        this.mType = type;
        mView.showLoading();

        ApiService apiService = RetrofitManager.create(ApiService.class);
        Observable<DataResponse<TodoTaskDetail>> observableUndoneTaskData = apiService.getNotodoList(type, mIndexPage);

        observableUndoneTaskData.compose(mView.<DataResponse<TodoTaskDetail>>bindToLife())
                .compose( RxSchedulers.<DataResponse<TodoTaskDetail>>applySchedulers())
                .subscribe(new Consumer<DataResponse<TodoTaskDetail>>() {
                    @Override
                    public void accept(DataResponse<TodoTaskDetail> data) throws Exception {

                        if (data.getErrorCode() == 0) {

                            int loadType = mIsRefresh ? LoadType.TYPE_REFRESH_SUCCESS : LoadType.TYPE_LOAD_MORE_SUCCESS;
                            mView.showUndoneTask(data.getData(), loadType);
                        } else {

                            mView.showFaild(data.getErrorMsg());

                        }
                        mView.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFaild("请求网络错误!");
                        mView.hideLoading();
                    }
                });


//        Observable.zip(observableLogin, observableUndoneTaskData, new BiFunction<DataResponse<User>, DataResponse<TodoTaskDetail>, Map<String, Object>>() {
//            @Override
//            public Map<String, Object> apply(DataResponse<User> userDataResponse, DataResponse<TodoTaskDetail> dataResponse) throws Exception {
//                Map<String, Object> objMap = new HashMap<>();
//                objMap.put(Constant.UNDONE_DATA, dataResponse.getData());
//                objMap.put(Constant.USER_KEY, userDataResponse.getData());
//                return objMap;
//            }
//        }).compose(RxSchedulers.<Map<String,Object>>applySchedulers())
//                .compose(mView.<Map<String,Object>>bindToLife())
//                .subscribe(new Consumer<Map<String, Object>>() {
//                    @Override
//                    public void accept(Map<String, Object> data) throws Exception {
//
//                        int loadType = mIsRefresh ? LoadType.TYPE_REFRESH_SUCCESS : LoadType.TYPE_LOAD_MORE_SUCCESS;
//
//                        mView.showUndoneTask((TodoTaskDetail) data.get(Constant.UNDONE_DATA), loadType);
//                        mView.hideLoading();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        mView.showFaild("请求网络错误!");
//                        mView.hideLoading();
//                    }
//                });
    }

    @Override
    public void refresh() {
        mIndexPage = 1;
        mIsRefresh = true;
        getUndoneTask(mType);
    }

    @Override
    public void deleteTodo(int id) {
        mView.showLoading();
        RetrofitManager.create(ApiService.class).deleteTodo(id).compose(RxSchedulers.<DataResponse>applySchedulers()).compose(mView.<DataResponse>bindToLife())
                .subscribe(new Consumer<DataResponse>() {
                    @Override
                    public void accept(DataResponse s) throws Exception {
                        mView.showDeleteSuccess("删除成功!");
                        mView.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showDeleteSuccess("删除失败,请重试...!");
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void updataStatus(int id) {
        mView.showLoading();

        RetrofitManager.create(ApiService.class).updateStateTodo(id, 1)
                .compose(RxSchedulers.<DataResponse>applySchedulers())
                .compose(mView.<DataResponse>bindToLife())
                .subscribe(new Consumer<DataResponse>() {
                    @Override
                    public void accept(DataResponse s) throws Exception {
                        mView.showMarkComplete("标记为已完成!");
                        mView.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showMarkComplete("标记完成失败，请重试!");
                        Logger.d("标志更新的异常信息" + throwable.getMessage().toString());
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void loadMore() {
        mIndexPage++;
        mIsRefresh = false;
        getUndoneTask(mType);
    }

}
