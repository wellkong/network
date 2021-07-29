package com.willkong.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.willkong.network.common.RxManager;
import com.willkong.network.commoninterceptor.CachInterceptor;
import com.willkong.network.commoninterceptor.CommonRequestInterceptor;
import com.willkong.network.commoninterceptor.CommonResponseInterceptor;
import com.willkong.network.convert.gson.DoubleDefaultAdapter;
import com.willkong.network.convert.gson.IntegerDefaultAdapter;
import com.willkong.network.convert.gson.LongDefaultAdapter;
import com.willkong.network.convert.gson.StringNullAdapter;
import com.willkong.network.environment.EnvironmentActivity;
import com.willkong.network.environment.IEnvironment;
import com.willkong.network.errorhandler.HttpErrorHandler;
import com.willkong.network.utils.ApiDns;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network
 * @Author: willkong
 * @CreateDate: 2020/5/12 16:31
 * @Description: 网络封装
 */
public abstract class NetworkApi implements IEnvironment {
    private static final String TAG = "NetworkApi";
    private static INetworkRequiredInfo iNetworkRequiredInfo;
    private static HashMap<String, Retrofit> retrofitHashMap = new HashMap<>();
    private String mBaseUrl;
    private OkHttpClient okHttpClient;
    /**
     * gosn格式化处理
     */
    private Gson gson;
    /**
     * 是否正式环境
     */
    private static boolean mIsFormal = true;

    protected NetworkApi() {
        if (!mIsFormal) {
            this.mBaseUrl = getTest();
        }else {
            mBaseUrl = getFormal();
        }
    }

    /**
     * 初始化，获取交互信息
     *
     * @param networkRequiredInfo
     */
    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        iNetworkRequiredInfo = networkRequiredInfo;
        mIsFormal = EnvironmentActivity.isOfficialEnvironment(networkRequiredInfo.getApplicationContext());
        Log.i(TAG,"mIsFormal="+mIsFormal);

    }

    protected Retrofit getRetrofit(Class service) {
        if (retrofitHashMap.get(mBaseUrl + service.getName()) != null) {
            return retrofitHashMap.get(mBaseUrl + service.getName());
        }
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(mBaseUrl);
        retrofitBuilder.client(getOkHttpClient());
//        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(buildGson()));//添加json转换框架(正常转换框架)
//        retrofitBuilder.addConverterFactory(MyGsonConverterFactory.create(buildGson()));//添加json自定义（根据需求，此种方法是拦截gson解析所做操作）
//        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        retrofitBuilder.addCallAdapterFactory(WillkongRxJava2CallAdapterFactory.create(getAppErrorHandler()));
        Retrofit retrofit = retrofitBuilder.build();
        retrofitHashMap.put(mBaseUrl + service.getName(), retrofit);
        return retrofit;
    }

    /**
     * okhttp使用建造者模式
     * 创建了客户端
     *
     * @return
     */
    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            if (getInterceptor()!=null&&getInterceptor().size()>0){
                List<Interceptor> list = getInterceptor();
                for (int i = 0; i <list.size(); i++) {
                    okHttpClientBuilder.addInterceptor(list.get(i));
                }
            }
            okHttpClientBuilder.addInterceptor(new CommonRequestInterceptor(iNetworkRequiredInfo));
            okHttpClientBuilder.addInterceptor(new CommonResponseInterceptor());
            //根据开发环境判断是否加入log打印
            if (iNetworkRequiredInfo != null && iNetworkRequiredInfo.isDebug()) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
            }
            //判断是否设置缓存
            if (iNetworkRequiredInfo != null && iNetworkRequiredInfo.isSetCache() && iNetworkRequiredInfo.getApplicationContext() != null) {
                CachInterceptor cachInterceptor = new CachInterceptor(iNetworkRequiredInfo.getApplicationContext());
                okHttpClientBuilder.addInterceptor(cachInterceptor);
                if (iNetworkRequiredInfo.getCache() != null) {
                    okHttpClientBuilder.cache(iNetworkRequiredInfo.getCache());
                } else {
                    String CACHE_NAME = "retrofitcache";
                    //设置缓存目录
                    File cacheFile = new File(iNetworkRequiredInfo.getApplicationContext().getApplicationContext().getExternalCacheDir(), CACHE_NAME);
                    //生成缓存，50M
                    Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
                    okHttpClientBuilder.cache(cache);
                }
            }
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            /**
             * 处理一些识别识别不了 ipv6手机，如小米  实现方案  将ipv6与ipv4置换位置，首先用ipv4解析
             */
            okHttpClientBuilder.dns(new ApiDns());
            okHttpClient = okHttpClientBuilder.build();
        }

        return okHttpClient;
    }

    /**
     * 线程切换封装
     *
     * @param observer
     * @param <T>
     * @return
     */
    public <T> ObservableTransformer<T, T> applySchedulers(final Observer<T> observer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<T> observable = (Observable<T>) upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())//解除订阅子线程
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(getAppErrorHandler())
                        .onErrorResumeNext(new HttpErrorHandler<T>());
                observable.subscribe(observer);
                return observable;
            }
        };
    }
    /**
     * 增加后台返回""和"null"的处理,如果后台返回格式正常，此处不需要添加
     * 1.int=>0
     * 2.double=>0.00
     * 3.long=>0L
     * 4.String=>""
     *
     * @return
     */
    public Gson buildGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Integer.class, new IntegerDefaultAdapter())
                    .registerTypeAdapter(int.class, new IntegerDefaultAdapter())
                    .registerTypeAdapter(Double.class, new DoubleDefaultAdapter())
                    .registerTypeAdapter(double.class, new DoubleDefaultAdapter())
                    .registerTypeAdapter(Long.class, new LongDefaultAdapter())
                    .registerTypeAdapter(long.class, new LongDefaultAdapter())
                    .registerTypeAdapter(String.class, new StringNullAdapter())
                    .create();
        }
        return gson;
    }

    protected abstract List<Interceptor> getInterceptor();

    protected abstract <T> Function<T, T> getAppErrorHandler();

//    /**
//     * 获取服务对象
//     *
//     * @param service
//     * @param <T>
//     * @return
//     */
//    public static <T> T getService(Class<T> service) {
//        return getRetrofit(service).create(service);
//    }


//    /**
//     * 处理服务错误
//     * @param <T>
//     * @return
//     */
//    public static <T> Function<T,T> getAppErrorHandler(){
//        return new Function<T, T>() {
//            @Override
//            public T apply(T response) throws Exception {
//                //response中code码不为0 出现错误
//                if (response instanceof BaseResponse && ((BaseResponse)response).code != 0){
//                    ExceptionHandler.ServerException exception = new ExceptionHandler.ServerException();
//                    exception.code = ((BaseResponse)response).code;
//                    exception.message = ((BaseResponse)response).message !=null ? ((BaseResponse)response).message:"";
//                    throw exception;
//                }
//                return response;
//            }
//        };
//    }
}
