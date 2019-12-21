package com.esaygo.app.utils.network.api;


import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdaRequestMap {

    private static Gson gson = new Gson();

    /**
     * 登录接口
     */
    public static String MemberLogin(String login_name, String login_pwd) {
        Map<String, Object> map = new HashMap<>(5);
        map.put("login_name", login_name);
        map.put("login_pwd", login_pwd);
        return gson.toJson(map);
    }





}
