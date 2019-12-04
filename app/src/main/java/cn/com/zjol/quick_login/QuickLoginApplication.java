package cn.com.zjol.quick_login;

import android.app.Activity;
import android.app.Application;

import com.aliya.uimode.UiModeManager;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.UIUtils;
import com.zjrb.passport.ZbConfig;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ZbConstants;

import cn.com.zjol.biz.core.network.DailyNetworkManager;
import cn.com.zjol.quick_login.common.AbsActivityLifecycleCallbacks;

/**
 * QuickLoginApplication
 * Created by wangzhen on 2019-11-11.
 */
public class QuickLoginApplication extends Application {
    private static final boolean isDebug = true;

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        AppUtils.setChannel("AppStore");
        UiModeManager.init(this, null);
        DailyNetworkManager.init(this);
        initPassport();
        OneClickLogin.init(this, "ad356081117148588b7c2376333de0a5");
        registerActivityLifecycleCallbacks(new AbsActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(Activity activity) {
                OneClickLogin.fitChinaMobileTypeface(activity, R.font.fzbiaoysk_zbjt);
            }
        });
    }

    /**
     * 初始化浙报通行证
     */
    private void initPassport() {
        ZbPassport.init(this, new ZbConfig.Builder().setEnvType(isDebug ? ZbConstants.Env.TEST : ZbConstants.Env.OFFICIAL)
                .setDebug(isDebug)
                .setAppVersion("1.0")
                .setClientId(isDebug ? 21 : 44)
                .setAppUuid("uuid"));
    }
}
