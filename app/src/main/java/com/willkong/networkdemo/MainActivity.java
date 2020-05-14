package com.willkong.networkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.willkong.network.beans.BaseResponse;
import com.willkong.network.environment.EnvironmentActivity;
import com.willkong.network.observer.BaseObserver;
import com.willkong.networkdemo.api.AppNetworkApi;
import com.willkong.networkdemo.api.AppApiInterface;
import com.willkong.networkdemo.mvp.view.LoginActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppNetworkApi.getService(AppApiInterface.class)
                        .getTopNews("messageType", "", 0)
                        .compose(AppNetworkApi.getInstance().applySchedulers(new BaseObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.e(TAG, "返回码："+((BaseResponse)o).code);
                                Log.e(TAG, "返回信息："+((BaseResponse)o).message);
                                Log.e(TAG, "返回体："+new Gson().toJson(((BaseResponse)o).data));
                                Log.e(TAG, new Gson().toJson(o));
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }));
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                MyNetworkApi.getService(NewsApiInterface.class)
//                        .getTopNews("messageType","",0)
//                        .compose(MyNetworkApi.getInstance().applySchedulers())

//                NetworkApi.getService(NewsApiInterface.class)
//                        .getTopNews("messageType", "", 0)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new BaseObserver<NewsBean>() {
//                            @Override
//                            public void onSuccess(NewsBean newsBean) {
//                                Log.e(TAG, new Gson().toJson(newsBean));
//                            }
//
//                            @Override
//                            public void onFailure(Throwable e) {
//                                Log.e(TAG, e.getMessage());
//                            }
//                        });
            }
        });

        findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i++ == 5) {
                    startActivity(new Intent(MainActivity.this, EnvironmentActivity.class));
                    i = 0;
                }
            }
        });
    }
}
