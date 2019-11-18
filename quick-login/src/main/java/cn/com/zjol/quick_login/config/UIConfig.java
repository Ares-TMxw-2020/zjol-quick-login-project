package cn.com.zjol.quick_login.config;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.netease.nis.quicklogin.helper.CMLoginUiConfig;
import com.netease.nis.quicklogin.helper.CULoginUiConfig;
import com.sdk.base.api.OnCustomViewListener;
import com.sdk.mobile.handler.UiHandler;
import com.zjrb.core.utils.UIUtils;

import cn.com.zjol.quick_login.R;
import cn.com.zjol.quick_login.common.Action;
import cn.com.zjol.quick_login.network.APIManager;

/**
 * UIConfig
 * Created by wangzhen on 2019-11-15.
 */
public class UIConfig {
    /**
     * create china mobile custom login ui
     * the default value for Integer Parameter is 0 and null for String Parameter if you do not want to modify specific value.
     *
     * @return custom login ui
     */
    public static CMLoginUiConfig createCMLoginUI() {
        return new CMLoginUiConfig()
                .setNavigationBar(getColor(R.color.white), "免密登录", getColor(R.color._222222), "@mipmap/ic_top_bar_back_dark", false)
                .setLogo("@mipmap/ic_login_chinamobile_logo", 70, 70, false, 100, 0)
                .setMobileMaskNumber(getColor(R.color._222222), 18, 130, 0)
                .setSlogan(getColor(R.color._666666), 195, 0)
                .setLoginButton(315, 44, "本机号码登录", getColor(R.color.white), "@drawable/user_btn_common", 244, 0)
                .setSwitchAccount(getColor(R.color.color_40a8c0), false, 344, 0)
                .setClause(10, getColor(R.color._666666), getColor(R.color.color_d12324), false, "", "", 10, 10, 0, 18, true)
                .setClauseText("登录即同意", "天目新闻用户协议", APIManager.getTianmuAgreementUrl(), null, null, "");
    }

    /**
     * create china unicom custom login ui
     * the default value for Integer Parameter is 0 and null for String Parameter if you do not want to modify specific value.
     *
     * @return custom login ui
     */
    public static CULoginUiConfig createCULoginUI() {
        return new CULoginUiConfig()
                .setProtocol(0, getColor(R.color.color_d12324), 12, "tv_tianmu_service_and_privacy", "天目新闻用户协议", APIManager.getTianmuAgreementUrl(), null, null, null)
                .setOtherLoginListener(new OnCustomViewListener() {
                    @Override
                    public void onClick(View view, UiHandler uiHandler) {
                        LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(new Intent(Action.AUTH_TELECOM_SWITCH));
                        uiHandler.finish();
                    }
                });
    }

    private static int getColor(int resId) {
        return ContextCompat.getColor(UIUtils.getContext(), resId);
    }
}
