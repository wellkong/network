package com.willkong.networkdemo.mvp.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.willkong.networkdemo.application.MyApplication;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 11:29
 * @Description: mvp的Presenter基类 引用基类view
 */
public class BasePresenter<V extends BaseView> {
    /**
     * v层泛型引用
     */
    protected V mView;

    private WeakReference<V> weakReferenceView;

    public void attachMvpView(V view) {
        weakReferenceView = new WeakReference<>(view);
        this.mView = weakReferenceView.get();
    }


    public void detachMvpView() {
        weakReferenceView.clear();
        weakReferenceView = null;
        mView = null;
    }


    /**
     * Retrofit的Response包裹
     * Observable<T> observable  可以再加一层包裹变为Observable<Response<T>> observable这样回调中就无需咋强转成Response
     * <p>
     * RequestCallback<T> requestCallback 可以再加一层包裹变为RequestCallback<Response<T>> requestCallback
     * <p>
     * 一般情况 要么都包裹要么都不包裹
     */
    protected <T> void subscribe(final Observable<T> observable, final RequestCallback<T> requestCallback) {
        if (checkedNetWork(MyApplication.instance())) {
            observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<T>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(T t) {
                            requestCallback.onSuccess(t);
                        }

                        @Override
                        public void onError(Throwable e) {
                            requestCallback.onFailure(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            requestCallback.onFailure("无网络链接,请检查您的网络，");
        }
    }

    /**
     * 检查是否连接网络
     *
     * @param context
     * @return
     */
    public static boolean checkedNetWork(Context context) {
        // 1.获得连接设备管理器
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        /**
         * 获取网络连接对象
         */
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return !(networkInfo == null || !networkInfo.isAvailable());
    }
}
