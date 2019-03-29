package com.moxi.writeNote.Model.loging;

/**
 * Created by Administrator on 2016/11/16.
 */

public class LoginUserData {
    private int pubIdNum;

    private String userPubId;

    private String systemDate;

    private String uniqueKey;

    private String currentDate;

    private LoginUser user;

    private String token;

    public int getPubIdNum() {
        return pubIdNum;
    }

    public void setPubIdNum(int pubIdNum) {
        this.pubIdNum = pubIdNum;
    }

    public String getUserPubId() {
        return userPubId;
    }

    public void setUserPubId(String userPubId) {
        this.userPubId = userPubId;
    }

    public String getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(String systemDate) {
        this.systemDate = systemDate;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public LoginUser getUser() {
        return user;
    }

    public void setUser(LoginUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
