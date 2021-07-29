package com.willkong.network;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network
 * @ClassName: CallAdapterFactory
 * @Author: willkong
 * @Description: 优化线程切换的统一处理
 * @CreateDate: 2021/7/28 23:44
 */
public class CallAdapterFactory<R,W> implements CallAdapter<R,W> {

    private CallAdapter<R,W> mAdapter;
    private Function<W,W> mFunction;

    public static <R,W> CallAdapterFactory<R,W> create(@NonNull CallAdapter<R,W> mAdapter, @NonNull Function<W,W>mFunction){
        return new CallAdapterFactory<>(mAdapter,mFunction);
    }

    private CallAdapterFactory(@NonNull CallAdapter<R, W> mAdapter, @NonNull Function<W, W> mFunction) {
        this.mAdapter = mAdapter;
        this.mFunction = mFunction;
    }

    @Override
    public Type responseType() {
        return mAdapter.responseType();
    }

    @Override
    public W adapt(Call<R> call) {
        W adapt = mAdapter.adapt(call);
        try {
            return mFunction.apply(adapt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adapt;
    }
}
