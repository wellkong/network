package com.willkong.network.observer;

import android.app.Dialog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.observer
 * @Author: willkong
 * @CreateDate: 2020/5/13 11:25
 * @Description: Observer封装订阅 弹框监听取消请求
 */
public abstract class DialogObserver<T> implements Observer<T>, DialogCancelListener {
    private boolean isShowDialog;
    private DialogHandler dialogHandler;
    private Disposable disposable;

    protected DialogObserver(Dialog dialog) {
        isShowDialog = dialog != null;
        dialogHandler = new DialogHandler(dialog, this);
    }
    /**
     * 显示对话框 发送一个显示对话框的消息给dialoghandler  由他自己处理（也就是dialog中hanldermesage处理该消息）
     */
    private void showProgressDialog() {
        if (dialogHandler != null) {
            dialogHandler.obtainMessage(DialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    /**
     * 隐藏对话框 ....
     */
    private void dismissProgressDialog() {
        if (dialogHandler != null) {
            dialogHandler.obtainMessage(DialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            dialogHandler = null;
        }
    }

    /**
     * 请求开始
     * 先判断isShowDialog的值，如果为false就不显示对话框，为true才显示
     */
    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if(isShowDialog){
            showProgressDialog();
        }
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {
        dismissProgressDialog();
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(Throwable e);

    @Override
    public void onCancel() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
