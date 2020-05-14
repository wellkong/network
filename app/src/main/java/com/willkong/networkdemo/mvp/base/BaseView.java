package com.willkong.networkdemo.mvp.base;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 11:28
 * @Description: mvp的view基类
 */
public interface BaseView {

    void showLoading();

    void hideLoading();

    /**
     * 弹出消息
     *
     * @param msg msg
     */
    void showMsg(String msg);
}
