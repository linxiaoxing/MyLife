package com.example.mylife.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.mylife.R;
import com.example.mylife.constant.Constant;
import com.example.mylife.constant.LoadType;
import com.example.mylife.di.component.DaggerFragmentComponent;
import com.example.mylife.di.component.FragmentComponent;
import com.example.mylife.di.module.FragmentModule;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<T extends BaseContract.BasePresenter> extends RxFragment implements BaseContract.BaseView {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    @Nullable
    @Inject
    protected T mPresenter;
    protected FragmentComponent mFragmentComponent;
    private Unbinder unbinder;
    private View mRootView, mErrorView, mEmptyView;

    protected abstract int getLayoutId();

    protected abstract void initInjector();

    protected abstract void initView(View view);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragmentComponent();
        ARouter.getInstance().inject(this);
        initInjector();
        attachView();
        if (!NetworkUtils.isConnected()) showNoNet();
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    /**
     * 初始化FragmentComponent
     */
    private void initFragmentComponent() {
        mFragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(((App) getActivity().getApplication()).getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflaterView(inflater, container);
        unbinder = ButterKnife.bind(this, mRootView);
        initView(mRootView);
        return mRootView;
    }

    /**
     * 设置View
     *
     * @param inflater
     * @param container
     */
    private void inflaterView(LayoutInflater inflater, @Nullable ViewGroup container) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        detachView();
    }


    /**
     * 贴上view
     */
    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    /**
     * 分离view
     */
    private void detachView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showLoading() {
        ToastUtils.showShort("showLoading");
    }

    @Override
    public void hideLoading() {
        ToastUtils.showShort("hideLoading");
    }

    @Override
    public void showSuccess(String successMsg) {
        ToastUtils.showShort(successMsg);
    }

    @Override
    public void showFaild(String errorMsg) {
        ToastUtils.showShort(errorMsg);
    }

    @Override
    public void showNoNet() {
        ToastUtils.showShort(R.string.no_network_connection);
    }

    @Override
    public void onRetry() {
        ToastUtils.showShort("onRetry");
    }

    /**
     * 设置加载数据结果
     *
     * @param baseQuickAdapter
     * @param refreshLayout
     * @param list
     * @param loadType
     */
    protected void setLoadDataResult(BaseQuickAdapter baseQuickAdapter, SwipeRefreshLayout refreshLayout, List list, @LoadType.checker int loadType) {

        baseQuickAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        switch (loadType) {
            case LoadType.TYPE_REFRESH_SUCCESS:
                baseQuickAdapter.setNewData(list);
                refreshLayout.setRefreshing(false);
                ToastUtils.showShort(R.string.refresh_success);
                break;
            case LoadType.TYPE_REFRESH_ERROR:
                refreshLayout.setRefreshing(false);
                ToastUtils.showShort(R.string.refresh_error);
                break;
            case LoadType.TYPE_LOAD_MORE_SUCCESS:
                if (list != null) baseQuickAdapter.addData(list);
                ToastUtils.showShort(R.string.load_more_success);
                break;
            case LoadType.TYPE_LOAD_MORE_ERROR:
                baseQuickAdapter.loadMoreFail();
                ToastUtils.showShort(R.string.load_more_error);
                break;
        }
        if (list == null || list.isEmpty() || list.size() < Constant.PAGE_SIZE) {
            baseQuickAdapter.loadMoreEnd(false);
        } else {
            baseQuickAdapter.loadMoreComplete();
        }
    }

    @Override
    public void jumpToLogin() {
        ARouter.getInstance().build("/activity/LoginActivity")
                .navigation();
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindToLifecycle();
    }

}
