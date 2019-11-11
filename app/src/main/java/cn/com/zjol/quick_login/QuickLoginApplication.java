package cn.com.zjol.quick_login;

import android.app.Application;

import com.aliya.uimode.UiModeManager;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.UIUtils;

import cn.com.zjol.biz.core.network.DailyNetworkManager;

/**
 * QuickLoginApplication
 * Created by wangzhen on 2019-11-11.
 */
public class QuickLoginApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        AppUtils.setChannel("AppStore");
        UiModeManager.init(this, null);
        DailyNetworkManager.init(this);
        OneClickLogin.init(this, "123");
    }
}
