package com.moxi.writeNote.utils;

import java.io.Serializable;

/**
 * Created by King on 2017/12/15.
 */

public class LockPassWord implements Serializable {
    private String password;
    private long updateTime;
    private String macId;
    private int isOpen;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }
}
