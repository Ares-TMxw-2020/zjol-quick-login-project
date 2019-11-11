package cn.com.zjol.quick_login.callback;

/**
 * OnePassCallback
 * Created by wangzhen on 2019-11-11.
 */
public interface OnLoginCallback {
    void onSuccess();

    void onError(String error);
}
