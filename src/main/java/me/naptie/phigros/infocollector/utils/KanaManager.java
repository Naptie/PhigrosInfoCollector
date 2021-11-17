package me.naptie.phigros.infocollector.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KanaManager {

	private static final Map<String, String> kataDic, hiraDic, hiraToKata = new HashMap<>();

	static {
		Map<String, String> a = new HashMap<>();

		a.put("ア", "a");
		a.put("イ", "i");
		a.put("ウ", "u");
		a.put("エ", "e");
		a.put("オ", "o");
		a.put("カ", "ka");
		a.put("キ", "ki");
		a.put("ク", "ku");
		a.put("ケ", "ke");
		a.put("コ", "ko");
		a.put("サ", "sa");
		a.put("シ", "shi");
		a.put("ス", "su");
		a.put("セ", "se");
		a.put("ソ", "so");
		a.put("タ", "ta");
		a.put("チ", "chi");
		a.put("ツ", "tsu");
		a.put("テ", "te");
		a.put("ト", "to");
		a.put("ナ", "na");
		a.put("ニ", "ni");
		a.put("ヌ", "nu");
		a.put("ネ", "ne");
		a.put("ノ", "no");
		a.put("ハ", "ha");
		a.put("ヒ", "hi");
		a.put("フ", "fu");
		a.put("ヘ", "he");
		a.put("ホ", "ho");
		a.put("マ", "ma");
		a.put("ミ", "mi");
		a.put("ム", "mu");
		a.put("メ", "me");
		a.put("モ", "mo");
		a.put("ヤ", "ya");
		a.put("ユ", "yu");
		a.put("ヨ", "yo");
		a.put("ラ", "ra");
		a.put("リ", "ri");
		a.put("ル", "ru");
		a.put("レ", "re");
		a.put("ロ", "ro");
		a.put("ワ", "wa");
		a.put("ヲ", "wo");
		a.put("ン", "n");
		a.put("ガ", "ga");
		a.put("ギ", "gi");
		a.put("グ", "gu");
		a.put("ゲ", "ge");
		a.put("ゴ", "go");
		a.put("ザ", "za");
		a.put("ジ", "ji");
		a.put("ズ", "zu");
		a.put("ゼ", "ze");
		a.put("ゾ", "zo");
		a.put("ダ", "da");
		a.put("ヂ", "ji");
		a.put("ヅ", "zu");
		a.put("デ", "de");
		a.put("ド", "do");
		a.put("バ", "ba");
		a.put("ビ", "bi");
		a.put("ブ", "bu");
		a.put("ベ", "be");
		a.put("ボ", "bo");
		a.put("パ", "pa");
		a.put("ピ", "pi");
		a.put("プ", "pu");
		a.put("ペ", "pe");
		a.put("ポ", "po");
		a.put("キャ", "kya");
		a.put("キュ", "kyu");
		a.put("キョ", "kyo");
		a.put("シャ", "sha");
		a.put("シュ", "shu");
		a.put("ショ", "sho");
		a.put("チャ", "cha");
		a.put("チュ", "chu");
		a.put("チョ", "cho");
		a.put("ニャ", "nya");
		a.put("ニュ", "nyu");
		a.put("ニョ", "nyo");
		a.put("ヒャ", "hya");
		a.put("ヒュ", "hyu");
		a.put("ヒョ", "hyo");
		a.put("リャ", "rya");
		a.put("リュ", "ryu");
		a.put("リョ", "ryo");
		a.put("ギャ", "gya");
		a.put("ギュ", "gyu");
		a.put("ギョ", "gyo");
		a.put("ジャ", "ja");
		a.put("ジュ", "ju");
		a.put("ジョ", "jo");
		a.put("ティ", "ti");
		a.put("ディ", "di");
		a.put("ツィ", "tsi");
		a.put("ヂャ", "dya");
		a.put("ヂュ", "dyu");
		a.put("ヂョ", "dyo");
		a.put("ビャ", "bya");
		a.put("ビュ", "byu");
		a.put("ビョ", "byo");
		a.put("ピャ", "pya");
		a.put("ピュ", "pyu");
		a.put("ピョ", "pyo");
		a.put("ー", "-");
		a.put("チェ", "che");
		a.put("フィ", "fi");
		a.put("フェ", "fe");
		a.put("ウィ", "wi");
		a.put("ウェ", "we");
		a.put("ヴィ", "vi");
		a.put("ヴェ", "ve");

		a.put("「", "\"");
		a.put("」", "\"");
		a.put("。", ".");

		kataDic = Collections.unmodifiableMap(a);

		a = new HashMap<>();

		a.put("あ", "a");
		a.put("い", "i");
		a.put("う", "u");
		a.put("え", "e");
		a.put("お", "o");
		a.put("か", "ka");
		a.put("き", "ki");
		a.put("く", "ku");
		a.put("け", "ke");
		a.put("こ", "ko");
		a.put("さ", "sa");
		a.put("し", "shi");
		a.put("す", "su");
		a.put("せ", "se");
		a.put("そ", "so");
		a.put("た", "ta");
		a.put("ち", "chi");
		a.put("つ", "tsu");
		a.put("て", "te");
		a.put("と", "to");
		a.put("な", "na");
		a.put("に", "ni");
		a.put("ぬ", "nu");
		a.put("ね", "ne");
		a.put("の", "no");
		a.put("は", "ha");
		a.put("ひ", "hi");
		a.put("ふ", "fu");
		a.put("へ", "he");
		a.put("ほ", "ho");
		a.put("ま", "ma");
		a.put("み", "mi");
		a.put("む", "mu");
		a.put("め", "me");
		a.put("も", "mo");
		a.put("や", "ya");
		a.put("ゆ", "yu");
		a.put("よ", "yo");
		a.put("ら", "ra");
		a.put("り", "ri");
		a.put("る", "ru");
		a.put("れ", "re");
		a.put("ろ", "ro");
		a.put("わ", "wa");
		a.put("を", "wo");
		a.put("ん", "n");
		a.put("が", "ga");
		a.put("ぎ", "gi");
		a.put("ぐ", "gu");
		a.put("げ", "ge");
		a.put("ご", "go");
		a.put("ざ", "za");
		a.put("じ", "ji");
		a.put("ず", "zu");
		a.put("ぜ", "ze");
		a.put("ぞ", "zo");
		a.put("だ", "da");
		a.put("ぢ", "ji");
		a.put("づ", "zu");
		a.put("で", "de");
		a.put("ど", "do");
		a.put("ば", "ba");
		a.put("び", "bi");
		a.put("ぶ", "bu");
		a.put("べ", "be");
		a.put("ぼ", "bo");
		a.put("ぱ", "pa");
		a.put("ぴ", "pi");
		a.put("ぷ", "pu");
		a.put("ぺ", "pe");
		a.put("ぽ", "po");
		a.put("きゃ", "kya");
		a.put("きゅ", "kyu");
		a.put("きょ", "kyo");
		a.put("しゃ", "sha");
		a.put("しゅ", "shu");
		a.put("しょ", "sho");
		a.put("ちゃ", "cha");
		a.put("ちゅ", "chu");
		a.put("ちょ", "cho");
		a.put("にゃ", "nya");
		a.put("にゅ", "nyu");
		a.put("にょ", "nyo");
		a.put("ひゃ", "hya");
		a.put("ひゅ", "hyu");
		a.put("ひょ", "hyo");
		a.put("りゃ", "rya");
		a.put("りゅ", "ryu");
		a.put("りょ", "ryo");
		a.put("ぎゃ", "gya");
		a.put("ぎゅ", "gyu");
		a.put("ぎょ", "gyo");
		a.put("じゃ", "ja");
		a.put("じゅ", "ju");
		a.put("じょ", "jo");
		a.put("てぃ", "ti");
		a.put("でぃ", "di");
		a.put("つぃ", "tsi");
		a.put("ぢゃ", "dya");
		a.put("ぢゅ", "dyu");
		a.put("ぢょ", "dyo");
		a.put("びゃ", "bya");
		a.put("びゅ", "byu");
		a.put("びょ", "byo");
		a.put("ぴゃ", "pya");
		a.put("ぴゅ", "pyu");
		a.put("ぴょ", "pyo");
		a.put("ー", "-");
		a.put("ちぇ", "che");
		a.put("ふぃ", "fi");
		a.put("ふぇ", "fe");
		a.put("うぃ", "wi");
		a.put("うぇ", "we");
		a.put("ヴぃ", "vi");
		a.put("ヴぇ", "ve");

		a.put("「", "\"");
		a.put("」", "\"");
		a.put("。", ".");

		hiraDic = Collections.unmodifiableMap(a);
	}

	private static String find(String str) {
		if (kataDic.containsKey(str)) {
			return kataDic.get(str);
		}
		if (hiraDic.containsKey(str)) {
			return hiraDic.get(str);
		}
		return null;
	}

	public static String convertToRomaji(String str) {
//		System.out.println("Looking up for " + str);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (i < str.length() - 1) {
				String a = find(String.valueOf(str.charAt(i)) + str.charAt(i + 1));
				if (a != null) {
					result.append(a);
//					System.out.println("Found " + a);
					i++;
					continue;
				}
			}
			result.append(find(String.valueOf(str.charAt(i))));
//			System.out.println("Found " + find(String.valueOf(str.charAt(i))));
		}
		return result.toString();
	}

	public static boolean isKana(String str) {
		return isHiragana(str) || isKatakana(str);
	}

	public static boolean isHiragana(String str) {
		return str.matches("[\\u3040-\\u309F]+");
	}

	public static boolean isKatakana(String str) {
		return str.matches("[\\u30A0-\\u30FFー]+");
	}

	public static String convertToKata(String str) {
		if (hiraToKata.isEmpty()) {
			for (String hira : hiraDic.keySet()) {
				if (!isHiragana(hira)) {
					continue;
				}
				for (String kata : kataDic.keySet()) {
					if (hiraDic.get(hira).equals(kataDic.get(kata))) {
						hiraToKata.put(hira, kata);
					}
				}
			}
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (isHiragana(String.valueOf(str.charAt(i)))) {
				if (i < str.length() - 1) {
					String hira = String.valueOf(str.charAt(i)) + str.charAt(i + 1);
					if (hiraToKata.containsKey(hira)) {
						result.append(hiraToKata.get(hira));
						i++;
						continue;
					}
				}
				result.append(hiraToKata.get(String.valueOf(str.charAt(i))));
			} else {
				result.append(str.charAt(i));
			}
		}
		return result.toString();
	}

}
