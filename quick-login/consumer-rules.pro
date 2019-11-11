-dontwarn com.cmic.sso.sdk.**
-keep public class com.cmic.sso.sdk.**{*;}
-keep class cn.com.chinatelecom.account.api.**{*;}
-keep class com.netease.nis.quicklogin.entity.**{*;}
-keep class com.netease.nis.quicklogin.listener.**{*;}
-keep class com.netease.nis.quicklogin.QuickLogin{
    public <methods>;
    public <fields>;
}
-keep class com.netease.nis.quicklogin.helper.CULoginUiConfig{*;}
-keep class com.netease.nis.quicklogin.helper.CMLoginUiConfig{*;}
-keep class com.netease.nis.quicklogin.utils.IConstants$OperatorType{*;}
-dontwarn com.sdk.**
-keep class com.sdk.** { *;}