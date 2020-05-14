package com.willkong.network;

import android.content.Context;

import okhttp3.Cache;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network
 * @Author: willkong
 * @CreateDate: 2020/5/13 10:08
 * @Description: 网络配置信息交互接口
 */
public interface INetworkRequiredInfo {
    String getAppVersionName();
    String getAppVersionCode();
    boolean isDebug();
    boolean isSetCache();
    Cache getCache();
    Context getApplicationContext();
}
