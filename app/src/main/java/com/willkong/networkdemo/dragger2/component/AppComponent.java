package com.willkong.networkdemo.dragger2.component;

import android.content.Context;

import com.willkong.networkdemo.dragger2.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.dragger2.component
 * @Author: willkong
 * @CreateDate: 2020/5/15 14:30
 * @Description:
 * AppComponent: 生命周期跟Application一样的组件。
 * 可注入到自定义的Application类中，
 * @Singletion代表各个注入对象为单例。
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    // 提供Applicaiton的Context
    Context context();
}
