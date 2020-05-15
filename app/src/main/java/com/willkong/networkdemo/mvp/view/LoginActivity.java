package com.willkong.networkdemo.mvp.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.willkong.networkdemo.R;
import com.willkong.networkdemo.api.DataBean;
import com.willkong.networkdemo.dragger2.AppLoginReq;
import com.willkong.networkdemo.dragger2.component.AppComponent;
import com.willkong.networkdemo.dragger2.component.DaggerLoginActivityComponent;
import com.willkong.networkdemo.dragger2.module.LoginActivityModule;
import com.willkong.networkdemo.mvp.base.BaseMvpActivity;
import com.willkong.networkdemo.mvp.contract.LoginContract;
import com.willkong.networkdemo.mvp.presenter.LoginPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseMvpActivity<LoginContract.View, LoginContract.Presenter> implements LoginContract.View {
    @BindView(R.id.tv_text)
    TextView textView;
    @Inject
    AppLoginReq appLoginReq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginContract.Presenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerLoginActivityComponent
                .builder()
                .appComponent(appComponent)
                .loginActivityModule(new LoginActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void setText(DataBean result) {
        textView.setText("返回数据：" + new Gson().toJson(result));
    }

    @OnClick({R.id.login, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                mPresenter.login("messageType", "", 0);
                break;
            case R.id.back:
                LoginActivity.this.finish();
//                appLoginReq.setUserName("张三");
//                appLoginReq.setPwd("1234567");
//                textView.setText("用户：" + appLoginReq.getUserName() + "\n" + "密码：" + appLoginReq.getPwd());
                break;
        }
    }
}
