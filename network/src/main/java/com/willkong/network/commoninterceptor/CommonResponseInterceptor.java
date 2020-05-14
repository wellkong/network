package com.willkong.network.commoninterceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.commoninterceptor
 * @Author: willkong
 * @CreateDate: 2020/5/13 10:30
 * @Description: 请求响应拦截器
 */
public class CommonResponseInterceptor implements Interceptor {
    public static final String TAG = "ResponseInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        long requestTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        Log.d(TAG, "requestTime=" + (System.currentTimeMillis() - requestTime));
        return response;
    }
}
