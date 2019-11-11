package cn.com.zjol.quick_login;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import java.lang.ref.SoftReference;

import cn.com.zjol.quick_login.callback.OnLoginCallback;
import cn.com.zjol.quick_login.callback.OnPrefetchNumberCallback;

/**
 * one click login
 * Created by wangzhen on 2019-11-11.
 */
public final class OneClickLogin {
    private static SoftReference<QuickLogin> mReference;
    private static String mBusinessId;
    private static Context mContext;
    private static Handler mHandler;
    private static OnLoginCallback mOnLoginCallback;

    /**
     * init
     *
     * @param context    context
     * @param businessId businessId
     */
    public static void init(Context context, String businessId) {
        mContext = context.getApplicationContext();
        mBusinessId = businessId;
        mHandler = new Handler();
    }

    /**
     * whether the app is debuggable or not
     *
     * @return is debuggable
     */
    private static boolean isDebuggable() {
        boolean debuggable;
        try {
            debuggable = (mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES)
                    .applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            debuggable = false;
        }
        return debuggable;
    }

    /**
     * get quick login instance from reference queue
     *
     * @return instance
     */
    private static QuickLogin instance() {
        if (mReference == null || mReference.get() == null) {
            mReference = new SoftReference<>(QuickLogin.getInstance(mContext, mBusinessId));
        }
        return mReference.get();
    }

    /**
     * 对于移动和联通而言因必须使用运营商界面，onGetMobileNumberSuccess回调中mobileNumber形参值为null，
     * 无需关心该值内容，直接在该回调中调用取号接口onePass即可展示一键登录界面并自动显示掩码mobileNumber
     *
     * @param callback OnPrefetchNumberCallback
     */
    public static void prefetchMobileNumber(final OnPrefetchNumberCallback callback) {
        QuickLogin instance = instance();
        instance.setDebugMode(isDebuggable());
        instance.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(final String token, final String mobileNumber) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess(token, mobileNumber);
                        }
                    }
                });
            }

            @Override
            public void onGetMobileNumberError(final String token, final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError(token, error);
                        }
                    }
                });
            }
        });
    }

    /**
     * do real one click login
     *
     * @param callback OnLoginCallback
     */
    public static void login(OnLoginCallback callback) {
        mOnLoginCallback = callback;
        instance().onePass(new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(String token, String accessCode) {
                tokenValidate(token, accessCode);
            }

            @Override
            public void onGetTokenError(String token, final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnLoginCallback != null) {
                            mOnLoginCallback.onError(error);
                        }
                    }
                });
            }
        });
    }

    private static void tokenValidate(String token, String accessCode) {
        Toast.makeText(mContext, "与服务端检验token", Toast.LENGTH_SHORT).show();
    }

    private static void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
