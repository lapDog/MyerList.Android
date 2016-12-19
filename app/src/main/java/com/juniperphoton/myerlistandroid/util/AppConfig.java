package com.juniperphoton.myerlistandroid.util;


import com.juniperphoton.myerlistandroid.App;

public class AppConfig {
    public static boolean hasLogined() {
        String access_token = LocalSettingUtil.getString(App.getInstance(), Params.ACCESS_TOKEN_KEY);
        return access_token != null;
    }

    public static String getSid() {
        return LocalSettingUtil.getString(App.getInstance(), Params.SID_KEY);
    }

    public static String getAccessToken() {
        return LocalSettingUtil.getString(App.getInstance(), Params.ACCESS_TOKEN_KEY);
    }

    public static boolean isInOfflineMode() {
        return LocalSettingUtil.checkKey(App.getInstance(), Params.OFFLINE_MODE);
    }
}