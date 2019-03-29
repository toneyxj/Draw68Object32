// Copyright MyScript. All rights reserved.

package com.myscript.iink.uireferenceimplementation;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class definition used for Gson parsing
 */
public class JiixDefinitions {
    public static class Padding {
        public float left;
        public float right;
    }

    public static class Word {

        public static String LABEL_FIELDNAME = "label";
        public String label;
        public String[] candidates;
        public Item[] items;
        public int first_char, last_char;
        public Style style;
        public  BoundingBox bounding_box;

        @Override
        public String toString() {
            return "Word{" +
                    "label='" + label + '\'' +
                    ", candidates=" + Arrays.toString(candidates) +
                    ", items=" + Arrays.toString(items) +
                    ", first_char=" + first_char +
                    ", last_char=" + last_char +
                    ", style=" + style +
                    ", bounding_box=" + bounding_box +
                    '}';
        }
    }

    public static class Item {
        public float[] X, Y, F;
        public int[] T;

        @Override
        public String toString() {
            return "Item{" +
                    "X=" + Arrays.toString(X) +
                    ", Y=" + Arrays.toString(Y) +
                    ", F=" + Arrays.toString(F) +
                    ", T=" + Arrays.toString(T) +
                    '}';
        }
    }

    public static class Span {
        public int first_char, last_char;
        private String style;
        private Style styleBin;

        public Style getStyleBin() {
            if (styleBin == null) {
                styleBin = getStyleBin(style);
            }
            return styleBin;
        }

        private Style getStyleBin(String styleStr) {
            if (styleStr == null) {
                return null;
            }
            String[] split = styleStr.split(";");
            if (split == null || split.length == 0) {
                return null;
            }
            Map<String, String> map = new HashMap<>();
            for (String s : split) {
                String[] split1 = s.split(":");
                if (split1 == null || split1.length < 2) {
                    continue;
                }
                map.put(split1[0].trim(), split1[1].trim());
            }
            Style style=new Style();
            style._myscript_pen_brush=  map.get("_myscript_pen_brush");
            style.color=map.get("color");
            String myscript_pen_width = map.get("_myscript_pen_width");
            if(myscript_pen_width!=null){
                style._myscript_pen_width=Float.parseFloat(myscript_pen_width);
            }
            return style;
        }

        @Override
        public String toString() {
            return "Span{" +
                    "first_char=" + first_char +
                    ", last_char=" + last_char +
                    ", style='" + style + '\'' +
                    ", styleBin=" + getStyleBin() +
                    '}';
        }
    }

    public static class Style {
        public String color, _myscript_pen_brush;
        public float _myscript_pen_width;

        @Override
        public String toString() {
            return "Style{" +
                    "color='" + color + '\'' +
                    ", _myscript_pen_brush='" + _myscript_pen_brush + '\'' +
                    ", _myscript_pen_width=" + _myscript_pen_width +
                    '}';
        }
    }


    public static class BoundingBox {
      public   float x,y,width,height;

        @Override
        public String toString() {
            return "BoundingBox{" +
                    "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
    public static class Result {
        public static String WORDS_FIELDNAME = "words";
        public Word[] words;
        public Span[] spans;





        @Override
        public String toString() {
            return "Result{" +
                    "words=" + Arrays.toString(words) +
                    ", spans=" + Arrays.toString(spans) +
                    '}';
        }
    }

}
