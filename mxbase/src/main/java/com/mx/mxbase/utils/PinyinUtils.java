package com.mx.mxbase.utils;


import com.mx.mxbase.model.PrictiseTextBeen;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaolijuan
 * @description:
 * @projectName: PinyinDome
 * @date: 2016-02-18
 * @time: 10:13
 */
public class PinyinUtils {

	/**
	 * 获得汉语拼音首字母
	 *
	 * @param chines 汉字
	 * @return
	 */
	public static String getAlpha(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	/**
	 * 获得全拼
	 * @param str 传入字符窜
	 * @return 返回拼音拼接字符串
	 */
	public static String getPingYinAndTone(String str,String split){
		StringBuilder builder=new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			builder.append(toPinYin(str.charAt(i)));
			if (i!=str.length()-1){
				builder.append(split);
			}
		}
		return builder.toString();
	}
	/**
	 * 获得全拼
	 * @param str 传入字符窜
	 * @return 返回拼音列表
	 */
	public static List<String> getPingYinAndTone(String str){
		List<String> strings=new ArrayList<String>();
		for (int i = 0; i < str.length(); i++) {
			strings.add(toPinYin(str.charAt(i)));
		}
		return strings;
	}
	/**
	 * 获得全拼
	 * @param value 传入字符窜
	 * @return 返回拼音列表
	 */
	public static List<PrictiseTextBeen> getPingYinAndToneList(String value){
		List<PrictiseTextBeen> prictiseTextBeens=new ArrayList<PrictiseTextBeen>();
		for (int i = 0; i < value.length(); i++) {
			char c=value.charAt(i);
			String str=toPinYin(c);
			PrictiseTextBeen been=new PrictiseTextBeen(str,String.valueOf(c));
			prictiseTextBeens.add(been);
		}
		return prictiseTextBeens;
	}
	/**
	 * 返回一个字的拼音
	 * @param hanzi
	 * @return
	 */
	public static String toPinYin(char hanzi){
		HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
		hanyuPinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		hanyuPinyin.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
		hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		String[] pinyinArray=null;
		try {
			//是否在汉字范围内
			if(hanzi>=0x4e00 && hanzi<=0x9fa5){
				pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
			pinyinArray=new String[1];
			pinyinArray[0]="null";
		}
		//将汉字返回
		return pinyinArray[0];
	}
	/**
	 * 将字符串中的中文转化为拼音,英文字符不变
	 *
	 * @param inputString 汉字
	 * @return
	 */
	public static String getPingYin(String inputString) {
		//排除非汉字、字母以及数字
		inputString = inputString.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", "");
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		String output = "";
		if (inputString != null && inputString.length() > 0
				&& !"null".equals(inputString)) {
			char[] input = inputString.trim().toCharArray();
			try {
				for (int i = 0; i < input.length; i++) {
					if (Character.toString(input[i]).matches(
							"[\\u4E00-\\u9FA5]+")) {
						String[] temp = PinyinHelper.toHanyuPinyinStringArray(
								input[i], format);
						output += temp[0];
					} else
						output += Character.toString(input[i]);
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			return "*";
		}
		return output;
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 *
	 * @param chines 汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

}