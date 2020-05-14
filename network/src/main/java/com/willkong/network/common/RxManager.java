package com.willkong.network.common;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.common
 * @Author: willkong
 * @CreateDate: 2020/5/13 10:29
 * @Description: 订阅者管理 防止内存泄漏
 */
public class RxManager {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();// 管理订阅者者

    public void register(Disposable d) {
        mCompositeDisposable.add(d);
    }

    public void unSubscribe() {
        mCompositeDisposable.dispose();// 取消订阅
    }
}