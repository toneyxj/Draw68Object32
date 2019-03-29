package com.moxi.haierexams.model;

/**
 * Created by Archer on 16/10/12.
 */
public class ChoseResultModel {
    private String ChoseValue;
    private int XQID;
    private String XQDesc;
    private int periodId;
    private String peroidDesc;

    public String getChoseValue() {
        return ChoseValue;
    }

    public void setChoseValue(String choseValue) {
        ChoseValue = choseValue;
    }

    public int getXQID() {
        return XQID;
    }

    public void setXQID(int XQID) {
        this.XQID = XQID;
    }

    public String getXQDesc() {
        return XQDesc;
    }

    public void setXQDesc(String XQDesc) {
        this.XQDesc = XQDesc;
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public String getPeroidDesc() {
        return peroidDesc;
    }

    public void setPeroidDesc(String peroidDesc) {
        this.peroidDesc = peroidDesc;
    }
}
