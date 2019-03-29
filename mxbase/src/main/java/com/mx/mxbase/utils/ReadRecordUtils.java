package com.mx.mxbase.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * 自带阅读器阅读记录
 * Created by xj on 2018/1/8.
 */

public class ReadRecordUtils {

    public static final String BASE_CONTENT_URI = "content://";
    public static final String AUTHORITY = "com.onyx.kreader.statistics.provider";
    public static final String ENDPOINT = "OnyxStatisticsModel";
    public static final Uri CONTENT_URI = Uri.parse(BASE_CONTENT_URI + AUTHORITY + "/" + ENDPOINT);

    private final ContentResolver cr;
//    private List<ReadingStatisticsBean> datas;

    public ReadRecordUtils(ContentResolver cr) {
        this.cr = cr;
    }

    //数据库查询
    public List<ReadingStatisticsBean> queryReadingStatisticsData(String selection) {
        List<ReadingStatisticsBean> list = new ArrayList<ReadingStatisticsBean>();
        Cursor cursor = cr.query(CONTENT_URI, null, selection, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReadingStatisticsBean bean = new ReadingStatisticsBean();
            bean.setType(cursor.getInt(cursor.getColumnIndex("type")));
            bean.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
            bean.setMd5short(cursor.getString(cursor.getColumnIndex("md5short")));
            bean.setPath(cursor.getString(cursor.getColumnIndex("path")));
            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
            bean.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            bean.setEventTime(cursor.getLong(cursor.getColumnIndex("eventTime")));
            bean.setDurationTime(cursor.getLong(cursor.getColumnIndex("durationTime")));
            bean.setScore(cursor.getInt(cursor.getColumnIndex("score")));

            bean.setLastPage(cursor.getInt(cursor.getColumnIndex("lastPage")));
            bean.setCurrPage(cursor.getInt(cursor.getColumnIndex("currPage")));
            list.add(bean);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    //JavaBean
    public class ReadingStatisticsBean {
        private int type ;
        private String md5;
        private String md5short;
        private long eventTime;
        private String path;
        private String name;
        private String author;
        private long durationTime;
        private int score;
        private int lastPage;
        private int currPage;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getMd5short() {
            return md5short;
        }

        public void setMd5short(String md5short) {
            this.md5short = md5short;
        }

        public long getEventTime() {
            return eventTime;
        }

        public void setEventTime(long eventTime) {
            this.eventTime = eventTime;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public long getDurationTime() {
            return durationTime;
        }

        public void setDurationTime(long durationTime) {
            this.durationTime = durationTime;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getLastPage() {
            return lastPage;
        }

        public void setLastPage(int lastPage) {
            this.lastPage = lastPage;
        }

        public int getCurrPage() {
            return currPage;
        }

        public void setCurrPage(int currPage) {
            this.currPage = currPage;
        }
    }

}
