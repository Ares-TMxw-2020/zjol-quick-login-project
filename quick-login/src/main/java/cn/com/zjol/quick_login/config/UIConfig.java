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
                .setNavigationBar(getColor(R.color.color_f5f5f5), "一键登录", getColor(R.color._222222), "@mipmap/ic_top_bar_back_dark", false)
                .setLogo(null, 0, 0, true, 0, 0)
                .setMobileMaskNumber(getColor(R.color._222222), 18, 130, 0)
                .setSlogan(getColor(R.color._666666), 195, 0)
                .setLoginButton(315, 44, "本机号码登录", getColor(R.color.white), "@drawable/user_btn_common", 244, 0)
                .setSwitchAccount(getColor(R.color.color_40a8c0), false, 344, 0)
                .setClause(10, getColor(R.color._666666), getColor(R.color.color_d12324), false, "@mipmap/quick_login_checkbox_checked", "@mipmap/quick_login_checkbox_uncheck", 10, 10, 0, 20, true)
                .setClauseText("我已阅读并同意", "天目新闻用户协议", APIManager.getTianmuAgreementUrl(), null, null, "");
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
                .setShowProtocolBox(true)
                .setLoginButton(dip2px(315), dip2px(44), dip2px(30), "本机号码登录", R.drawable.quick_login_btn_unicom, R.drawable.quick_login_btn_unicom)
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

    public static int dip2px(float dip) {
        final float scale = UIUtils.getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
