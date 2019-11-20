package cn.com.zjol.quick_login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.netease.nis.quicklogin.utils.IConstants;
import com.zjrb.core.utils.UIUtils;

import cn.com.zjol.biz.core.DailyActivity;
import cn.com.zjol.biz.core.UserBiz;
import cn.com.zjol.biz.core.nav.Nav;
import cn.com.zjol.biz.core.utils.LoadingDialogUtils;
import cn.com.zjol.biz.core.utils.LoginHelper;
import cn.com.zjol.quick_login.callback.OnLoginCallback;
import cn.com.zjol.quick_login.callback.OnPrefetchNumberCallback;
import cn.com.zjol.quick_login.common.Action;
import cn.com.zjol.quick_login.common.QuickAuthKey;
import cn.com.zjol.quick_login.route.RouteManager;

public class MainActivity extends DailyActivity {

    private static final int REQUEST_CODE_TELECOM_AUTH = 0x1;
    private Context mContext;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        register();
        prefetch();
    }

    private void register() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TextUtils.equals(action, Action.AUTH_TELECOM_LOGIN)) {
                    onePass();
                } else if (TextUtils.equals(action, Action.AUTH_TELECOM_SWITCH)) {
                    switchLoginWay();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.AUTH_TELECOM_LOGIN);
        filter.addAction(Action.AUTH_TELECOM_SWITCH);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    private void switchLoginWay() {
        LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
        Nav.with(this).toPath("/login/validate/code");
        finish();
    }

    private void prefetch() {
        LoadingDialogUtils.newInstance().getLoginingDialog();
        OneClickLogin.prefetchMobileNumber(new OnPrefetchNumberCallback() {
            @Override
            public void onSuccess(String token, String mobileNumber) {
                LoginHelper.get().setLogin(true);
                IConstants.OperatorType operatorType = OneClickLogin.operatorType();
                switch (operatorType) {
                    case TYPE_CM:
                    case TYPE_CU:
                        onePass();
                        break;
                    case TYPE_CT:
                        Bundle bundle = new Bundle();
                        bundle.putString(QuickAuthKey.MASK_NUMBER, mobileNumber);
                        Nav.with(mContext).setExtras(bundle).toPath(RouteManager.TELECOM_AUTH, REQUEST_CODE_TELECOM_AUTH);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String token, String error) {
                switchLoginWay();
            }
        });
    }

    private void onePass() {
        OneClickLogin.login(new OnLoginCallback() {
            @Override
            public void onSuccess() {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(true, "登录成功");
                finishDelay();
            }

            @Override
            public void onError(int errorCode, String error) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, "登录失败");
                finishDelay();
            }

            @Override
            public void onOtherLogin() {
                switchLoginWay();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TELECOM_AUTH) {
            if (resultCode != RESULT_OK) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, "登录失败");
                finishDelay();
            }
        }
    }

    private void finishDelay() {
        UIUtils.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1200);
    }

    @Override
    public void finish() {
        super.finish();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Action.USER_LOGIN));
        LoginHelper.get().finish();
        Intent intent = getIntent();
        if (intent != null) {
            intent.putExtra("LoginMainIsLoginUser", UserBiz.get().isLoginUser());
            setResult(Activity.RESULT_OK, intent);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
}
