package com.moxi.haierexams.model;

import java.io.Serializable;

/**
 * Created by Archer on 16/11/14.
 */
public class SyncExamsModel implements Serializable {
    private String cos_sem_id;//学期id
    private String coc_sem_name;
    private String cob_pub_id;//出版社id
    private String cob_pub_name;
    private String cob_sec_id;//学段id
    private String cob_sec_name;
    private String cob_sub_id;//科目id
    private String cob_sub_name;

    public String getCos_sem_id() {
        return cos_sem_id;
    }

    public void setCos_sem_id(String cos_sem_id) {
        this.cos_sem_id = cos_sem_id;
    }

    public String getCoc_sem_name() {
        return coc_sem_name;
    }

    public void setCoc_sem_name(String coc_sem_name) {
        this.coc_sem_name = coc_sem_name;
    }

    public String getCob_pub_id() {
        return cob_pub_id;
    }

    public void setCob_pub_id(String cob_pub_id) {
        this.cob_pub_id = cob_pub_id;
    }

    public String getCob_pub_name() {
        return cob_pub_name;
    }

    public void setCob_pub_name(String cob_pub_name) {
        this.cob_pub_name = cob_pub_name;
    }

    public String getCob_sec_id() {
        return cob_sec_id;
    }

    public void setCob_sec_id(String cob_sec_id) {
        this.cob_sec_id = cob_sec_id;
    }

    public String getCob_sec_name() {
        return cob_sec_name;
    }

    public void setCob_sec_name(String cob_sec_name) {
        this.cob_sec_name = cob_sec_name;
    }

    public String getCob_sub_id() {
        return cob_sub_id;
    }

    public void setCob_sub_id(String cob_sub_id) {
        this.cob_sub_id = cob_sub_id;
    }

    public String getCob_sub_name() {
        return cob_sub_name;
    }

    public void setCob_sub_name(String cob_sub_name) {
        this.cob_sub_name = cob_sub_name;
    }
}
