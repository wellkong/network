package com.willkong.networkdemo.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.willkong.network.NetworkApi;
import com.willkong.networkdemo.api.NetworkRequiredInfo;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo
 * @Author: willkong
 * @CreateDate: 2020/5/13 10:10
 * @Description: java类作用描述
 */
public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkApi.init(new NetworkRequiredInfo(MyApplication.this));
    }
    public static Context getContext() {
        return context;
    }
}
