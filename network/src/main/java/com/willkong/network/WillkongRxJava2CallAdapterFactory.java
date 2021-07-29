package com.willkong.network;

import com.willkong.network.errorhandler.HttpErrorHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network
 * @ClassName: WillkongRxJava2CallAdapterFactory
 * @Author: willkong
 * @Description: rxjava转换类
 * @CreateDate: 2021/7/28 23:59
 */
public class WillkongRxJava2CallAdapterFactory extends CallAdapter.Factory{
    Function function;
    private CallAdapter.Factory mFactory;

    public static WillkongRxJava2CallAdapterFactory create(Function function){
        return new WillkongRxJava2CallAdapterFactory(function);
    }
    private WillkongRxJava2CallAdapterFactory(Function function){
        this.function = function;
        mFactory = RxJava2CallAdapterFactory.create();
    }
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        final CallAdapter<?,?> callAdapter = mFactory.get(returnType,annotations,retrofit);
        Class<?> rawType = getRawType(returnType);
        if (callAdapter!=null){
            //生产不同的CallAdapter
            if (rawType == Observable.class){
                return CallAdapterFactory.create((CallAdapter<Observable<?>,Observable<?>>)callAdapter,f->{
                    Observable<?> observable = f.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                    if (function!=null){
                        return observable.map(function).onErrorResumeNext(new HttpErrorHandler());
                    }else {
                        return observable.onErrorResumeNext(new HttpErrorHandler());
                    }
                });
            }
        }
        if (callAdapter!=null){
            //生产不同的CallAdapter
            if (rawType == Flowable.class){
                return CallAdapterFactory.create((CallAdapter<Flowable<?>,Flowable<?>>)callAdapter,f ->{
                    Flowable<?> observable = f.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                    if (function!=null){
                        return observable.map(function).onErrorResumeNext(new HttpErrorHandler());
                    }else {
                        return observable.onErrorResumeNext(new HttpErrorHandler());
                    }
                });
            }
        }
        return callAdapter;
    }
}
