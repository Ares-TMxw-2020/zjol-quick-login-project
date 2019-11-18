package cn.com.zjol.quick_login.entity;

import java.io.Serializable;

/**
 * ErrorEntity
 * Created by wangzhen on 2019-11-18.
 */
public class ErrorEntity implements Serializable {

    /**
     * resultCode : 200060
     * authType : 0
     * authTypeDes : 其他
     * resultDesc : 第三方登录方式
     */

    public String resultCode;
    public String authType;
    public String authTypeDes;
    public String resultDesc;
}
