package cn.com.zjol.quick_login;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;
import com.netease.nis.quicklogin.utils.IConstants;
import com.zjrb.core.db.SPHelper;
import com.zjrb.core.utils.JsonUtils;
import com.zjrb.passport.Entity.AuthInfo;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.listener.ZbAuthListener;

import java.lang.ref.SoftReference;

import cn.com.zjol.biz.core.UserBiz;
import cn.com.zjol.biz.core.model.ZBLoginBean;
import cn.com.zjol.biz.core.nav.Nav;
import cn.com.zjol.biz.core.network.compatible.APICallBack;
import cn.com.zjol.biz.core.network.task.LoginValidateTask;
import cn.com.zjol.biz.core.utils.LoginHelper;
import cn.com.zjol.biz.core.utils.RouteManager;
import cn.com.zjol.biz.core.utils.ZBUtils;
import cn.com.zjol.quick_login.callback.OnLoginCallback;
import cn.com.zjol.quick_login.callback.OnPrefetchNumberCallback;
import cn.com.zjol.quick_login.common.ErrorCode;
import cn.com.zjol.quick_login.config.UIConfig;
import cn.com.zjol.quick_login.entity.ErrorEntity;

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
        instance.setCMLoginUiConfig(UIConfig.createCMLoginUI());
        instance.setCULoginUiConfig(UIConfig.createCULoginUI());
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
                        ErrorEntity entity = JsonUtils.parseObject(error, ErrorEntity.class);
                        if (entity != null && ErrorCode.OTHER_LOGIN.equals(entity.resultCode)) {
                            if (mOnLoginCallback != null) {
                                mOnLoginCallback.onOtherLogin();
                            }
                        } else {
                            if (mOnLoginCallback != null) {
                                mOnLoginCallback.onError(-1, error);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelGetToken() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnLoginCallback != null) {
                            mOnLoginCallback.onError(-1, "onCancelGetToken");
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
                        mOnLoginCallback.onError(-1, "AuthInfo is null");
                    }
                    return;
                }
                String code = info.getCode();
                new LoginValidateTask(new APICallBack<ZBLoginBean>() {
                    @Override
                    public void onSuccess(ZBLoginBean data) {
                        if (data != null) {
                            if (data.getAccount() != null) {
                                UserBiz.get().setZBLoginBean(data);
                                LoginHelper.get().setResult(true);
                                SPHelper.get().put("isPhone", true).put("last_login", data.getAccount().getMobile()).commit();
                                //新用户首次登录跳转积分任务页面
                                if (data.getAccount().isFirst_login()) {
                                    Nav.with(mContext).toPath(RouteManager.ZB_SCORE);
                                }
                                ZBUtils.showPointDialog(data);
                                if (mOnLoginCallback != null) {
                                    mOnLoginCallback.onSuccess();
                                }
                            }
                        } else {
                            if (mOnLoginCallback != null) {
                                mOnLoginCallback.onError(-1, "ZBLoginBean is null");
                            }
                        }
                    }

                    @Override
                    public void onError(String errMsg, int errCode) {
                        if (mOnLoginCallback != null) {
                            mOnLoginCallback.onError(errCode, errMsg);
                        }
                    }
                }).exe(code, code, "one_click", code);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (mOnLoginCallback != null) {
                    mOnLoginCallback.onError(errorCode, errorMessage);
                }
            }
        });
    }

    private static void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
