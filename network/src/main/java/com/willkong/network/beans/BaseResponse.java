package com.willkong.network.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.beans
 * @Author: willkong
 * @CreateDate: 2020/5/12 16:08
 * @Description: 网络回调基础类，加注解序列化
 */
public class BaseResponse<T> implements Serializable {
    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public T data;
}
