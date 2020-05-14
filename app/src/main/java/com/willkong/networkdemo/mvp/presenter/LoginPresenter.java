package com.willkong.networkdemo.mvp.presenter;

import com.willkong.networkdemo.api.DataBean;
import com.willkong.networkdemo.mvp.base.RequestCallback;
import com.willkong.networkdemo.mvp.contract.LoginContract;
import com.willkong.networkdemo.mvp.model.LoginModel;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.presenter
 * @Author: willkong
 * @CreateDate: 2020/5/14 14:14
 * @Description: LoginPresenter的P层
 */
public class LoginPresenter extends LoginContract.Presenter {

    /**
     * m层
     */
    private LoginModel loginModel;

    /**
     * mvp模式  p层持有  v 和m 的接口引用 来进行数据的传递  起一个中间层的作用
     */
    public LoginPresenter() {
        this.loginModel = new LoginModel();
    }

    @Override
    public void login(String nos, String signature, long timestamp) {
        mView.showLoading();
        loginModel.login(nos, signature, timestamp, new RequestCallback<DataBean>() {
            @Override
            public void onSuccess(DataBean data) {
                if (mView == null) return;
                mView.hideLoading();
                assert data != null;
                mView.setText(data);
            }

            @Override
            public void onFailure(String msg) {
                if (mView == null) return;
                mView.hideLoading();
                mView.showMsg(msg);
            }
        });
    }
}
