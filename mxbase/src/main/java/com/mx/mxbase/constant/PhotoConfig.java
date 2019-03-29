package com.mx.mxbase.constant;


import com.mx.mxbase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2017/7/25.
 */

public class PhotoConfig {
    public static int getSources(String fileNmae) {
        String prefix = fileNmae.substring(fileNmae.lastIndexOf(".") + 1);
        prefix = prefix.toLowerCase();
        if (prefix.equals("txt")) {
            return R.mipmap.file_txt;
        } else if (prefix.equals("pdf")) {
            return R.mipmap.file_pdf;
        } else if (prefix.equals("epub")) {
            return R.mipmap.file_epub;
        } else if (prefix.equals("fb2")) {
            return R.mipmap.file_fb2;
        } else if (prefix.equals("chm")) {
            return R.mipmap.file_chm;
        } else if (prefix.equals("doc")) {
            return R.mipmap.file_doc;
        } else if (prefix.equals("docx")) {
            return R.mipmap.file_doc;
        } else if (prefix.equals("cbr")) {
            return R.mipmap.file_doc;
        } else if (prefix.equals("cbz")) {
            return R.mipmap.file_doc;
        } else if (prefix.equals("djvu")) {
            return R.mipmap.file_doc;
        } else if (prefix.equals("odt")) {
            return R.mipmap.file_doc;
        } else if (prefix.equals("rtf")) {
            return R.mipmap.file_doc;
        } else {
            return R.mipmap.file_mobi;
        }
    }
    public static List<String>  getAllFileType(){
         List<String> fileTypes= new ArrayList<String>();
        fileTypes.add("pdf");
        fileTypes.add("mobi");
        fileTypes.add("txt");
        fileTypes.add("epub");
        fileTypes.add("fb2");
//        fileTypes.add("chm");
        fileTypes.add("doc");
//        fileTypes.add("docx");
//        fileTypes.add("cbr");
//        fileTypes.add("cbz");
//        fileTypes.add("djvu");
//        fileTypes.add("odt");
        fileTypes.add("rtf");
        return  fileTypes;
    }
}
