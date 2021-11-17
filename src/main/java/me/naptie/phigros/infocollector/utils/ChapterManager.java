package me.naptie.phigros.infocollector.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Scanner;

public class ChapterManager {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String str = scanner.nextLine();
		String[] formal = getFormalName(str);
		String shortened = getShortenedName(str);
		System.out.println(shortened + ": " + formal[0] + " - " + formal[1]);
	}

	public static String[] getFormalName(String str) {
		str = str.replace(" - ", "-");
		String[] result = new String[]{"", ""};
		if (str.startsWith("Chapter") || str.startsWith("Side")) {
			if (str.contains("-")) {
				result[0] = str.substring(0, str.indexOf('-'));
				result[1] = str.substring(str.indexOf('-') + 1);
				result[1] = result[1].replace("精选集", "").trim() + " 精选集";
			} else {
				result[0] = str.replaceAll("[\u4E00-\u9FA5]", "").trim();
				result[1] = str.replaceAll("[^\u4E00-\u9FA5]", "");
			}
		} else {
			result[0] = translate(str);
			result[1] = str;
		}
		return result;
	}

	public static String getShortenedName(String str) {
		return str.startsWith("Chapter") || str.startsWith("Side") ? str.replaceAll(" - ", "-").replaceAll("精选集", "").replaceAll("[\u4E00-\u9FA5]+", str.contains("EX") ? translate(str.replaceAll("[^\u4E00-\u9FA5]", "").replace("精选集", "")) : "").trim().replaceAll(" ", "-").toLowerCase() : translate(str).toLowerCase();
	}

	private static String translate(String str) {
		switch (str) {
			case "单曲集": {
				return "Single";
			}
			case "隐藏曲目": {
				return "Hidden";
			}
			case "新手教程": {
				return "Introduction";
			}
			case "已下架曲目": {
				return "Unavailable";
			}
			default: {
				return getChineseShortenedName(str);
			}
		}
	}

	private static String getChineseShortenedName(String str) {
		HanyuPinyinOutputFormat pinyinFormat = new HanyuPinyinOutputFormat();
		pinyinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		pinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		pinyinFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		StringBuilder pinyin = new StringBuilder();
		for (char c : str.toCharArray()) {
			String[] s = null;
			try {
				s = PinyinHelper.toHanyuPinyinStringArray(c, pinyinFormat);
			} catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
				badHanyuPinyinOutputFormatCombination.printStackTrace();
			}
			if (s != null) {
				pinyin.append(s[0]);
			}
		}
		return pinyin.toString();
	}

}
