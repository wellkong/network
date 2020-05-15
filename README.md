# network
封装简单实用的网络框架
添加打包功能：在Terminal中运行gradlew makeJar编译完成后自动生成到'build/libs/'目录下
gradlew makeJar

如果是引用jar包使用需要在工程中加入以下依赖
```
    api 'com.squareup.retrofit2:converter-gson:2.6.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.11.0'
    api 'com.squareup.retrofit2:adapter-rxjava2:2.6.0'
    api 'io.reactivex.rxjava2:rxandroid:2.1.1'
    api 'io.reactivex.rxjava2:rxjava:2.2.6'
```
    
    
    使用方法：
    1、工程中定义一个网络类继承NetworkApi实现模板方法
    
```
import com.willkong.network.NetworkApi;
import com.willkong.network.beans.BaseResponse;
import com.willkong.network.errorhandler.ExceptionHandler;

import io.reactivex.functions.Function;
import okhttp3.Interceptor;


public class AppNetworkApi extends NetworkApi {
    private static volatile AppNetworkApi sInstance;

    public static AppNetworkApi getInstance() {
        if (sInstance == null) {
            synchronized (AppNetworkApi.class) {
                if (sInstance == null) {
                    sInstance = new AppNetworkApi();
                }
            }
        }
        return sInstance;
    }
    /**
     * 获取服务对象
     *
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T getService(Class<T> service) {
        return getInstance().getRetrofit(service).create(service);
    }
    @Override
    protected Interceptor getInterceptor() {
        return null;
    }

    @Override
    protected <T> Function<T, T> getAppErrorHandler() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                //response中code码不为0 出现错误
                if (response instanceof BaseResponse && ((BaseResponse) response).code != 0) {
                    ExceptionHandler.ServerException exception = new ExceptionHandler.ServerException();
                    exception.code = ((BaseResponse) response).code;
                    exception.message = ((BaseResponse) response).message != null ? ((BaseResponse) response).message : "";
                    throw exception;
                }
                return response;
            }
        };
    }

    @Override
    public String getFormal() {
        return "http://";
    }

    @Override
    public String getTest() {
        return null;
    }
}
```
2、定义一个类NetworkRequiredInfo实现接口INetworkRequiredInfo给工具类需要的参数交互

```

import android.content.Context;

import com.willkong.network.INetworkRequiredInfo;
import com.willkong.testapplication.BuildConfig;

import java.io.File;

import okhttp3.Cache;

/**
 * @ProjectName: TestApplication
 * @Package: com.willkong.testapplication
 * @Author: willkong
 * @CreateDate: 2020/5/14 18:11
 * @Description: java类作用描述
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

```
3、在Application初始化网络工具
```

/**
 * @ProjectName: TestApplication
 * @Package: com.willkong.testapplication
 * @Author: willkong
 * @CreateDate: 2020/5/14 18:10
 * @Description: java类作用描述
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkApi.init(new NetworkRequiredInfo(this));
    }
}
```
4、定义网络请求接口
```

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
```
5、使用示例
```

    private void sendRequest(){
        AppNetworkApi.getService(AppApiInterface.class)
                .getTopNews("messageType", "", 0)
                .compose(AppNetworkApi.getInstance().applySchedulers(new BaseObserver() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.e(TAG, "test返回码："+((BaseResponse)o).code);
                        Log.e(TAG, "test返回信息："+((BaseResponse)o).message);
                        Log.e(TAG, "test返回体："+new Gson().toJson(((BaseResponse)o).data));
                        Log.e(TAG, new Gson().toJson(o));
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                }));
    }
```
MVP架构实现步骤：

1.创建一个基类View，让所有View接口都必须实现
```

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 11:28
 * @Description: mvp的view基类
 */
public interface BaseView {

    void showLoading();

    void hideLoading();

    /**
     * 弹出消息
     *
     * @param msg msg
     */
    void showMsg(String msg);
}

```
2.创建一个基类的Presenter，在类上规定View泛型，然后定义绑定和解绑的方法，对外在提供一个获取View的方法，让子类直接通过方法来获取View使用即可 
```

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 11:29
 * @Description: mvp的Presenter基类 引用基类view
 */
public class BasePresenter<V extends BaseView> {
    /**
     * v层泛型引用
     */
    protected V mView;

    private WeakReference<V> weakReferenceView;

    public void attachMvpView(V view) {
        weakReferenceView = new WeakReference<>(view);
        this.mView = weakReferenceView.get();
    }


    public void detachMvpView() {
        weakReferenceView.clear();
        weakReferenceView = null;
        mView = null;
    }
}
```
3.创建一个基类的Activity，声明一个创建Presenter的抽象方法，因为要帮子类去绑定和解绑那么就需拿到子类的Presenter才行，但是又不能随便一个类都能绑定的，因为只有基类的Presenter中才定义了绑定解绑的方法，所以同样的在类上可以声明泛型在方法上使用泛型来达到目的
```

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.willkong.networkdemo.mvp.utils.DialogUtils;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 12:36
 * @Description: BaseMvpActivity框架基类
 */
public abstract class BaseMvpActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {
    protected P mPresenter;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachMvpView((V) this);
        loadingDialog = DialogUtils.createLoadingDialog(this, "加载中...");
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachMvpView();
        }
    }

    @Override
    public void showLoading() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

```
```
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.willkong.networkdemo.mvp.utils.DialogUtils;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 12:47
 * @Description: Fragment基类
 */
public abstract class BaseMvpFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView {

    private OnFragmentInteractionListener mListener;
    protected P mPresenter;
    private Dialog loadingDialog;

    protected abstract P createPresenter();

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachMvpView((V) this);
        loadingDialog = DialogUtils.createLoadingDialog(getActivity(), "加载中...");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }

    @Override
    public void showLoading() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachMvpView();
        }
    }
}
```
4、新建m层接口和m层实现类实现解耦
```

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 12:49
 * @Description: model基类
 */
public interface BaseModel {
}
```
5、新建一个数据返回响应接口
```

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.mvp.base
 * @Author: willkong
 * @CreateDate: 2020/5/14 11:28
 * @Description: 数据响应回调
 */
public interface RequestCallback<T> {
    /**
     * 请求成功
     *
     * @param data 服务器返回的结果数据
     */
    void onSuccess(T data);

    /**
     * 请求失败
     *
     * @param msg 错误信息
     */
    void onFailure(String msg);
}
```
使用示例
1、为了方便使用，我们定义一个功能协议中间件Contract，里面定义了相互关联的m、v、p层
```

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

```
2、定义功能项相应的mvp层
LoginPresenter
```

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

```
LoginModel
```

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

```
LoginActivity
```

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

```
