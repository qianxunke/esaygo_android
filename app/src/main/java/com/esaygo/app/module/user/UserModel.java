package com.esaygo.app.module.user;


import com.esaygo.app.utils.AppUtils;
import com.esaygo.app.utils.PrefsUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserModel {

    private final static String CurrentUser = "currentUser";
    public static final String ROLES = "roles";
    public static final String USERNAME = "username";
    public static final String TYPE = "type";
    public static final String OWNER = "owner";
    public static final String LANGUAGE = "language";
    public static final String secretKey = "secr";
    public static final String DEFAULT_USER_NAME = "default_user_name";

    public static class User implements Serializable {
        String token;
        String user_id;
        String nike_name;
        String user_name;
        String password; //登陆密码
        int user_stats; //登陆状态
        int identity_card_type;
        String identity_card_no;
        String mobile_phone;
        String user_email;
        String gender;
        int user_point;
        String register_time;
        String modified_time;
        String tran_user_name;     //12306 用户名称
        String tran_user_account;  //12306 用户账号
        String tran_user_pwd;      //12306 用户密码

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getNike_name() {
            return nike_name;
        }

        public void setNike_name(String nike_name) {
            this.nike_name = nike_name;
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

        public int getUser_stats() {
            return user_stats;
        }

        public void setUser_stats(int user_stats) {
            this.user_stats = user_stats;
        }

        public int getIdentity_card_type() {
            return identity_card_type;
        }

        public void setIdentity_card_type(int identity_card_type) {
            this.identity_card_type = identity_card_type;
        }

        public String getIdentity_card_no() {
            return identity_card_no;
        }

        public void setIdentity_card_no(String identity_card_no) {
            this.identity_card_no = identity_card_no;
        }

        public String getMobile_phone() {
            return mobile_phone;
        }

        public void setMobile_phone(String mobile_phone) {
            this.mobile_phone = mobile_phone;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getUser_point() {
            return user_point;
        }

        public void setUser_point(int user_point) {
            this.user_point = user_point;
        }

        public String getRegister_time() {
            return register_time;
        }

        public void setRegister_time(String register_time) {
            this.register_time = register_time;
        }

        public String getModified_time() {
            return modified_time;
        }

        public void setModified_time(String modified_time) {
            this.modified_time = modified_time;
        }

        public String getTran_user_name() {
            return tran_user_name;
        }

        public void setTran_user_name(String tran_user_name) {
            this.tran_user_name = tran_user_name;
        }

        public String getTran_user_account() {
            return tran_user_account;
        }

        public void setTran_user_account(String tran_user_account) {
            this.tran_user_account = tran_user_account;
        }

        public String getTran_user_pwd() {
            return tran_user_pwd;
        }

        public void setTran_user_pwd(String tran_user_pwd) {
            this.tran_user_pwd = tran_user_pwd;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }


    public static User getCurrentUser() {
        return PrefsUtils.getInstance().getObject(CurrentUser, User.class);
    }

    public static boolean insertCurrentUser(User user) {
        try {
            PrefsUtils.getInstance().putObject(CurrentUser, user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean deleteUserInfo() {
        try {
            PrefsUtils.getInstance().putObject(CurrentUser, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
