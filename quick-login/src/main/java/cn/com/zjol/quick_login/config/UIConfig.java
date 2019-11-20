package cn.com.zjol.quick_login.config;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.cmic.sso.sdk.AuthThemeConfig;
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
        AuthThemeConfig config = new AuthThemeConfig.Builder()
                //顶部导航栏
                .setAuthNavTransparent(false)
                .setNavColor(getColor(R.color.color_f5f5f5))
                .setNavText("一键登录")
                .setNavTextColor(getColor(R.color._222222))
                .setNavReturnImgPath("@mipmap/ic_top_bar_back_dark")
                //去除logo
                .setLogoHidden(true)
                //手机掩码
                .setNumberColor(getColor(R.color._222222))
                .setNumberSize(18)
                .setNumFieldOffsetY(130)
                //slogan
                .setSloganText(16, getColor(R.color._666666))
                .setSloganOffsetY(195)
                //登录按钮
                .setLogBtnText("本机号码登录", getColor(R.color.white), 16)
                .setLogBtnImgPath("@drawable/user_btn_common")
                .setLogoWidthDip(315)
                .setLogoHeightDip(44)
                .setLogBtnOffsetY(244)
                //切换帐号
                .setSwitchAccTex("切换帐号", getColor(R.color.color_40a8c0), 16)
                .setSwitchOffsetY(344)
                //用户协议
                .setPrivacyState(true)
                .setPrivacyOffsetY_B(20)
                .setPrivacyText(10, getColor(R.color._666666), getColor(R.color.color_d12324), false)
                .privacyAlignment("登录即同意", "天目新闻用户协议", APIManager.getTianmuAgreementUrl(), "", "", "")
                .setCheckBoxImgPath("umcsdk_check_image", "umcsdk_uncheck_image", 0, 0)
                .build();
        return new CMLoginUiConfig().setAuthThemeConfig(config);
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

    private static int dip2px(float dip) {
        final float scale = UIUtils.getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
