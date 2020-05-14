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
