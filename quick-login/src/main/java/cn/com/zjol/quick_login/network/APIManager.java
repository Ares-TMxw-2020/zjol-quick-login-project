package cn.com.zjol.quick_login.network;

import com.zjrb.core.utils.UIUtils;

import cn.com.zjol.biz.core.db.SettingManager;
import cn.com.zjol.quick_login.R;

/**
 * APIManager
 * Created by wangzhen on 2019-11-15.
 */
public class APIManager {
    public static String scheme() {
        return SettingManager.getInstance().isOpenHttps() ? "https" : "http";
    }

    public static String host() {
        return SettingManager.getInstance().getHost();
    }

    public static String tmHost() {
        return UIUtils.getString(R.string.env_tm);
    }


    public static String getTianmuAgreementUrl() {
        return scheme() + "://" + tmHost() + "/agreement.html?title=用户协议";
    }

    public static String getTianyiAgreementUrl() {
        return "https://e.189.cn/sdk/agreement/content.do?type=main&appKey=&hidetop=true&returnUrl=";
    }
}
