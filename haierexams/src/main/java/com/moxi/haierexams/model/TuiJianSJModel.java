package com.moxi.haierexams.model;

import com.mx.mxbase.model.BaseModel;

import java.util.List;

/**
 * Created by Archer on 16/11/10.
 */
public class TuiJianSJModel extends BaseModel {

    private AAAA result;

    public AAAA getResult() {
        return result;
    }

    public void setResult(AAAA result) {
        this.result = result;
    }

    public class AAAA {
        private List<Paper> list;

        public List<Paper> getList() {
            return list;
        }

        public void setList(List<Paper> list) {
            this.list = list;
        }
    }

    /**
     * 临时用的试卷
     *
     * @author zhoumao
     */
    public class Paper {

        private String id;
        private String name;
        private String semId;
        private String subId;
        private String secId;
        private String subName;
        private String cprId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSemId() {
            return semId;
        }

        public void setSemId(String semId) {
            this.semId = semId;
        }

        public String getSubId() {
            return subId;
        }

        public void setSubId(String subId) {
            this.subId = subId;
        }

        public String getSecId() {
            return secId;
        }

        public void setSecId(String secId) {
            this.secId = secId;
        }

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
        }

        public String getCprId() {
            return cprId;
        }

        public void setCprId(String cprId) {
            this.cprId = cprId;
        }
    }
}
