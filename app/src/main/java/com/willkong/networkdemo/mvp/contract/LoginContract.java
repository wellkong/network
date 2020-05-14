package com.willkong.networkdemo.mvp.contract;

import com.willkong.networkdemo.api.DataBean;
import com.willkong.networkdemo.mvp.base.BaseModel;
import com.willkong.networkdemo.mvp.base.BasePresenter;
import com.willkong.networkdemo.mvp.base.BaseView;
import com.willkong.networkdemo.mvp.base.RequestCallback;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.contract
 * @Author: willkong
 * @CreateDate: 2020/5/14 13:56
 * @Description: 中间件
 */
public interface LoginContract {

    interface View extends BaseView {
        /**
         * 将数据返回给view
         *
         * @param result resuklt
         */
        void setText(DataBean result);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void login(String nos, String signature, long timestamp);
    }

    interface Model extends BaseModel {
        /**
         * 登陆方法
         */
        void login(String nos, String signature, long timestamp, RequestCallback<DataBean> callback);
    }

}
