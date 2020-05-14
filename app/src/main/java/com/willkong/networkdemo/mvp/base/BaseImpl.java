package com.willkong.networkdemo.mvp.base;

import java.lang.reflect.ParameterizedType;

import retrofit2.Retrofit;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 12:51
 * @Description: 初始化网络api基类
 */
public class BaseImpl<Service> {
    protected Service mService;
    public BaseImpl(Service mService) {
        this.mService = mService;
    }

    @SuppressWarnings("unchecked")
    private Class<Service> getServiceClass() {
        return (Class<Service>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
