package com.willkong.network.commoninterceptor;

import com.willkong.network.INetworkRequiredInfo;
import com.willkong.network.utils.TecentUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.commoninterceptor
 * @Author: willkong
 * @CreateDate: 2020/5/13 10:29
 * @Description: 请求拦截器
 */
public class CommonRequestInterceptor implements Interceptor {
    private INetworkRequiredInfo requiredInfo;

    public CommonRequestInterceptor(INetworkRequiredInfo requiredInfo) {
        this.requiredInfo = requiredInfo;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String timeStr = TecentUtil.getTimeStr();
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("os", "android");
        builder.addHeader("appVersion", this.requiredInfo.getAppVersionCode());
        builder.addHeader("Authorization", "Authorization");
        builder.addHeader("Date", timeStr);
        return chain.proceed(builder.build());
    }
}
