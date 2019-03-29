package com.moxi.haierexams.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moxi.haierexams.R;
import com.moxi.haierexams.model.CourseModel;
import com.moxi.haierexams.model.OptionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/30 0030.
 */
public class SQLUtil {

    private SQLiteDatabase db;

    private static SQLUtil mInstance = null;

    public static SQLUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SQLUtil(context);
        }
        return mInstance;
    }

    public SQLUtil(Context context) {
        AssetsDatabaseManager.initManager(context.getApplicationContext());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        db = mg.getDatabase("base.db");
    }
//    studySectionTables = DataSupport.findAll(StudySectionTable.class);
////        DataSupport.findBySQL("select * from SemesterTable where secId=")
//    semesterTables = DataSupport.where("secId=?", "1").find(SemesterTable.class);

    /**
     * 获取学段
     *
     * @return
     */
    public List<OptionModel> getStudySectionFromDb() {
        String sql = "select sec_id,sec_name from T_StudySection";
        Cursor cursor = db.rawQuery(sql, new String[]{});

        List<OptionModel> optionModels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("sec_id"));
                String name = cursor.getString(cursor.getColumnIndex("sec_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                optionModels.add(cm);
            } while (cursor.moveToNext());
        }
        return optionModels;
    }

    /**
     * 获取学期
     *
     * @param secId 学段id
     * @return
     */
    public List<OptionModel> getSemesterFromDb(String secId) {
        String sql = "select sem_id,sem_name from T_Semester where sem_sec_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{secId});

        List<OptionModel> optionModels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("sem_id"));
                String name = cursor.getString(cursor.getColumnIndex("sem_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                optionModels.add(cm);
            } while (cursor.moveToNext());
        }
        return optionModels;
    }

    /**
     * 获取科目
     *
     * @param semId 学期id（引用semester的id）
     * @return
     */
    public List<CourseModel> getKeMuFromDb(String semId) {
        int[] courseRes = new int[]{R.mipmap.mx_img_course_yw, R.mipmap.mx_img_course_sx, R.mipmap.mx_img_course_yy,
                R.mipmap.mx_img_course_wl, R.mipmap.mx_img_course_hx, R.mipmap.mx_img_course_sw, R.mipmap.mx_img_course_ls
                , R.mipmap.mx_img_course_dl, R.mipmap.mx_img_course_zz};
        int[] coursePressRes = new int[]{R.mipmap.mx_img_course_yw_press, R.mipmap.mx_img_course_sx_press, R.mipmap.mx_img_course_yy_press,
                R.mipmap.mx_img_course_wl_press, R.mipmap.mx_img_course_hx_press, R.mipmap.mx_img_course_sw_press, R.mipmap.mx_img_course_ls_press
                , R.mipmap.mx_img_course_dl_press, R.mipmap.mx_img_course_zz_press};

        Cursor cursor = db.rawQuery("select T_Subject.sub_id,T_Subject.sub_name from T_Subject inner join T_SemesterSubject on T_Subject.sub_id=T_SemesterSubject.ssu_sub_id where T_SemesterSubject.ssu_sem_id=?", new String[]{semId});
        List<CourseModel> listCourse = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            listCourse.clear();
            do {
                int id = cursor.getInt(cursor.getColumnIndex("sub_id"));
                String name = cursor.getString(cursor.getColumnIndex("sub_name"));
                CourseModel cm = new CourseModel();
                cm.setChosen(false);
                if (id > 0 && id < 10) {
                    int i = id - 1;
                    cm.setCourseRes(courseRes[i]);
                    cm.setCoursePressRes(coursePressRes[i]);
                }
                cm.setCourseName(name);
                cm.setId(id);
                listCourse.add(cm);
            } while (cursor.moveToNext());
        }
        return listCourse;
    }

    /**
     * 获取题型
     *
     * @param subjectId      科目id
     * @param chosenPeriodId 学段id
     */
    public List<OptionModel> getTXFromDb(String subjectId, String chosenPeriodId) {
        String sql = "select T_ExerciseType.ext_id,T_ExerciseType.ext_name " +
                "from T_Subject inner join T_StudySection " +
                "inner join T_ExerciseType inner join T_ExerciseTypeStudySectionSubject " +
                " on T_Subject.sub_id=T_ExerciseTypeStudySectionSubject.cts_sub_id" +
                " and T_StudySection.sec_id=T_ExerciseTypeStudySectionSubject.cts_sec_id" +
                " and T_ExerciseType.ext_id=T_ExerciseTypeStudySectionSubject.cts_ext_id" +
                " where T_ExerciseTypeStudySectionSubject.cts_sub_id=? and T_ExerciseTypeStudySectionSubject.cts_sec_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{subjectId, chosenPeriodId});

        List<OptionModel> listTx = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            listTx.clear();
            do {
                int id = cursor.getInt(cursor.getColumnIndex("ext_id"));
                String name = cursor.getString(cursor.getColumnIndex("ext_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                listTx.add(cm);
            } while (cursor.moveToNext());
        }
        return listTx;
    }

    /**
     * 获取难度
     */
    public List<OptionModel> getLDFromDb() {
        String sql = "select dct_id,dct_name from T_Dict where dct_pid=?";
        Cursor cursor = db.rawQuery(sql, new String[]{"100"});

        List<OptionModel> optionModels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("dct_id"));
                String name = cursor.getString(cursor.getColumnIndex("dct_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                optionModels.add(cm);
            } while (cursor.moveToNext());
        }
        return optionModels;
    }

    /**
     * 获取出版社
     *
     * @return
     */
    public List<OptionModel> getCBSFromDb(String secId, String subId) {
        String sql = "select  distinct pub_id, pub_name from T_CourseBook inner join T_Publisher on T_CourseBook.cob_pub_id=T_Publisher.pub_id where cob_sec_id=? and cob_sub_id=?";

//        String sql = "select pub_id,pub_name from T_Publisher";
        Cursor cursor = db.rawQuery(sql, new String[]{secId, subId});

        List<OptionModel> optionModels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("pub_id"));
                String name = cursor.getString(cursor.getColumnIndex("pub_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                optionModels.add(cm);
            } while (cursor.moveToNext());
        }
        return optionModels;
    }

    /**
     * 获取教材
     *
     * @param cos_sem_id 学期id（引用semester的id）
     * @param cob_pub_id 教材所属出版社id
     * @param cob_sec_id 教材所属学段id
     * @param cob_sub_id 教材所属科目id
     * @return
     */
    public List<OptionModel> getCourseBookFromDb(String cos_sem_id, String cob_pub_id, String cob_sec_id, String cob_sub_id) {

        String sql = " select cob_id,cob_name from T_CourseBook inner join T_CourseBookSemester on T_CourseBook.cob_id=T_CourseBookSemester.cos_cob_id where cos_sem_id=? and cob_pub_id=? and cob_sec_id=? and cob_sub_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{cos_sem_id, cob_pub_id, cob_sec_id, cob_sub_id});

        List<OptionModel> optionModels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("cob_id"));
                String name = cursor.getString(cursor.getColumnIndex("cob_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                optionModels.add(cm);
            } while (cursor.moveToNext());
        } else {
            String sql2 = "select cob_id,cob_name from T_CourseBook where cob_pub_id=? and cob_sec_id=? and cob_sub_id=?";
            Cursor cursor2 = db.rawQuery(sql2, new String[]{cob_pub_id, cob_sec_id, cob_sub_id});
            if (cursor2 != null && cursor2.moveToFirst()) {
                do {
                    int id = cursor2.getInt(cursor2.getColumnIndex("cob_id"));
                    String name = cursor2.getString(cursor2.getColumnIndex("cob_name"));
                    OptionModel cm = new OptionModel();
                    cm.setChosen(false);
                    cm.setOptionName(name);
                    cm.setId(id);
                    optionModels.add(cm);
                } while (cursor2.moveToNext());
            }
        }
        return optionModels;
    }

    /**
     * 获取章节名称父目录
     *
     * @param cobId 教材章节所属教材id（引用courseBook的id）
     * @return
     */
    public List<OptionModel> getCourseChapterMenuFromDb(String cobId) {
        String sql = "select cch_id,cch_name from T_CourseChapter where cch_cob_id=? and cch_pid is null";
        Cursor cursor = db.rawQuery(sql, new String[]{cobId});

        List<OptionModel> optionModels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("cch_id"));
                String name = cursor.getString(cursor.getColumnIndex("cch_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                optionModels.add(cm);
            } while (cursor.moveToNext());
        }
        return optionModels;
    }

    /**
     * 获取章节名称子目录
     *
     * @param cobId 教材章节所属教材id（引用courseBook的id）
     * @param cchId 章节的上级章节id
     * @return
     */
    public List<OptionModel> getCourseChapterChildFromDb(String cobId, String cchId) {
        String sql = "select cch_id,cch_name from T_CourseChapter where cch_cob_id=? and cch_pid=?";
        Cursor cursor = db.rawQuery(sql, new String[]{cobId, cchId});

        List<OptionModel> optionModels = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("cch_id"));
                String name = cursor.getString(cursor.getColumnIndex("cch_name"));
                OptionModel cm = new OptionModel();
                cm.setChosen(false);
                cm.setOptionName(name);
                cm.setId(id);
                optionModels.add(cm);
            } while (cursor.moveToNext());
        }
        return optionModels;
    }


    /**
     * 根据章节id查询
     *
     * @param cchId
     * @return 返回书籍相关信息
     */
    public String getBookIdByCchId(String cchId) {
        String cobId = "";
        String sql = " select * from T_CourseBook  JOIN T_StudySection JOIN T_CourseChapter" +
                " where T_CourseBook.cob_sec_id=T_StudySection.sec_id " +
                " and T_CourseBook.cob_id= T_CourseChapter.cch_cob_id " +
                " and T_CourseChapter.cch_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{cchId + ""});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                cobId = cursor.getString(cursor.getColumnIndex("cob_id"));
            } while (cursor.moveToNext());
        }
        return cobId;
    }

//    /**
//     * 根据章节id查询
//     *
//     * @param cchId
//     * @return 返回书籍相关信息
//     */
//    public HashMap<String, Object> getBookIdByCchId(String cchId) {
//        HashMap<String, Object> history = new HashMap<>();
//        String sql = " select cob_id,cob_name,pub_id,pub_name,sec_id,sec_name,sem_id,sem_name,sub_id,sub_name from T_CourseBook  JOIN T_StudySection JOIN T_CourseChapter JOIN T_Publisher JOIN T_CourseBookSemester JOIN T_Semester JOIN T_Subject" +
//                " where T_CourseBook.cob_sec_id=T_StudySection.sec_id and T_Semester.sem_id = T_CourseBookSemester.cos_sem_id" +
//                " and T_CourseBook.cob_id= T_CourseChapter.cch_cob_id and T_CourseBookSemester.cos_cob_id = T_CourseChapter.cch_cob_id" +
//                " and T_Subject.sub_id = T_CourseBook.cob_sub_id and T_CourseBook.cob_pub_id = T_Publisher.pub_id and T_CourseChapter.cch_id=?";
//        Cursor cursor = db.rawQuery(sql, new String[]{cchId + ""});
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                String cobId = cursor.getString(cursor.getColumnIndex("cob_id"));
//                String cob_name = cursor.getString(cursor.getColumnIndex("cob_name"));
//                String pub_id = cursor.getString(cursor.getColumnIndex("pub_id"));
//                String pub_name = cursor.getString(cursor.getColumnIndex("pub_name"));
//                String sec_id = cursor.getString(cursor.getColumnIndex("sec_id"));
//                String sec_name = cursor.getString(cursor.getColumnIndex("sec_name"));
//                String sem_id = cursor.getString(cursor.getColumnIndex("sem_id"));
//                String sem_name = cursor.getString(cursor.getColumnIndex("sem_name"));
//                String sub_id = cursor.getString(cursor.getColumnIndex("sub_id"));
//                String sub_name = cursor.getString(cursor.getColumnIndex("sub_name"));
//
//                history.put("book_id", cobId);
//                history.put("book_name", cob_name);
//                history.put("pub_id", pub_id);
//                history.put("pub_name", pub_name);
//                history.put("sec_id", sec_id);
//                history.put("sec_name", sec_name);
//                history.put("sem_id", sem_id);
//                history.put("sem_name", sem_name);
//            } while (cursor.moveToNext());
//        }
//        return history;
//    }

}
