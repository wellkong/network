package com.willkong.networkdemo.mvp.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.willkong.networkdemo.R;
import com.willkong.networkdemo.api.DataBean;
import com.willkong.networkdemo.mvp.base.BaseMvpActivity;
import com.willkong.networkdemo.mvp.contract.LoginContract;
import com.willkong.networkdemo.mvp.presenter.LoginPresenter;

public class LoginActivity extends BaseMvpActivity<LoginContract.View, LoginContract.Presenter> implements View.OnClickListener, LoginContract.View {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView = findViewById(R.id.tv_text);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    protected LoginContract.Presenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                mPresenter.login("messageType", "", 0);
                break;
            case R.id.back:
                LoginActivity.this.finish();
                break;
        }
    }

    @Override
    public void setText(DataBean result) {
        textView.setText("返回数据：" + new Gson().toJson(result));
    }
}
