package cn.com.zjol.quick_login.callback;

/**
 * PrefetchCallback
 * Created by wangzhen on 2019-11-11.
 */
public interface OnPrefetchNumberCallback {
    void onSuccess(String token, String mobileNumber);

    void onError(String token, String error);
}
