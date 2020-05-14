package com.willkong.network.commoninterceptor;

import android.content.Context;

import com.willkong.network.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.commoninterceptor
 * @Author: willkong
 * @CreateDate: 2020/5/13 15:48
 * @Description: 缓存数据拦截器
 */
public class CachInterceptor implements Interceptor {
    private static final String CACHE_NAME = "retrofitcache";
    private Context mContext;

    public CachInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //网络不可用
        if (!NetworkUtils.checkedNetWork(mContext.getApplicationContext())) {
            //在请求头中加入：强制使用缓存，不访问网络
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = chain.proceed(request);
        //网络可用
        if (NetworkUtils.checkedNetWork(mContext.getApplicationContext())) {
            int maxAge = 0;
            // 有网络时 在响应头中加入：设置缓存超时时间0个小时
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("pragma")
                    .build();
        } else {
            // 无网络时，在响应头中加入：设置超时为4周
            int maxStale = 60 * 60 * 24 * 28;
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("pragma")
                    .build();
        }
        return response;
    }
}
