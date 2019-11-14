package cn.com.zjol.quick_login;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.CMLoginUiConfig;
import com.netease.nis.quicklogin.helper.CULoginUiConfig;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;
import com.netease.nis.quicklogin.utils.IConstants;
import com.zjrb.passport.Entity.AuthInfo;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.listener.ZbAuthListener;

import java.lang.ref.SoftReference;

import cn.com.zjol.biz.core.model.ZBLoginBean;
import cn.com.zjol.biz.core.network.compatible.APICallBack;
import cn.com.zjol.biz.core.network.task.LoginValidateTask;
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
        mHandler = new Handler(Looper.getMainLooper());
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
     * get the operator type
     *
     * @return operator type
     * @see com.netease.nis.quicklogin.utils.IConstants.OperatorType
     */
    public static IConstants.OperatorType operatorType() {
        return instance().getOperatorType(mContext);
    }

    /**
     * 对于移动和联通而言因必须使用运营商界面，onGetMobileNumberSuccess回调中mobileNumber形参值为null，
     * 无需关心该值内容，直接在该回调中调用取号接口onePass即可展示一键登录界面并自动显示掩码mobileNumber
     *
     * @param callback OnPrefetchNumberCallback
     */
    public static void prefetchMobileNumber(final OnPrefetchNumberCallback callback) {
        QuickLogin instance = instance();
        instance.setCMLoginUiConfig(createCMLoginUI());
        instance.setCULoginUiConfig(createCULoginUI());
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

    /**
     * validate token
     *
     * @param token      yidun token
     * @param accessCode yidun access code
     */
    private static void tokenValidate(String token, String accessCode) {
        ZbPassport.loginYiDun(token, accessCode, new ZbAuthListener() {
            @Override
            public void onSuccess(AuthInfo info) {
                if (info == null) {
                    if (mOnLoginCallback != null) {
                        mOnLoginCallback.onError("AuthInfo null");
                    }
                    return;
                }
                String code = info.getCode();
                new LoginValidateTask(new APICallBack<ZBLoginBean>() {
                    @Override
                    public void onSuccess(ZBLoginBean data) {
                        if (mOnLoginCallback != null) {
                            mOnLoginCallback.onSuccess();
                        }
                    }

                    @Override
                    public void onError(String errMsg, int errCode) {
                        if (mOnLoginCallback != null) {
                            mOnLoginCallback.onError(errMsg);
                        }
                    }
                }).exe(code, code, "one_click", code);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (mOnLoginCallback != null) {
                    mOnLoginCallback.onError(errorMessage);
                }
            }
        });
    }

    /**
     * create china mobile custom login ui
     * the default value for Integer Parameter is 0 and null for String Parameter if you do not want to modify specific parameter.
     *
     * @return custom login ui
     */
    private static CMLoginUiConfig createCMLoginUI() {
        return new CMLoginUiConfig()
                .setClauseText("登录即同意", "天目新闻隐私政策", "https://www.jianshu.com/p/8b89546d2c48", null, null, "并使用本机号码登录")
                ;
    }

    /**
     * create china unicom custom login ui
     * the default value for Integer Parameter is 0 and null for String Parameter if you do not want to modify specific parameter.
     *
     * @return custom login ui
     */
    private static CULoginUiConfig createCULoginUI() {
        return new CULoginUiConfig();
    }

    private static void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
