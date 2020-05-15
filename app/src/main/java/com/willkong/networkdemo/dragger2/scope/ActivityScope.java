package com.willkong.networkdemo.dragger2.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.dragger2.scope
 * @Author: willkong
 * @CreateDate: 2020/5/15 14:25
 * @Description: @ActivityScope注解，这个注解是自定义的，对应Activity的生命周期，Dagger2可以通过自定义注解限定注解作用域
 */
@Scope
@Retention(RUNTIME)
public @interface ActivityScope {
}
