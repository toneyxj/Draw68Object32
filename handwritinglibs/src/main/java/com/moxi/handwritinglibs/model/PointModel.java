package com.moxi.handwritinglibs.model;

/**
 * Created by xj on 2017/7/3.
 */

public class PointModel {
    public boolean isDrawLine;
    public int moveEvent;
    public float eventX;
    public float eventY;

    public PointModel(boolean isDrawLine,int moveEvent, float eventX, float eventY) {
        this.isDrawLine=isDrawLine;
        this.moveEvent = moveEvent;
        this.eventX = eventX;
        this.eventY = eventY;
    }
}
