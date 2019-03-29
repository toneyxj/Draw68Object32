package com.moxi.writeNote.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.writeNote.Model.SimpleWriteModel;
import com.moxi.writeNote.listener.PasteListener;
import com.mx.mxbase.constant.APPLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 粘贴异步线程
 * Created by 夏君 on 2017/2/28.
 */

public class  PasteAsy extends AsyncTask<String, Void, String> {
    private PasteListener listener;
    public String pasteFile;

    /**
     * @param pasteFile  文件夹路径
     * @param listener 粘贴监听
     */
    public PasteAsy(String pasteFile, PasteListener listener) {
        this.listener=listener;
        this.pasteFile=pasteFile;
    }
    private void updateName(String parentCode,String saveCode,long id){
        if (!WritePadUtils.getInstance().updataSaveCode(parentCode,saveCode,id)){
            updateName(parentCode,saveCode,id);
        }
    }

    @Override
    protected String doInBackground(String... arg0) {
        if (MoveFileConfig.getInstance().getParentCode()==null){
            return "选择文件异常无法移动";
        }
        List<SimpleWriteModel> simpleWriteModels=MoveFileConfig.getInstance().getSimpleWriteModels();
        APPLog.e(simpleWriteModels.toString());
        //文件夹
        List<SimpleWriteModel> midsks=new ArrayList<SimpleWriteModel>();
        //文件
        List<SimpleWriteModel> files=new ArrayList<SimpleWriteModel>();

        for (int i = 0; i < simpleWriteModels.size(); i++) {
            SimpleWriteModel model=simpleWriteModels.get(i);
            boolean isNoMove=model.isFloder==0&&pasteFile.length()>=model.saveCode.length()&&pasteFile.substring(0,model.saveCode.length()).equals(model.saveCode);
            if (isNoMove||WritePadUtils.getInstance().judgeDataExist(pasteFile,model.name,model.isFloder)!=-1){
                //已存在文件
                if (model.isFloder==1){
                    //文件
                    files.add(model);
                }else if (model.isFloder==0){
                    //文件夹
                    midsks.add(model);
                }
            }else {
                String sourceSaveCode=model.parentCode;
                Log.e("paste-sourceSaveCode",sourceSaveCode);
                int index=sourceSaveCode.length();
                //可转移文件
                if(model.isFloder==0) {
                    //文件路径改变
                    List<WritPadModel> lists=WritePadUtils.getInstance().getMirkAllFiles(model.saveCode);
                    for (int j = 0; j < lists.size(); j++) {
                        WritPadModel modelj=lists.get(j);
                        String parentCode= pasteFile+modelj.parentCode.substring(index);
                        String saveCode= pasteFile+modelj.saveCode.substring(index);
                        updateName(parentCode,saveCode,modelj.id);
                    }
                    //改变自己路径
                    updateName(pasteFile,pasteFile+"/"+model.name,model.id);
                }else {
                    //转换所有文件信息
                    List<WritPadModel> lists=WritePadUtils.getInstance().getListFiles(model.saveCode);
                    for (WritPadModel model1:lists){
                        updateName(pasteFile,pasteFile+"/"+model.name,model1.id);
                    }
                }

            }
        }
        String result="";
        if (midsks.size()==0&&files.size()==0){
            result="sucess";
        }
//        else if((midsks.size()+files.size())==simpleWriteModels.size()){
//            result="您选择的移动文件，与目标文件夹文件有冲突，无法进行移动";
//        }
        else {
            StringBuilder builder=new StringBuilder();
            for (int i = 0; i < midsks.size(); i++) {
                if (i==0){
                    builder.append("文件夹：");
                }else {
                    builder.append("；");
                }
                builder.append(midsks.get(i).name);
            }
            if (midsks.size()!=0)builder.append("\n");
            for (int i = 0; i < files.size(); i++) {
                if (i==0){
                    builder.append("文件：");
                }else {
                    builder.append("；");
                }
                builder.append(files.get(i).name);
            }
            builder.append("\n与目标文件夹有冲突或者重复，无法移动");
             result=builder.toString();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (null==listener)return;
        //设置回调
        boolean is=result.equals("sucess");
        listener.onPaste(is,result);
    }
}
