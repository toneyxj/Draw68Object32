package com.moxi.writeNote.myScript;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.writeNote.listener.ChangeToTextListener;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.IEditorListener2;

import org.json.JSONException;

import java.io.IOException;

public class MyScriptManager implements IEditorListener2 {
    private MyScriptService myScriptService;
    private ChangeToTextListener listener;
    private Editor editor;
    private Context context;
    private Handler handler = new Handler();

    public MyScriptManager(Context context, ChangeToTextListener listener) {
        this.context = context;
        this.listener = listener;

    }

    public void initMyScript(View view) {
        if (editor == null) {
            myScriptService = new MyScriptService();
            int width = view.getWidth();
            int height = view.getHeight();
            myScriptService.init(width, height, context);
            editor = myScriptService.getEditor();
            editor.addListener(this);
        }
    }

    public void changeToText(WritePageData data) {
        if (myScriptService == null) return;

        if (listener!=null){
            listener.onChangeStart();
        }
        if (data==null||((data.mainLines==null||data.mainLines.size()==0)&&(data.drawMiddleLines==null||data.drawMiddleLines.size()==0))){
            listener.onChangeFaile("");
            return;
        }
        myScriptService.addCoordinate(data);
    }

    @Override
    public void selectionChanged(Editor editor, String[] strings) {

    }

    @Override
    public void activeBlockChanged(Editor editor, String s) {

    }

    @Override
    public void partChanging(Editor editor, ContentPart contentPart, ContentPart contentPart1) {

    }

    @Override
    public void partChanged(Editor editor) {

    }

    @Override
    public void contentChanged(Editor editor, String[] strings) {
        if (listener == null) return;
        try {
            final String s = myScriptService.change();
            if (listener == null) return;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onChangeSucess(s);
                    clear();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            listener.onChangeFaile(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onChangeFaile(e.getMessage());
        }
    }

    @Override
    public void onError(Editor editor, final String s, final String s1) {
        if (listener == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onChangeFaile("onError:" + s + "\n:" + s1);
            }
        });

    }

    private void clear() {
        if (editor != null)
            editor.clear();
    }

    public void remove() {
        if (editor != null)
            editor.removeListener(this);
        if (myScriptService != null) {
            myScriptService.close();
        }
        handler.removeCallbacksAndMessages(null);
    }
}
