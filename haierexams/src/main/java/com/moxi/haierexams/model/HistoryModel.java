package com.moxi.haierexams.model;

/**
 * Created by zhengdelong on 16/9/29.
 */

public class HistoryModel {

    private String createtime;
    private int id;
    private int memId;
    private int cchId;
    private long cobId;
    private int type;
    private String title;

    public int getCchId() {
        return cchId;
    }

    public void setCchId(int cchId) {
        this.cchId = cchId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemId() {
        return memId;
    }

    public void setMemId(int memId) {
        this.memId = memId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCobId() {
        return cobId;
    }

    public void setCobId(long cobId) {
        this.cobId = cobId;
    }
}
