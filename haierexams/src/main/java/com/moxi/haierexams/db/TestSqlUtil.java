package com.moxi.haierexams.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moxi.haierexams.model.ExamsDetails;
import com.mx.mxbase.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Archer on 16/11/10.
 */
public class TestSqlUtil {

    private SQLiteDatabase db;

    private static TestSqlUtil mInstance = null;
    private Context context;

    public static TestSqlUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TestSqlUtil(context);
        }
        return mInstance;
    }

    public TestSqlUtil(Context context) {
        AssetsDatabaseManager.initManager(context.getApplicationContext());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        db = mg.getDatabase("paper.db");
        this.context = context;
    }

    /**
     * 获取历史试卷
     *
     * @param kemuid 科目id
     * @return
     */
    public List<HashMap<String, Object>> getExamsAll(String kemuid, String sec_id, int page, int pageSize) {
        List<HashMap<String, Object>> result = new ArrayList<>();
        String sql = "select pap_id,pap_name,pap_sub_name,pap_sub_id from T_Paper where 1=1 and pap_sec_id=" + sec_id;
        if (!kemuid.equals("")) {
            sql += " and pap_sub_id=" + kemuid;
        }
        Cursor cursor = db.rawQuery(sql, new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, Object> hashMap = new HashMap<>();
                int pap_id = cursor.getInt(cursor.getColumnIndex("pap_id"));
                String pap_name = cursor.getString(cursor.getColumnIndex("pap_name"));
                String pap_sub_name = cursor.getString(cursor.getColumnIndex("pap_sub_name"));
                int pap_sub_id = cursor.getInt(cursor.getColumnIndex("pap_sub_id"));
                hashMap.put("pap_id", pap_id);
                hashMap.put("pap_name", pap_name);
                hashMap.put("pap_sub_name", pap_sub_name);
                hashMap.put("pap_sub_id", pap_sub_id);
                result.add(hashMap);
            } while (cursor.moveToNext());
        }
        if (result.size() > 0) {
            List<List<HashMap<String, Object>>> aaa = ListUtils.splitList(result, pageSize);
            result = aaa.get(page);
        }
        return result;
    }

    /**
     * @param paperId
     * @return
     */
    public List<ExamsDetails> getExamsDetails(String paperId) {
        List<ExamsDetails> result = new ArrayList<>();
        String sql = "select * from T_PaperSubject where psj_pap_id=" + paperId;
        Cursor cursor = db.rawQuery(sql, new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ExamsDetails examsDetails = new ExamsDetails();

                int psj_id = cursor.getInt(cursor.getColumnIndex("psj_id"));
                String psj_title = cursor.getString(cursor.getColumnIndex("psj_title"));
                String psj_option = cursor.getString(cursor.getColumnIndex("psj_option"));
                String psj_answer = cursor.getString(cursor.getColumnIndex("psj_answer"));
                String psj_analysis = cursor.getString(cursor.getColumnIndex("psj_analysis"));
                String psj_updatetime = cursor.getString(cursor.getColumnIndex("psj_updatetime"));
                int psj_state = cursor.getInt(cursor.getColumnIndex("psj_state"));
                int psj_type = cursor.getInt(cursor.getColumnIndex("psj_type"));

                examsDetails.setAnalysis(psj_analysis);
                examsDetails.setAnswer(psj_answer);
                examsDetails.setOption(psj_option);
                examsDetails.setResult("");
                examsDetails.setId(psj_id);
                examsDetails.setTitle(psj_title);
                examsDetails.setType(psj_type);

                result.add(examsDetails);
            } while (cursor.moveToNext());
        }
        return result;
    }
}
