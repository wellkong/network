package com.willkong.networkdemo.api;

import com.willkong.network.NetworkApi;
import com.willkong.network.beans.BaseResponse;
import com.willkong.network.errorhandler.ExceptionHandler;
import com.willkong.network.utils.TecentUtil;

import java.io.IOException;

import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.api
 * @Author: willkong
 * @CreateDate: 2020/5/13 15:17
 * @Description: 我的网络请求api
 */
public class AppNetworkApi extends NetworkApi {
    private static final String BASE_URL = "http://travel.gaciov.com:9809/";
    private static volatile AppNetworkApi sInstance;

    public static AppNetworkApi getInstance() {
        if (sInstance == null) {
            synchronized (AppNetworkApi.class) {
                if (sInstance == null) {
                    sInstance = new AppNetworkApi();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取服务对象
     *
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T getService(Class<T> service) {
        return getInstance().getRetrofit(service).create(service);
    }

    @Override
    protected Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String timeStr = TecentUtil.getTimeStr();
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("os", "android");
                builder.addHeader("Authorization", "Authorization");
                builder.addHeader("Date", timeStr);
                return chain.proceed(builder.build());
            }
        };
    }

    /**
     * 处理服务错误
     *
     * @param <T>
     * @return
     */
    @Override
    protected <T> Function<T, T> getAppErrorHandler() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                //response中code码不为0 出现错误
                if (response instanceof BaseResponse && ((BaseResponse) response).code != 0) {
                    ExceptionHandler.ServerException exception = new ExceptionHandler.ServerException();
                    exception.code = ((BaseResponse) response).code;
                    exception.message = ((BaseResponse) response).message != null ? ((BaseResponse) response).message : "";
                    throw exception;
                }
                return response;
            }
        };
    }

    @Override
    public String getFormal() {
        return BASE_URL;
    }

    @Override
    public String getTest() {
        return "http:tyu";
    }
}
