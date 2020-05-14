package com.willkong.networkdemo.api;

import android.content.Context;

import com.willkong.network.INetworkRequiredInfo;
import com.willkong.networkdemo.BuildConfig;

import java.io.File;

import okhttp3.Cache;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo
 * @Author: willkong
 * @CreateDate: 2020/5/13 10:15
 * @Description: 网络请求交互数据
 */
public class NetworkRequiredInfo implements INetworkRequiredInfo {
    private Context mContext;
    public NetworkRequiredInfo(Context mContext){
        this.mContext = mContext.getApplicationContext();
    }
    @Override
    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String getAppVersionCode() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public boolean isSetCache() {
        return false;
    }

    @Override
    public Cache getCache() {
        String CACHE_NAME = "retrofitcache";
        //设置缓存目录
        File cacheFile = new File(mContext.getExternalCacheDir(), CACHE_NAME);
        //生成缓存，50M
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        return cache;
    }

    @Override
    public Context getApplicationContext() {
        return mContext;
    }

}
