package cn.com.zjol.quick_login;

import android.app.Application;

/**
 * QuickLoginApplication
 * Created by wangzhen on 2019-11-11.
 */
public class QuickLoginApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneClickLogin.init(this, "123");
    }
}
