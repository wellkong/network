package com.willkong.networkdemo.application;

import android.app.Application;

import com.willkong.netstatusbus.NetWorkMonitorManager;
import com.willkong.network.NetworkApi;
import com.willkong.networkdemo.api.NetworkRequiredInfo;
import com.willkong.networkdemo.dragger2.component.AppComponent;
import com.willkong.networkdemo.dragger2.component.DaggerAppComponent;
import com.willkong.networkdemo.dragger2.module.AppModule;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo
 * @Author: willkong
 * @CreateDate: 2020/5/13 10:10
 * @Description: java类作用描述
 */
public class MyApplication extends Application {
    //全局单例 注入器 并给其他注入器提供依赖
    private AppComponent appComponent;

    public static MyApplication mInstance;

    public static MyApplication instance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initAppComponent();
        NetworkApi.init(new NetworkRequiredInfo(MyApplication.this));
        NetWorkMonitorManager.getInstance().init(this);
    }

    /**
     * 初始化依赖注入容器
     */
    public void initAppComponent() {
        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
