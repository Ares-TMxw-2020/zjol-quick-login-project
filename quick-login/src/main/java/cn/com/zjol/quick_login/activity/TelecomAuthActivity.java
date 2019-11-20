package cn.com.zjol.quick_login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.zjol.biz.core.DailyActivity;
import cn.com.zjol.biz.core.nav.Nav;
import cn.com.zjol.quick_login.R;
import cn.com.zjol.quick_login.common.Action;
import cn.com.zjol.quick_login.common.QuickAuthKey;
import cn.com.zjol.quick_login.network.APIManager;
import cn.daily.android.statusbar.DarkStatusBar;

/**
 * telecom auth activity
 * Created by wangzhen on 2019-11-15.
 */
public class TelecomAuthActivity extends DailyActivity {

    private TextView mTvMaskNumber;
    private CheckBox mCheckboxAgreement;

    private String mMaskNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telecom_auth);
        DarkStatusBar.get().fitDark(this);
        initArgs();
        initViews();
        bind();
    }

    private void bind() {
        mTvMaskNumber.setText(mMaskNumber);
        mCheckboxAgreement.setText(buildAgreementSpannable());
    }

    private Spannable buildAgreementSpannable() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("登录即同意");
        int start = builder.length();
        builder.append("天翼账号服务协议");
        int end = builder.length();
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Nav.with(widget.getContext()).to(APIManager.getTianyiAgreementUrl());
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.color_d12324));
                ds.setUnderlineText(false);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("与");
        start = builder.length();
        builder.append("天目新闻用户协议");
        end = builder.length();
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Nav.with(widget.getContext()).to(APIManager.getTianmuAgreementUrl());
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.color_d12324));
                ds.setUnderlineText(false);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private void initArgs() {
        mMaskNumber = getIntent().getStringExtra(QuickAuthKey.MASK_NUMBER);
    }

    private void initViews() {
        mTvMaskNumber = (TextView) findViewById(R.id.tv_mask_number);
        mCheckboxAgreement = (CheckBox) findViewById(R.id.checkbox_agreement);
        mCheckboxAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean isShowTopBar() {
        return false;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_telecom_login) {
            if (!mCheckboxAgreement.isChecked()) {
                Toast.makeText(this, "请同意服务条款", Toast.LENGTH_SHORT).show();
                return;
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Action.AUTH_TELECOM_LOGIN));
            setResult(RESULT_OK);
        } else if (id == R.id.tv_switch_account) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Action.AUTH_TELECOM_SWITCH));
            setResult(RESULT_OK);
        }
        finish();
    }
}
