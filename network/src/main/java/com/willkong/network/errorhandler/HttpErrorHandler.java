package com.willkong.network.errorhandler;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.errorhandler
 * @Author: willkong
 * @CreateDate: 2020/5/13 14:16
 * @Description:
 * HttpErrorHandler处理以下两类网络错误：
 * 1、http请求相关的错误，例如：404,403，socket timeout等等；
 * 2、应用数据的错误会抛RunTimeException,最后也会走到这个函数统一处理；
 */
public class HttpErrorHandler<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        return Observable.error(ExceptionHandler.handlerException(throwable));
    }
}
