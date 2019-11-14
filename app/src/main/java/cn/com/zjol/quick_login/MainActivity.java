package cn.com.zjol.quick_login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nis.quicklogin.utils.IConstants;

import cn.com.zjol.quick_login.callback.OnLoginCallback;
import cn.com.zjol.quick_login.callback.OnPrefetchNumberCallback;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private boolean mPrefetchSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        prefetch();
    }

    private void prefetch() {
        OneClickLogin.prefetchMobileNumber(new OnPrefetchNumberCallback() {
            @Override
            public void onSuccess(String token, String mobileNumber) {
                mPrefetchSuccess = true;
                mTextView.setText(mobileNumber);

                IConstants.OperatorType operatorType = OneClickLogin.operatorType();
                switch (operatorType) {
                    case TYPE_CM:
                    case TYPE_CU:
                        onePass();
                        break;
                    case TYPE_CT:
                        //进入电信自定义授权页
                        Toast.makeText(MainActivity.this, "进入电信自定义授权页", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String token, String error) {
                mPrefetchSuccess = false;
                mTextView.setText(error);
            }
        });
    }

    private void initView() {
        mTextView = findViewById(R.id.text);
    }

    public void onClick(View view) {
        if (!mPrefetchSuccess) {
            Toast.makeText(this, "预取手机号失败", Toast.LENGTH_SHORT).show();
            return;
        }
        onePass();
    }

    private void onePass() {
        OneClickLogin.login(new OnLoginCallback() {
            @Override
            public void onSuccess() {
                mTextView.setText("通行证检验成功");
            }

            @Override
            public void onError(String error) {
                mTextView.setText(error);
            }
        });
    }
}
