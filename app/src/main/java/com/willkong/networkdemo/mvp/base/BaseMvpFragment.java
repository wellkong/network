package com.willkong.networkdemo.mvp.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
 * @CreateDate: 2020/5/14 12:47
 * @Description: Fragment基类
 */
public abstract class BaseMvpFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView {
    protected boolean regEvent;
    private Unbinder unBinder;
    public View view;
    private OnFragmentInteractionListener mListener;
    protected P mPresenter;
    private Dialog loadingDialog;

    protected abstract P createPresenter();

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutId(), container, false);
        unBinder = ButterKnife.bind(this, view);
        initEventBus();
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachMvpView((V) this);
        loadingDialog = DialogUtils.createLoadingDialog(getActivity(), "加载中...");
        return view;
    }
    /**
     * 获取布局ID
     *
     * @return
     */
    protected abstract int getLayoutId();
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
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachMvpView();
        }
        if (regEvent) {
            EventBus.getDefault().unregister(this);
        }
    }
}
