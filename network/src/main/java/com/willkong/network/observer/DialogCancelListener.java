package com.willkong.network.observer;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.observer
 * @Author: willkong
 * @CreateDate: 2020/5/15 10:25
 * @Description: 对话框隐藏或者消失之后取消请求
 */
public interface DialogCancelListener {
    /**
     * 取消网络请求
     */
    void onCancel();
}
