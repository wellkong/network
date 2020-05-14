package com.willkong.networkdemo.mvp.model;

import com.willkong.network.beans.BaseResponse;
import com.willkong.network.observer.BaseObserver;
import com.willkong.networkdemo.api.AppApiInterface;
import com.willkong.networkdemo.api.AppNetworkApi;
import com.willkong.networkdemo.api.DataBean;
import com.willkong.networkdemo.mvp.base.BaseImpl;
import com.willkong.networkdemo.mvp.base.RequestCallback;
import com.willkong.networkdemo.mvp.contract.LoginContract;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.model
 * @Author: willkong
 * @CreateDate: 2020/5/14 13:50
 * @Description: 登录数据模型类
 */
public class LoginModel extends BaseImpl<AppApiInterface> implements LoginContract.Model {
    public LoginModel() {
        super(AppNetworkApi.getService(AppApiInterface.class));
    }

    @Override
    public void login(String nos, String signature, long timestamp, final RequestCallback<DataBean> callback) {
        mService.getTopNews(nos, signature, timestamp)
                .compose(AppNetworkApi.getInstance().applySchedulers(new BaseObserver() {
                    @Override
                    public void onSuccess(Object o) {
                        callback.onSuccess(((BaseResponse<DataBean>) o).data);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                }));
    }
}
