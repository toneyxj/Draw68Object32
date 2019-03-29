package com.moxi.haierexams.model;

/**
 * Created by Archer on 16/10/14.
 */
public class WrongExamsModel {
    public String subjectId;
    public String subjectName;
    public int count;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
