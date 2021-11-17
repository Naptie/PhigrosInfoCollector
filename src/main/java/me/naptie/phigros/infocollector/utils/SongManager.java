package me.naptie.phigros.infocollector.utils;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import fr.free.nrw.jakaroma.KanaToRomaji;
import me.naptie.phigros.infocollector.exceptions.UnparsableException;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SongManager {

	public static void main(String[] args) {
		System.out.println(getShortenedName("Winter↑Cube↓ (Original mix)"));
	}

	public static String getShortenedName(String str) {
		Matcher matcher = Pattern.compile("[\u0800-\u9FA5]+").matcher(str);
		StringBuilder result = new StringBuilder(str.replaceAll("[\u0800-\u9FA5]+", "%s"));
		try {
			while (matcher.find()) {
//				System.out.println("Found JP pattern: " + matcher.group());
				result = new StringBuilder(result.toString().replaceFirst("%s", getJapaneseShortenedName(matcher.group())));
			}
		} catch (UnparsableException e) {
			result = new StringBuilder(str);
		}
		matcher = Pattern.compile("[\u4E00-\u9FA5]+").matcher(result.toString());
		result = new StringBuilder(result.toString().replaceAll("[\u4E00-\u9FA5]+", "%s"));
		while (matcher.find()) {
//			System.out.println("Found CN pattern: " + matcher.group());
			result = new StringBuilder(result.toString().replaceFirst("%s", getChineseShortenedName(matcher.group())));
		}
		return format((new KanaToRomaji()).convert(KanaManager.convertToKata(result.toString())));
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

	private static String getJapaneseShortenedName(String str) throws UnparsableException {
		if (KanaManager.isKana(str)) {
			return KanaManager.convertToRomaji(str);
		}
		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(str);
		StringBuilder kana = new StringBuilder();
//		System.out.printf("JP Tokens for %s:\n", str);
		for (Token token : tokens) {
			if (KanaManager.isKatakana(token.getSurface())) {
				kana.append((new KanaToRomaji()).convert(token.getSurface()));
				continue;
			}
			kana.append(token.getReading());
			if (token.getReading().equals("*")) {
//				System.out.println(token.getSurface() + " is not Japanese");
				throw new UnparsableException();
			}
//			System.out.println("Token for " + token.getSurface() + ": " + token.getAllFeatures());
		}
		return kana.toString();
	}

	private static String normalize(String str) {
		if (str.equalsIgnoreCase("Ποσειδών")) {
			return "poseidon";
		}
		str = str.replaceAll("ä", "a").replaceAll("ρ", "p");
		Matcher matcher = Pattern.compile("[-]+|[_]+|[.]+|[(]|[)]|[A-z]+|[0-9]+|[&]+").matcher(str);
		StringBuilder result = new StringBuilder();
		while (matcher.find()) {
			result.append(matcher.group());
		}
		return result.toString();
	}

	private static String format(String str) {
		return normalize(str.toLowerCase().replace("-", "").replace("。", "-").replace("，", "-").replace("、", "-").replace("♪", "").replaceAll("[\\\\/:*?\"< >|]", "-"));
	}

}
