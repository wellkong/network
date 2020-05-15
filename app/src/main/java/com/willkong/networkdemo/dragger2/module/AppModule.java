package com.willkong.networkdemo.dragger2.module;

import android.content.Context;

import com.willkong.networkdemo.application.MyApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.dragger2.module
 * @Author: willkong
 * @CreateDate: 2020/5/15 14:32
 * @Description:
 * AppModule: 这里提供了AppComponent里的需要注入的对象。
 */
@Module
public class AppModule {
    private final MyApplication application;
    public AppModule(MyApplication application) {
        this.application = application;
    }
    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }
}
