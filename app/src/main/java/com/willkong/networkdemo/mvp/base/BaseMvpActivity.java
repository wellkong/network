package com.willkong.networkdemo.mvp.base;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.willkong.networkdemo.mvp.utils.DialogUtils;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 12:36
 * @Description: BaseMvpActivity框架基类
 */
public abstract class BaseMvpActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {
    protected P mPresenter;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachMvpView((V) this);
        loadingDialog = DialogUtils.createLoadingDialog(this, "加载中...");
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachMvpView();
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
