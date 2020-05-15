package com.willkong.networkdemo.dragger2.module;

import android.app.Activity;

import com.willkong.networkdemo.dragger2.AppLoginReq;
import com.willkong.networkdemo.dragger2.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.dragger2.module
 * @Author: willkong
 * @CreateDate: 2020/5/15 14:38
 * @Description: 注入Activity，同时规定Activity所对应的域是@ActivityScope 单例
 */
@Module
public class LoginActivityModule {
    private final Activity activity;

    public LoginActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    Activity activity() {
        return this.activity;
    }

    @ActivityScope
    @Provides
    AppLoginReq provideAppLoginReq(){
        return new AppLoginReq();
    }
}
