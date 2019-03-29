package com.moxi.haierexams.model;

import com.mx.mxbase.model.BaseModel;

import java.util.List;

/**
 * Created by Archer on 16/9/29.
 */
public class ExamsDetailsModel extends BaseModel {
    private List<ExamsDetails> result;

    public List<ExamsDetails> getResult() {
        return result;
    }

    public void setResult(List<ExamsDetails> result) {
        this.result = result;
    }
}
