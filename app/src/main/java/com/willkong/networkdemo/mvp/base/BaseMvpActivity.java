package com.willkong.networkdemo.mvp.base;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.willkong.networkdemo.application.MyApplication;
import com.willkong.networkdemo.dragger2.component.AppComponent;
import com.willkong.networkdemo.eventbus.BaseEventbusBean;
import com.willkong.networkdemo.mvp.utils.DialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 12:36
 * @Description: BaseMvpActivity框架基类
 */
public abstract class BaseMvpActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {
    protected boolean regEvent;
    private Unbinder unbinder;
    protected P mPresenter;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        setupActivityComponent(MyApplication.instance().getAppComponent());
        initEventBus();
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachMvpView((V) this);
        loadingDialog = DialogUtils.createLoadingDialog(this, "加载中...");
    }
    /**
     * 获取布局ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    protected abstract P createPresenter();
    /**
     * 依赖注入
     *
     * @param appComponent
     */
    protected abstract void setupActivityComponent(AppComponent appComponent);
    /**
     * 初始化EventBus
     */
    private void initEventBus() {
        if (regEvent) {
            EventBus.getDefault().register(this);
        }
    }
    @Subscribe
    public void onMessageEvent(BaseEventbusBean event) {
        onEvent(event);
    }

    protected void onEvent(BaseEventbusBean event) {

    }
    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachMvpView();
        }
        if (regEvent) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void showLoading() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
