package com.moxi.haierexams.model;

/**
 * Created by Archer on 16/10/17.
 */
public class ChoseExamsModel {
    private ExamsDetails examsDetails;
    private int index;
    private String resultKey;

    public String getResultKey() {
        return resultKey;
    }

    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ExamsDetails getExamsDetails() {
        return examsDetails;
    }

    public void setExamsDetails(ExamsDetails examsDetails) {
        this.examsDetails = examsDetails;
    }
}
