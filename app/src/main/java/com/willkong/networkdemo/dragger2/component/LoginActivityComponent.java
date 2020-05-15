package com.willkong.networkdemo.dragger2.component;

import com.willkong.networkdemo.MainActivity;
import com.willkong.networkdemo.dragger2.module.LoginActivityModule;
import com.willkong.networkdemo.dragger2.scope.ActivityScope;
import com.willkong.networkdemo.mvp.view.LoginActivity;

import dagger.Component;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.dragger2.component
 * @Author: willkong
 * @CreateDate: 2020/5/15 14:27
 * @Description:
 * ActivityComponent：生命周期跟Activity一样的组件，
 * 这里提供了inject方法将Activity注入到ActivityComponent中，
 * 通过该方法，将Activity中需要注入的对象注入到该Activity中。
 * 继承了AppComponent，子类component 依赖父类的component ，子类component的Scoped 要小于父类的Scoped，Singleton的级别是Application
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = LoginActivityModule.class)
public interface LoginActivityComponent {
    void inject(LoginActivity loginActivity);
}
