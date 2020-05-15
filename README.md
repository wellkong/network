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

项目后期会加入依赖注解dragger2的使用，下面记录一下dragger2

## 命名规约
- @Provides方法用provide前缀命名
- @Module 用Module后缀命名
- @Component 以Component作为后缀

简单的说，就是一个工厂模式，由Dagger负责创建工厂，帮忙生产instance。遵从Java规范JSR 330，可以使用这些注解。现在不研究Dagger2是如何根据注解去生成工厂的，先来看看工厂是什么东西，理解为什么可以实现了DI(Dependency Injection)，如何创建IoC(Inverse of Control)容器。

- Dagger2是通过依赖注入完成类的初始化。

这个过程需要三部分：

1. 依赖提供方（生产者）
2. 依赖注入容器（桥梁）
3. 依赖需求方（消费者）
![01bb20639b6534f4ea371e021ccc98b6.jpeg](en-resource://database/928:0)

Dagger2是怎么选择依赖提供的呢，规则是这样的


**步骤1：查找Module中是否存在创建该类的方法。
步骤2：若存在创建类方法，查看该方法是否存在参数
步骤2.1：若存在参数，则按从步骤1开始依次初始化每个参数
步骤2.2：若不存在参数，则直接初始化该类实例，一次依赖注入到此结束
步骤3：若不存在创建类方法，则查找Inject注解的构造函数，看构造函数是否存在参数
步骤3.1：若存在参数，则从步骤1开始依次初始化每个参数
步骤3.2：若不存在参数，则直接初始化该类实例，一次依赖注入到此结束**


* 在使用@Component的时候必须要提供scope范围，标准范围是
* @Singleton@Component在使用@Module的时候必须匹配相同的scope能使用
* Singleton的时候，要注意标注，否则默认多例


总结：

* @Inject


主要有两个作用
**#1** 作为**依赖注提供方**：
使用@Inject注解构造方法。
注解**类**的**构造函数**，让Dagger2帮我们实例化该**类**，并注入。
**#2** 作为**依赖需求方**:
使用@Inject注解成员。
如果一个**成员变量**被@Inject注解修饰，并且**成员类**的**构造函数**也被@Inject注解，那么dagger2帮我们实例化该**成员类**，并注入。
通常在需要依赖的地方使用这个注解。换句话说，你用它告诉Dagger这个类或者字段需要依赖注入。这样，Dagger就会构造一个这个类的实例并满足他们的依赖。


使用@Inject可以让IoC容器负责生成instance，如果没有这个注解，dagger将不认识，当做普通类，无法代理


* @Module

**#1** @Module 注解类，负责管理依赖。
Module 其实是一个简单工厂模式，Module 里面的方法都是创建相应类实例的方法。
**#2** 通过@Module获得第三方类库的对象。
**#3** @Module是一个依赖提供方的合集。

```

@Modulepublic class AModule {

    @Provides
    public Gson provideGson(){
        return new Gson();
        }
   }
```

* @Provides

**#1** 注解@Module **类**中的**方法**。
在modules中，我们定义的方法是用这个注解，以此来告诉Dagger我们想要构造对象并提供这些依赖


* @Component

**#1** @Component一般用来注解接口。
**#2** 负责在@Inject和@Module之间建立连接。
也可以说是@Inject和@Module的桥梁，它的主要作用就是连接这两个部分。
**#3** 实例化@Inject注解的类时，遇到没有构造函数的类依赖，则该依赖由@Module修饰的类提供。
**#4** 依赖注入容器只是一个接口interface。


Component需要引用到目标类的实例，Component会查找目标类中用Inject注解标注的属性，查找到相应的属性后会接着查找该属性对应的用Inject标注的构造函数（这时候就发生联系了），剩下的工作就是初始化该属性的实例并把实例进行赋值。因此我们也可以给Component叫另外一个名字注入器（Injector）


Component注解的类,再编译之后,会生产一个以Dagger+类名的一个类,如下面的MainComponent会生成类DaggerMainComponent(补充一点,Kotlinkapt编译生成类的位置:\build\generated\source\kapt\debug),我们需要在目标类MainActivity中加入下面代码

```

DaggerMainComponent.builder()
                .build()
                .inject(this)
```


DaggerMainComponent使用了建造者设计模式,inject方法是我们MainComponent中定义的,这样目标类就和Component建立了联系.Component会去遍历使用@Inject注解的常量,然后去查找对应的类是否有@Inject注解的构造方法,如果没有就会报异常.

```

@Component {modules = {HeaterModule.class, PumperModule.class}}public interface MachineComponent {
  void inject(CoffeeMachine machine);
  }
```

dagger中Component就是最顶级的入口，dagger为之生成了工厂类 DaggerMachineComponent，目标是构建CoffeeMachine， 在CoffeeMachine中使用了Injection，那么依赖要由工厂类来提供。工厂类是根据modules的参数来找依赖绑定的。本例中，指向了HeaterModule, PumperModule，意思是CoffeeMachine的依赖要从这些module里找。


## 工厂名称生成规则

如果Component是接口, 则生成Dagger+接口名
如果Component是内部接口，比如本例，则生成Dagger+类名+ _+ 接口名

* @Scope

    Scopes可是非常的有用，Dagger2可以通过自定义注解限定注解作用域。后面会演示一个例子，这是一个非常强大的特点，因为就如前面说的一样，没 必要让每个对象都去了解如何管理他们的实例。在scope的例子中，我们用自定义的@PerActivity注解一个类，所以这个对象存活时间就和 activity的一样。简单来说就是我们可以定义所有范围的粒度(@PerFragment, @PerUser, 等等)。

* Qualifier

    当类的类型不足以鉴别一个依赖的时候，我们就可以使用这个注解标示。例如：在Android中，我们会需要不同类型的context，所以我们就可以定义 qualifier注解“@ForApplication”和“@ForActivity”，这样当注入一个context的时候，我们就可以告诉 Dagger我们想要哪种类型的context。

```

/**
 * View层，负责界面的展示
 */public class TestActivity extends AppCompatActivity implements IView{//当一个成员变量被@Inject注解修饰，并且它的类型构造函数也被@Inject注解修饰,dagger2就会自动实例化该成员类型，并注入到该成员变量
    @Inject
    TestPresent mPresent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        DaggerTestComponent.builder().testModule(new TestModule(this)).build().inject(this);//@Component负责连接起@Inject和@Module注解
        mPresent.updateUI();
    }
    @Override
    public void updateUI(String text) {
        ((TextView)findViewById(R.id.textview)).setText(text);
    }}
```
```

/**
 * Present类，调用Model层的业务方法，更新View层的界面展示
 */public class TestPresent {
    IView mView;
    @Inject
    TestModel mModel;//Dagger2遇到@Inject标记的成员属性，就会去查看该成员类的构造函数，如果构造函数也被@Inject标记,则会自动初始化，完成依赖注入。
    //TestPresent的构造函数也被@Inject注解修饰
    @Inject
    public TestPresent(IView view){
        this.mView=view;
    }
    public void updateUI(){
        mView.updateUI(mModel.getText());
    }}
```
```

/**
 * Model类，实现具体的业务逻辑
 */public class TestModel {
    //构造函数用@Inject修饰
    @Inject
    public TestModel(){
    }
    public String getText(){
        return "Dagger2应用实践...";
    }}
```
```

/**
 * Module类提供那些没有构造函数的类的依赖，如第三方类库，系统类，接口类
 */@Modulepublic class TestModule {
    private IView mView;
    public TestModule(IView iView){
        this.mView=iView;
    }
    //@Provides注解的方法，提供IView类的依赖。
    @Provides
    public IView provideIView(){
        return this.mView;
    }}
```
```

/**
 *Component必须是一个接口类或者抽象
 */
@Component(modules = TestModule.class)public interface TestComponent {
    void inject(TestActivity testActivity);}
```

