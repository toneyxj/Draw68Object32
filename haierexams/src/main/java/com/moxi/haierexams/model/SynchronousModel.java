package com.moxi.haierexams.model;

/**
 * Created by zhengdelong on 16/9/30.
 */

public class SynchronousModel {

    private int parentId;
    private int id;
    private String title;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
