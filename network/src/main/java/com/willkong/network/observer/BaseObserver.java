package com.willkong.network.observer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.observer
 * @Author: willkong
 * @CreateDate: 2020/5/13 11:25
 * @Description: Observer封装
 */
public abstract class BaseObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

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

    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(Throwable e);
}
