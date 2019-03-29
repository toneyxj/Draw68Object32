package com.moxi.writeNote.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.moxi.writeNote.adapter.PdfBackground;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moxi.writeNote.adapter.PdfBackground.BORDER_WIDTH;

/**
 * Created by xj on 2017/9/6.
 */

public class PDFCreateRunalbe implements Runnable {
    private String dir;
    private boolean isDelete = false;
    private PDFCreateListener listener;
    private String pdfName;
    private static final String hitnTxt="pdf文件生成进度：";

    /**
     * pdf文件创建过程
     *
     * @param dir      文件夹路径，或者是文件路径
     * @param pdfName  pdf文件名
     * @param isDelete 是否创建文件后删除图片
     * @param listener pdf文件生成监听
     */
    public PDFCreateRunalbe(String dir, String pdfName, boolean isDelete, PDFCreateListener listener) {
        this.dir = dir;
        this.pdfName = pdfName;
        this.isDelete = isDelete;
        this.listener = listener;
    }

    /**
     * pdf文件创建过程
     *
     * @param dir      文件夹路径，或者是文件路径
     * @param pdfName  pdf文件名
     * @param isDelete 是否创建文件后删除图片
     */
    public PDFCreateRunalbe(String dir, String pdfName, boolean isDelete) {
        this.dir = dir;
        this.pdfName = pdfName;
        this.isDelete = isDelete;
    }

    public PDFCreateRunalbe(String dir, String pdfName) {
        this.dir = dir;
        this.pdfName = pdfName;
    }

    public void setListener(PDFCreateListener listener) {
        this.listener = listener;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null == listener) return;
            switch (msg.what) {
                case 0:
                    listener.onFail(msg.obj.toString());
                    break;
                case 1:
//                    listener.onStartCrtPdf((Integer) msg.obj);
                    listener.onProgressHitn(msg.obj.toString());
                    break;
                case 2:
//                    listener.onProgress((Integer) msg.obj);
                    break;
                case 3:
                    listener.onFinish();
                    break;
                case 4:
//                    listener.onPdfEditeSucess(msg.obj.toString());
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void run() {

        List<String> sourceFiles = new ArrayList<String>();

        File file = new File(dir);

        if (!file.exists()) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = "文件不存在";
            handler.sendMessage(msg);
            return;
        }
        File[] files = file.listFiles();
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                File son = files[i];

                if (son.isFile()) {
                    String filename = son.getName();
                    filename = filename.toLowerCase();
                    if (filename.endsWith(".jpg")
                            || filename.endsWith(".png")
                            || filename.endsWith(".jpeg")) {
                    }
                    sourceFiles.add(son.getAbsolutePath());
                }
            }
        } else {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = "请检查Sd卡连接";
            handler.sendMessage(msg);
            return;
        }
        if (sourceFiles.size() == 0) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = "没有可转换的图片文件";
            handler.sendMessage(msg);
            return;
        }

        //检查目标文件路径
        File filesavePath = new File(pdfName);
        if (file.exists()) file.delete();
        if (!file.exists()) {
            try {
                filesavePath.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 0;
                msg.obj = "保存pdf文件路径有误";
                handler.sendMessage(msg);
            }
        }
        Message msg5 = new Message();
        msg5.what = 1;
        msg5.obj = "文件处理中";
        handler.sendMessage(msg5);
        Collections.sort(sourceFiles, new NameComparator());
        //开始转换
        Message msg = new Message();
        msg.what = 1;
        msg.obj = hitnTxt+"1/"+String.valueOf(sourceFiles.size());
//        msg.obj = sourceFiles.size();
        handler.sendMessage(msg);

        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfName));

            PdfBackground event = new PdfBackground();
            writer.setPageEvent(event);

            document.open();
            for (int i = 0; i < sourceFiles.size(); i++) {
                document.newPage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(sourceFiles.get(i));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image img = Image.getInstance(stream.toByteArray());
                //设置图片缩放到A4纸的大小
                img.scaleToFit(PageSize.A4.getWidth() - BORDER_WIDTH * 2, PageSize.A4.getHeight() - BORDER_WIDTH * 2);
//                 设置图片的显示位置（居中）
                img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2);
                document.add(img);

                bitmap.recycle();
                stream.close();

                //处理后通知更新
                Message msg1 = new Message();
                msg1.what = 1;
                msg1.obj = hitnTxt+String.valueOf(i+1)+"/"+String.valueOf(sourceFiles.size());
                handler.sendMessage(msg1);
            }
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Message msg2 = new Message();
            msg2.what = 0;
            msg2.obj = "pdf文件生成出错";
            handler.sendMessage(msg2);
        }

        if (isDelete) {
            Message msg3 = new Message();
            msg3.what = 4;
            msg3.obj = "缓存清理中...";
            handler.sendMessage(msg3);
            deleteFile(file);
        }
        //结束转换
        handler.sendEmptyMessage(3);
    }
    /**
     * 将SD卡文件删除
     *
     * @param file 删除路径
     */
    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            // 如果它是一个目录
            else if (file.isDirectory()) {
                // 声明目录下所有的文件 files[];
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }
    /**
     * 初次顺序排序
     */
    private  class NameComparator implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            String name1 = new File(lhs).getName();
            String name2 = new File(rhs).getName();
            if (name1 == null && name2 == null) {
                return 0;
            } else if (name1 == null && name2 != null) {
                return 1;
            } else if (name1 != null && name2 == null) {
                return -1;
            } else if (name1.equals("") && !name2.equals("")) {
                return 1;
            } else if (!name1.equals("") && name2.equals("")) {
                return -1;
            }
            List<String> list1 = getNumbers(name1);
            List<String> list2 = getNumbers(name2);
            if (list1.size()>0&&list2.size()>0) {
                String[] text1 = name1.split("\\d+");
                String[] text2 = name2.split("\\d+");
                if ((text1.length==0&&text2.length==0)
                        ||(text1.length!=0&&text2.length==0&&text1[0].equals(""))
                        ||(text1.length==0&&text2.length!=0&&text2[0].equals(""))){
                    int num1=Integer.parseInt(list1.get(0));
                    int num2=Integer.parseInt(list2.get(0));
                    if (num1!=num2){
                        return num1>num2?1:-1;
                    }
                }

                int len=text1.length>text2.length?text2.length:text1.length;

                for (int i = 0; i < len; i++) {
                    if (text1[i].equals(text2[i])&&(i<list1.size()&&i<list2.size())){
                        int num1=Integer.parseInt(list1.get(i));
                        int num2=Integer.parseInt(list2.get(i));
                        if (num1!=num2){
                            return num1>num2?1:-1;
                        }
                    }
                }
            }
            int type = name1.compareTo(name2);
            if (type > 0) {
                return 1;
            } else if (type < 0) {
                return -1;
            }
            return 0;
        }
    }

    private   List<String> getNumbers(String str) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }
    /**
     * pdf文件装换
     */
    public interface PDFCreateListener {
        void onFinish();

        void onFail(String msg);

//        void onProgress(int progress);
//
//        void onStartCrtPdf(int totalfiles);
//
//        void onPdfEditeSucess(String hitn);
        void onProgressHitn(String hitn);

    }
}
