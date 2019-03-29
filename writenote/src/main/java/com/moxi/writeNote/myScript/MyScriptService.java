package com.moxi.writeNote.myScript;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.google.gson.Gson;
import com.moxi.handwritinglibs.model.WriteModel.WLine;
import com.moxi.handwritinglibs.model.WriteModel.WMoreLine;
import com.moxi.handwritinglibs.model.WriteModel.WPoint;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.writeNote.myScript.exception.NullPointerExceptionEngine;
import com.myscript.iink.Configuration;
import com.myscript.iink.ContentPackage;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.Engine;
import com.myscript.iink.MimeType;
import com.myscript.iink.ParameterSet;
import com.myscript.iink.PointerEvent;
import com.myscript.iink.PointerEventType;
import com.myscript.iink.PointerType;
import com.myscript.iink.Renderer;
import com.myscript.iink.graphics.Transform;
import com.myscript.iink.uireferenceimplementation.FontMetricsProvider;
import com.myscript.iink.uireferenceimplementation.JiixDefinitions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/8/13 0013.
 */

public class MyScriptService {
    private Engine engine;
    private Editor editor;
    private Gson gson;
    private ContentPackage package1;
    private ContentPart part;

    public MyScriptService() {
        gson = new Gson();
    }

    public void init(float dpiX, float dpiY, Context context) throws NullPointerExceptionEngine {
        engine = IInkApplication.getEngine();
        if (engine == null) {
            throw new NullPointerExceptionEngine();
        }

        Configuration conf = engine.getConfiguration();
        String confDir = "zip://" + context.getPackageCodePath() + "!/assets/conf";
        conf.setStringArray("configuration-manager.search-path", new String[]{confDir});
        String tempDir = context.getFilesDir().getPath() + File.separator + "tmp";
        conf.setString("content-package.temp-folder", tempDir);

        conf.setString("lang", "zh_CN");


        conf.setNumber("text.margin.top", 0);
        conf.setNumber("text.margin.left", 0);
        conf.setNumber("text.margin.right", 0);

        // Configure the engine to disable guides (recommended)
        //配置引擎以禁用指南（推荐）
        engine.getConfiguration().setBoolean("text.guides.enable", false);

        // Create a renderer with a null render target
        //使用null渲染目标创建渲染器
//        float dpiX = 300;
//        float dpiY = 800;
        Renderer renderer = engine.createRenderer(dpiX, dpiY, null);

        // Create the editor
        //创建编辑器
        editor = engine.createEditor(renderer);

        // The editor requires a font metrics provider and a view size *before* calling setPart()
        //在调用setPart（）之前，编辑器需要字体度量提供程序和视图大小*
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Map<String, Typeface> typefaceMap = new HashMap<>();
        editor.setFontMetricsProvider(new FontMetricsProvider(displayMetrics, typefaceMap));
        editor.setViewSize((int) dpiX, (int) dpiY);

        Renderer renderer1 = editor.getRenderer();
        Transform viewTransform = renderer1.getViewTransform();
        // Create a temporary package and part for the editor to work with
        //创建一个临时包和部件供编辑器使用
        String packageName = "File1.iink";
        File file = new File(context.getFilesDir(), packageName);
        package1 = null;
        try {
            package1 = engine.createPackage(file);
            part = package1.createPart("Text");//"Text Document", "Text", "Diagram", "Math", and "Drawing")
            editor.setPart(part);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String change() throws IOException, JSONException {
        JSONObject jo = getJiixJsonObject();
        String label = "";

        if (jo.has("label")) {
            label = jo.getString("label");
        }

        return label;
    }

    @NonNull
    public JSONObject getJiixJsonObject() throws IOException, JSONException {
        ParameterSet parameterSet = editor.getEngine().createParameterSet();
     parameterSet.setBoolean("export.jiix.strokes", false);
        String jiixString = editor.export_(editor.getRootBlock(), MimeType.JIIX, parameterSet);

        return new JSONObject(jiixString);
    }

    private final int maxLeng=100;
    private List<PointerEvent> pointerEvents;

    public synchronized void addCoordinate(final WritePageData data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int is=data.drawMiddleLines.size();
                for (int i = 0; i < is; i++) {
                    WMoreLine mline=data.drawMiddleLines.get(i);
                    if (mline.status==0){
                        int i1s=mline.MoreLines.size();
                        for (int i1 = 0; i1 < i1s; i1++) {
                            addCoordinate(mline.MoreLines.get(i1));
                        }
                    }
                }
                int size=data.mainLines.size();
                for (int i = 0; i <size ; i++) {
                    addCoordinate(data.mainLines.get(i));
                }
            }
        }).start();

    }

    private void addCoordinate(WLine line){
        if (line==null||line.getPoints().size()<3)return;
        List<WPoint> lines=line.getPoints();
        int size=lines.size();
        for (int i = 0; i <size; i++) {
            WPoint p=lines.get(i);
            if (i==0){
                addCoordinate( MotionEvent.ACTION_DOWN,p);
            }else if (i==(size-1)){
                addCoordinate( MotionEvent.ACTION_UP,p);
            }else {
                addCoordinate( MotionEvent.ACTION_MOVE,p);
            }
        }
    }
    private void addCoordinate(int action, WPoint point) {
        if(pointerEvents==null){
            pointerEvents=new ArrayList<>();
        }
        float x = point.x;
        float y = point.y;
        final long NO_TIMESTAMP = -1;
        final float NO_PRESSURE = 0.0f;
        final int NO_POINTER_ID = -1;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pointerEvents.clear();
                pointerEvents.add(new PointerEvent(PointerEventType.DOWN, x, y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, NO_POINTER_ID));
                break;
            case MotionEvent.ACTION_MOVE:
                pointerEvents.add(new PointerEvent(PointerEventType.MOVE, x, y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, NO_POINTER_ID));
                break;
            case MotionEvent.ACTION_UP:
                pointerEvents.add(new PointerEvent(PointerEventType.UP, x, y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, NO_POINTER_ID));
                if(pointerEvents.size()>0&&pointerEvents.size()<maxLeng){
                    PointerEvent[] pes=new PointerEvent[pointerEvents.size()];
                    pointerEvents.toArray(pes);
                    editor.pointerEvents(pes, false);
                }
                break;
        }

    }

    public void clear() {
        editor.clear();
    }

    public Editor getEditor() {
        return editor;
    }

    public void close() {
        if (editor != null)
            editor.close();
        if (editor != null)
            engine.close();
        if (package1 != null)
            package1.close();
        if (part != null)
            part.close();
        engine = null;

    }


    public JiixDefinitions.Result getJiixDefinitionsResult() throws IOException, JSONException {
        JSONObject jiixJsonObject = getJiixJsonObject();
        if (jiixJsonObject == null) {
            return null;
        }
        String jiixString = jiixJsonObject.toString()
                .replace("-myscript-pen-width", "_myscript_pen_width")
                .replace("-myscript-pen-brush", "_myscript_pen_brush")
                .replace("first-char", "first_char")
                .replace("bounding-box", "bounding_box")
                .replace("last-char", "last_char");
        JiixDefinitions.Result jdr = gson.fromJson(jiixString, JiixDefinitions.Result.class);
        JiixDefinitions.Word[] words = jdr.words;
        JiixDefinitions.Span[] spans = jdr.spans;
        if (spans != null && spans.length > 0 && words != null && words.length > 0) {
            for (JiixDefinitions.Word w : words) {
                int first_char = w.first_char;
                int last_char = w.last_char;
                JiixDefinitions.Item[] items = w.items;
                for (JiixDefinitions.Span s : spans) {
                    int first_char1 = s.first_char;
                    int last_char1 = s.last_char;
                    if (first_char >= first_char1 && last_char <= last_char1) {
                        w.style = s.getStyleBin();
                        break;
                    }
                }
            }
        }
        return jdr;
    }

    public void setColor(String color) {
        String style = "-myscript-pen-brush: FountainPen;color: " + color + ";-myscript-pen-width: 1.5";
        editor.setPenStyle(style);
    }

    public Transform getTransform() {
        Transform viewTransform = editor.getRenderer().getViewTransform();
        return viewTransform;
    }

    public MimeType[] getExportType() {
        MimeType[] supportedExportMimeTypes = editor.getSupportedExportMimeTypes(editor.getRootBlock());
        return supportedExportMimeTypes;
    }

    public String export(MimeType mimeType) throws IOException {
        String str = editor.export_(editor.getRootBlock(), mimeType);
        return str;
    }


}
