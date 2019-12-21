package com.esaygo.app.utils.network.exception;

/**
 * @author qianxunke
 * @email  736567805@qq.com
 * @date 2019-11-28 16:44
 * @desc api请求异常
 */
public class ApiException extends Exception {
    public ApiException(String msg) {
        super(msg);
    }
}
