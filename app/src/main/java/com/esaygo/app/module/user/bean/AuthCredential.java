package com.esaygo.app.module.user.bean;

public class AuthCredential {
    /**
     * {
     * "user_name":"",
     * "password":"",
     * "verification_code":"469695",
     * "mobile_phone":"18334142052",
     * "login_type":2
     * }
     */

    String user_name;
    String password;
    String verification_code;
    String mobile_phone;
    int login_type;

    public AuthCredential(String mobile_phone,  String verification_code) {
        this.mobile_phone=mobile_phone;
        this.verification_code = verification_code;
        this.login_type=2;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public int getLogin_type() {
        return login_type;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }
}
