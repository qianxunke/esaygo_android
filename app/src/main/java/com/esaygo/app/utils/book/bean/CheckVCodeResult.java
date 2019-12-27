package com.esaygo.app.utils.book.bean;
/**
 * 此类为自动识别验证码的返回类型，包括绑定会话的Client客户端和返回的识别结果
 *
 */

import com.esaygo.app.utils.book.utils.ConnectionUtils;

import org.apache.http.impl.client.CloseableHttpClient;

import okhttp3.OkHttpClient;

public class CheckVCodeResult {

	private OkHttpClient okHttpClient;
	private String checkVCode;

	private boolean isGetCode;   //是否已获得验证码
	private boolean isLocalCode;  //是否已经打过码
	private boolean isCheckCode;  //是否12306验证过了

	public OkHttpClient getOkHttpClient() {
		return okHttpClient;
	}

	public void setOkHttpClient(OkHttpClient okHttpClient) {
		this.okHttpClient = okHttpClient;
	}

	public String getCheckVCode() {
		return checkVCode;
	}

	public void setCheckVCode(String checkVCode) {
		this.checkVCode = checkVCode;
	}

	public boolean isGetCode() {
		return isGetCode;
	}

	public void setGetCode(boolean getCode) {
		isGetCode = getCode;
	}

	public boolean isLocalCode() {
		return isLocalCode;
	}

	public void setLocalCode(boolean localCode) {
		isLocalCode = localCode;
	}

	public boolean isCheckCode() {
		return isCheckCode;
	}

	public void setCheckCode(boolean checkCode) {
		isCheckCode = checkCode;
	}
}

