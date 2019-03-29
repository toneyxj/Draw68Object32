package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.writeNote.config.ConfigInfor;
import com.moxi.writeNote.utils.MD5;
import com.mx.mxbase.utils.StringUtils;

public class ExternalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_external);
        String name=getIntent().getStringExtra("name");
        String path=getIntent().getStringExtra("path");
        WritPadModel model;
        if (StringUtils.isNull(name)||StringUtils.isNull(path)) {
            /**
             * 添加界面
             */
             model = new WritPadModel(-1, "新增界面", "", -1, ConfigInfor.rootDir, 0, "0", null, 0l,"");
        }else {
            String saveCode =ConfigInfor.rootDir+"/"+MD5.stringToMD5(path);
            model= WritePadUtils.getInstance().getWritPadModel(saveCode,0);
            if (model==null){
                model = new WritPadModel(-1,pathSpil(name) , saveCode, 1, ConfigInfor.rootDir, 0, "0", null, 0l,"");
            }
        }

        Intent intent = new Intent(ExternalActivity.this, NewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        intent.putExtras(bundle);
        startActivity(intent);

        this.finish();
    }
    private String pathSpil(String filName){
        String str=filName.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }
}
