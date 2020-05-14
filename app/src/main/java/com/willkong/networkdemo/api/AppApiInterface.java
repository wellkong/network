package com.willkong.networkdemo.api;

import com.willkong.network.beans.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network
 * @Author: willkong
 * @CreateDate: 2020/5/12 16:38
 * @Description: api网络请求接口
 */
public interface AppApiInterface {
    @GET("/v1/public/dictionary-info/commom-api/{nos}")
    Observable<BaseResponse<DataBean>> getTopNews(@Path("nos") String nos,
                                                  @Query("signature") String signature,
                                                  @Query("timestamp") long timestamp);
}
