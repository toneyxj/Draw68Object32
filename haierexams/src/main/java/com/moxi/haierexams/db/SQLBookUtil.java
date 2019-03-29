package com.moxi.haierexams.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.moxi.haierexams.model.ExamsDetails;
import com.moxi.haierexams.model.ExamsDetailsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/30 0030.
 */
public class SQLBookUtil {

    private SQLiteDatabase db;
    private AssetsDatabaseManager mg;

    private static SQLBookUtil mInstance = null;

    public static SQLBookUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SQLBookUtil(context);
        }
        return mInstance;
    }

    public SQLBookUtil(Context context) {
        AssetsDatabaseManager.initManager(context.getApplicationContext());
        mg = AssetsDatabaseManager.getManager();
    }

    public String getExamsDetails(String bookId, String coeCchId) {
        String fileName = bookId + ".db";
        db = mg.getDatabase(DownDbService.localDBPath, fileName);
        if (db == null)
            return "";
        String sql = "select coe_cch_id,coe_difficulty,coe_id,coe_state,coe_type, coe_analysis,coe_answer,coe_knowledge,coe_option,coe_title,coe_updatetime from T_CourseExercise where coe_cch_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{coeCchId});
        List<ExamsDetails> result = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            ExamsDetails cm;
            do {
                int cchId = cursor.getInt(cursor.getColumnIndex("coe_cch_id"));
                int difficulty = cursor.getInt(cursor.getColumnIndex("coe_difficulty"));
                int id = cursor.getInt(cursor.getColumnIndex("coe_id"));
                int state = cursor.getInt(cursor.getColumnIndex("coe_state"));
                int type = cursor.getInt(cursor.getColumnIndex("coe_type"));
                String analysis = cursor.getString(cursor.getColumnIndex("coe_analysis"));
                String answer = cursor.getString(cursor.getColumnIndex("coe_answer"));
                String knowledge = cursor.getString(cursor.getColumnIndex("coe_knowledge"));
                String option = cursor.getString(cursor.getColumnIndex("coe_option"));
                String title = cursor.getString(cursor.getColumnIndex("coe_title"));
                String updatetime = cursor.getString(cursor.getColumnIndex("coe_updatetime"));

                cm = new ExamsDetails();
                cm.setCchId(cchId);
                cm.setDifficulty(difficulty);
                cm.setId(id);
                cm.setState(state);
                cm.setType(type);
                cm.setAnalysis(AES.decode(analysis));
                cm.setAnswer(AES.decode(answer));
                cm.setKnowledge(AES.decode(knowledge));
                cm.setOption(AES.decode(option));
                cm.setTitle(AES.decode(title));
                cm.setUpdatetime(getTime(Long.parseLong(updatetime)));
                result.add(cm);
            } while (cursor.moveToNext());
        }
        mg.closeDatabase(fileName);
        return toJsonExamsDetailsModel(result);
    }

    private String toJsonExamsDetailsModel(List<ExamsDetails> result) {
        Gson gson = new Gson();
        ExamsDetailsModel model = new ExamsDetailsModel();
        model.setResult(result);
        return gson.toJson(model);
    }

    private String getTime(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sDateFormat.format(time);
    }
}


