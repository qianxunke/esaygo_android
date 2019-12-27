package com.esaygo.app.utils.book.bean;

import com.esaygo.app.utils.book.utils.ConnectionUtils;

import org.apache.http.impl.client.CloseableHttpClient;

import okhttp3.OkHttpClient;

public class VCode {
	private String vcode;// 验证码
	private String timestamp;// 请求验证码时的时间戳
	private String callbackParameter;// 请求验证码时验证时间戳的回调参数
   private OkHttpClient okHttpClient;

	public OkHttpClient getOkHttpClient() {
		return okHttpClient;
	}

	public void setOkHttpClient(OkHttpClient okHttpClient) {
		this.okHttpClient = okHttpClient;
	}

	public String getCallbackParameter() {
		return callbackParameter;
	}

	public void setCallbackParameter(String callbackParameter) {
		this.callbackParameter = callbackParameter;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
