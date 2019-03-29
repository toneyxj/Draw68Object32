package com.moxi.haierexams.model;


import com.mx.mxbase.model.BaseModel;

import java.io.Serializable;

/**
 * Created by Archer on 16/8/9.
 */
public class CourseModel extends BaseModel implements Serializable {
    private int id;
    private String courseName;
    private String desc;
    private int courseRes;
    private int coursePressRes;
    private boolean chosen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isChosen() {
        return chosen;
    }

    public int getCourseRes() {
        return courseRes;
    }

    public void setCourseRes(int courseRes) {
        this.courseRes = courseRes;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public int getCoursePressRes() {
        return coursePressRes;
    }

    public void setCoursePressRes(int coursePressRes) {
        this.coursePressRes = coursePressRes;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
