package com.moxi.handwritinglibs.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 手指事件关闭与开启
 * Created by xj on 2018/4/11.
 */

public class FingerUtils {

    public static void openOrOff(boolean open) {
//        if (open) {
//            writeStringValueToFile("/sys/devices/platform/onyx_misc.0/tp_disable", "0");
//        } else {
//            writeStringValueToFile("/sys/devices/platform/onyx_misc.0/tp_disable", "1");
//        }
    }

    private static boolean writeStringValueToFile(final String path, String value) {
        FileOutputStream fout = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                String command = "touch " + path;
                do_exec(command);
            }
            fout = new FileOutputStream(file);
            byte[] bytes = value.getBytes();
            fout.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private static void do_exec(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
