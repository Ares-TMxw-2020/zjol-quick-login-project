package cn.com.zjol.quick_login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.zjol.quick_login.callback.OnLoginCallback;
import cn.com.zjol.quick_login.callback.OnPrefetchNumberCallback;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private boolean mPrefetchSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        prefetch();
    }

    private void prefetch() {
        OneClickLogin.prefetchMobileNumber(new OnPrefetchNumberCallback() {
            @Override
            public void onSuccess(String token, String mobileNumber) {
                mPrefetchSuccess = true;
                mTextView.setText(mobileNumber);
            }

            @Override
            public void onError(String token, String error) {
                mPrefetchSuccess = false;
                mTextView.setText(error);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
    }

    public void onClick(View view) {
        if (!mPrefetchSuccess) {
            Toast.makeText(this, "预取手机号失败", Toast.LENGTH_SHORT).show();
            return;
        }
        OneClickLogin.login(new OnLoginCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String error) {
                mTextView.setText(error);
            }
        });
    }
}
