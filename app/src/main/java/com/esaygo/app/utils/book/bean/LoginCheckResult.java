package com.esaygo.app.utils.book.bean;

import com.esaygo.app.utils.book.utils.ConnectionUtils;

import org.apache.http.impl.client.CloseableHttpClient;

import okhttp3.OkHttpClient;

public class LoginCheckResult {
	private OkHttpClient okHttpClient;// 绑定会话的客户端
	private boolean checkUser; // 用户是否可以登录
	private boolean getToken; // 是否获取到Token
	private boolean checkToken; // Token是否通过检查
	private boolean login; // 是否完成登录
	private boolean codeCheck;//验证码是否已经验证

	private boolean isGetCode;   //是否已获得验证码
	private boolean isLocalCode;  //是否已经打过码

	private String newapptk; // 检查Token发送的识别码
	private String apptk;// 登陆成功的识别码
	private String username;// 登陆成功后获取的用户名

	public String getApptk() {
		return apptk;
	}

	public void setApptk(String apptk) {
		this.apptk = apptk;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNewapptk() {
		return newapptk;
	}

	public void setNewapptk(String newapptk) {
		this.newapptk = newapptk;
	}

	public boolean isCheckUser() {
		return checkUser;
	}

	public void setCheckUser(boolean checkUser) {
		this.checkUser = checkUser;
	}

	public boolean isGetToken() {
		return getToken;
	}

	public void setGetToken(boolean getToken) {
		this.getToken = getToken;
	}

	public boolean isCheckToken() {
		return checkToken;
	}

	public void setCheckToken(boolean checkToken) {
		this.checkToken = checkToken;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public boolean isCodeCheck() {
		return codeCheck;
	}

	public void setCodeCheck(boolean codeCheck) {
		this.codeCheck = codeCheck;
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

	public OkHttpClient getOkHttpClient() {
		return okHttpClient;
	}

	public void setOkHttpClient(OkHttpClient okHttpClient) {
		this.okHttpClient = okHttpClient;
	}
}
