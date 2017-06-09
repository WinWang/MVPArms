package com.jess.arms.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jess.arms.base.delegate.IActivity;
import com.jess.arms.mvp.IPresenter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import javax.inject.Inject;

import static com.jess.arms.base.delegate.ActivityDelegate.LAYOUT_FRAMELAYOUT;
import static com.jess.arms.base.delegate.ActivityDelegate.LAYOUT_LINEARLAYOUT;
import static com.jess.arms.base.delegate.ActivityDelegate.LAYOUT_RELATIVELAYOUT;

/**
 * 因为java只能单继承,所以如果有需要继承特定Activity的三方库,那你就需要自己自定义Activity
 * 继承于这个特定的Activity,然后按照将BaseActivity的格式,复制过去记住一定要实现{@link IActivity}
 */
public abstract class BaseActivity<P extends IPresenter> extends RxAppCompatActivity implements IActivity{
    protected final String TAG = this.getClass().getSimpleName();
    @Inject
    protected P mPresenter;

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }
        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }
        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }
        return view == null ? super.onCreateView(name, context, attrs) : view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAfterCallback(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return
     */
    @Override
    public boolean useEventBus() {
        return true;
    }

    /**
     * 这个Activity是否会使用Fragment,框架会根据这个属性判断是否注册{@link android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks}
     * 如果返回false,那意味着这个Activity不需要绑定Fragment,那你再在这个Activity中绑定继承于 {@link com.jess.arms.base.BaseFragment} 的Fragment将不起任何作用
     * @return
     */
    @Override
    public boolean useFragment() {
        return true;
    }

    /**
     * 用来初始化或恢复数据。由于{@link android.app.Application.ActivityLifecycleCallbacks} 不能完全代理Activity的生命周期。
     * 在{@link com.jess.arms.base.BaseActivity#onCreate(Bundle)}的super.onCreate() 后执行。
     * BaseActivity提供一个空实现,因为大多数情况下{@link #initData(Bundle)} 方法可以满足需求。
     *
     * 使用场景：
     * 如果Activity是使用Fragment，并且要想处理Activity重建时重用已经创建好的Fragment，在Activity则重写此方法。
     * 由于FragmentManager的恢复是在 onCreate() 方法返回后执行的，所以在 {@link #initData(Bundle)} 中恢复Fragment会返回null。
     *
     * @param savedInstanceState
     */
    @Override
    public void initAfterCallback(Bundle savedInstanceState) {

    }
}
