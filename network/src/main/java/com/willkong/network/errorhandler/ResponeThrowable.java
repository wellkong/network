package com.willkong.network.errorhandler;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.errorhandler
 * @Author: willkong
 * @CreateDate: 2020/5/21 16:46
 * @Description: 异常统一处理
 */
public class ResponeThrowable extends Exception {
    public int code;
    public String message;

    public ResponeThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    /**
     * 重写信息返回方法
     *
     * @return
     */
    @Override
    public String getMessage() {
        return message;
    }
}
