package com.esaygo.app.module.main.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentActivity;

import com.esaygo.app.PdaApplication;
import com.esaygo.app.di.component.DaggerFragmentComponent;
import com.esaygo.app.di.component.FragmentComponent;
import com.esaygo.app.di.module.FragmentModule;
import com.gyf.immersionbar.components.ImmersionFragment;
import com.gyf.immersionbar.components.ImmersionOwner;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.leefeng.promptlibrary.PromptDialog;

public abstract class ImBaseFragment extends ImmersionFragment implements ImmersionOwner {

  //  @Inject

    protected View mRootView;
    protected Activity mActivity;
    protected LayoutInflater inflater;
    protected Context mContext;
    // 标志位 标志已经初始化完成。
    protected boolean isPrepared;
    //标志位 fragment是否可见
    protected boolean isVisible;
    private Unbinder mUnbinder;
    public View mError;
    public Bundle mBundle;
    protected Handler mHandler;
    protected PromptDialog promptDialog;


    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        } else {
            mRootView = inflater.inflate(getLayoutId(), container, false);
            mActivity = (Activity) getSupportActivity();
            mContext = mActivity;
            this.inflater = inflater;
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        initInject();
        initVariables();
        mBundle = getArguments();
        initBundle(mBundle);
        mHandler = new Handler();
        promptDialog=new PromptDialog(mActivity);
        //     mError = ButterKnife.findById(mRootView, R.id.cl_error);
        initWidget();
        finishCreateView(savedInstanceState);
        initDatas();
    }

    protected void lazyLoadData() {

    }

    /**
     * 初始化bundle
     *
     * @param bundle
     */
    protected void initBundle(Bundle bundle) {
    }

    protected void initDatas() {
        loadData();
    }

    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    /**
     * 分离
     */
    @Override
    public void onDetach() {
        this.mActivity = null;
        super.onDetach();
    }


    /**
     * 初始化RV
     */
    protected void initRecyclerView() {


    }

    /**
     * 初始化刷新
     */
    @SuppressLint("CheckResult")
    protected void initRefreshLayout() {

    }

    /**
     * 清除数据
     */
    protected void clearData() {

    }


    /**
     * 初始化变量
     */
    public void initVariables() {
    }

    /**
     * 懒加载
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) return;
        lazyLoadData();
        isPrepared = false;
    }


    /**
     * 加载数据
     */
    protected void loadData() {
    }


    protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(PdaApplication.getInstance().getAppComponent())
                .fragmentModule(getFragmentModule())
                .build();
    }

    protected FragmentModule getFragmentModule() {
        return new FragmentModule(this);
    }


    /**
     * 注入dagger2依赖
     */
    protected void initInject() {

    }

    /**
     * 显示错误信息
     *
     * @param msg msg
     */
    public void showError(String msg) {
        if (mError != null) {
            visible(mError);
        }
    }

    /**
     * 完成加载
     */
    public void complete() {
        if (mError != null) {
            gone(mError);
        }
    }

    protected void finishTask() {

    }

    /**
     * 布局
     *
     * @return int
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public void initWidget() {

    }

    public void onVisible() {
        lazyLoad();
    }

    /**
     * Fragment数据的懒加载
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 获取Activity
     *
     * @return FragmentActivity
     */
    public FragmentActivity getSupportActivity() {
        return (FragmentActivity) super.getActivity();
    }

    /**
     * 获取ApplicationContext 信息
     *
     * @return Context
     */
    public Context getApplicationContext() {
        return this.mContext == null ? (getActivity() == null ? null : getActivity()
                .getApplicationContext()) : this.mContext.getApplicationContext();
    }


    /**
     * 隐藏View
     *
     * @param views view数组
     */
    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示View
     *
     * @param views view数组
     */
    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    /**
     * 隐藏View
     *
     * @param id
     */
    protected void gone(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    gone(view);
            }
        }

    }

    /**
     * 显示View
     *
     * @param id
     */
    protected void visible(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    visible(view);
            }
        }
    }

    public View $(@IdRes int id) {
        View view;
        if (mRootView != null) {
            view = mRootView.findViewById(id);
            return view;
        }
        return null;
    }


}



