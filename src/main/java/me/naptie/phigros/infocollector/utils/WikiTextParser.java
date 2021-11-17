package me.naptie.phigros.infocollector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiTextParser {

	public static void main(String[] args) throws IOException {
		parse(HttpManager.readJsonFromUrl("https://api.neonmc.top:6400/wikitext.json").getJSONObject("parse").getJSONObject("wikitext").getString("*")).toJSONString();
	}

	public static JSONObject parse(String wikiText) {
		JSONObject result = new JSONObject(true);
		String[] chapterName = ChapterManager.getFormalName(wikiText.split("==")[1]);
		JSONObject name = new JSONObject(true);
		name.put("subtitle", f(chapterName[0]));
		name.put("title", f(chapterName[1]));
		result.put("name", name);
		JSONArray songs = new JSONArray();
		int i = wikiText.indexOf("{| class="), next = 0;
		while (i < wikiText.length()) {
			int j = i + 2;
			while (!(wikiText.charAt(j - 2) == '\n' && wikiText.charAt(j - 1) == '|' && wikiText.charAt(j) == '}')) {
				j++;
			}
			next = j + 1;
			String songText = wikiText.substring(i, next);
			JSONObject song = new JSONObject(true);
			i = songText.indexOf(" |") + 3;
			if (songText.contains("原属章节")) {
				i--;
			}
			j = i;
			while (songText.charAt(j) != '\n') {
				j++;
			}
//			System.out.printf("=== Song #%d ===\nName: %s\nLevels:", songs.size() + 1, songText.substring(i, j));
			song.put("name", f(songText.substring(i, j).replaceAll("<small>*.+</small>", "").replaceAll("<(S*?)[^>]*>.*?|</.*?>", "")));
			if (songText.substring(i, j).contains("small")) {
				Matcher matcher = Pattern.compile("<small>*.+</small>").matcher(songText.substring(i, j));
				if (matcher.find()) {
					song.put("extra", f(matcher.group().replaceAll("<(S*?)[^>]*>.*?|</.*?>", "")));
				}
			}
			JSONArray charts = new JSONArray();
			i = songText.indexOf("难度");
			j = i + 6;
			while (songText.charAt(j) != '\n') {
				while (!Character.isUpperCase(songText.charAt(j))) {
					j++;
				}
				StringBuilder level = new StringBuilder();
				JSONObject chart = new JSONObject(true);
				while (Character.isUpperCase(songText.charAt(j)) || Character.isLowerCase(songText.charAt(j))) {
					level.append(songText.charAt(j));
					j++;
				}
//				System.out.print(" " + level.toString());
				chart.put("level", f(level.toString()));
				charts.add(chart);
				if (song.getString("name").equals("望影の方舟Six")) {
					JSONObject errorChart = (JSONObject) chart.clone();
					errorChart.put("level", f(level.toString()) + "_Error");
					charts.add(errorChart);
				}
				while (!Character.isUpperCase(songText.charAt(j))) {
					j++;
					if (songText.charAt(j) == '\n') {
						break;
					}
				}
			}
			i = songText.indexOf("File:") + 5;
			j = i;
			while (songText.charAt(j) != '|') {
				j++;
			}
//			System.out.println("\nIllustration: " + "https://zh.moegirl.org.cn/Special:FilePath/" + songText.substring(i, j));
			song.put("illustration", f("https://zh.moegirl.org.cn/Special:FilePath/" + songText.substring(i, j)));
//			System.out.print("Difficulties1:");
			i = songText.indexOf("等级") + 6;
			j = i;
			for (int k = 0; k < charts.size(); k++) {
				while (songText.charAt(j) == '|') {
					j++;
				}
				i = j;
				while (songText.charAt(j) != '|' && songText.charAt(j) != '\n') {
					j++;
				}
//				System.out.print(" " + songText.substring(i, j));
				try {
					charts.getJSONObject(k).put("difficulty1", Integer.parseInt(songText.substring(i, j)));
				} catch (NumberFormatException e) {
					charts.getJSONObject(k).put("difficulty1", f(songText.substring(i, j)));
				}
				if (k < charts.size() - 1 && charts.getJSONObject(k + 1).getString("level").contains("Error") && charts.getJSONObject(k + 1).getString("level").startsWith(charts.getJSONObject(k).getString("level"))) {
					try {
						charts.getJSONObject(k + 1).put("difficulty1", Integer.parseInt(songText.substring(i, j)));
					} catch (NumberFormatException e) {
						charts.getJSONObject(k + 1).put("difficulty1", f(songText.substring(i, j)));
					}
					k++;
				}
			}
			if (songText.contains("定数")) {
				i = songText.indexOf("定数") + 6;
//			System.out.print("\nDifficulties2:");
				j = i;
				for (int k = 0; k < charts.size(); k++) {
					while (songText.charAt(j) == '|') {
						j++;
					}
					i = j;
					while (songText.charAt(j) != '|' && songText.charAt(j) != '\n') {
						j++;
					}
//					System.out.print(" " + songText.substring(i, j));
					try {
						charts.getJSONObject(k).put("difficulty2", Double.parseDouble(songText.substring(i, j)));
					} catch (NumberFormatException e) {
						charts.getJSONObject(k).put("difficulty2", f(songText.substring(i, j)));
					}
					if (k < charts.size() - 1 && charts.getJSONObject(k + 1).getString("level").contains("Error") && charts.getJSONObject(k + 1).getString("level").startsWith(charts.getJSONObject(k).getString("level"))) {
						try {
							charts.getJSONObject(k + 1).put("difficulty2", Double.parseDouble(songText.substring(i, j)));
						} catch (NumberFormatException e) {
							charts.getJSONObject(k + 1).put("difficulty2", f(songText.substring(i, j)));
						}
						k++;
					}
				}
			} else {
				for (int k = 0; k < charts.size(); k++) {
					charts.getJSONObject(k).put("difficulty2", "?");
				}
			}
//			System.out.print("\nNumber of notes:");
			i = songText.indexOf("物量");
			j = i;
			for (int k = 0; k < charts.size(); k++) {
				while (!Character.isDigit(songText.charAt(j))) {
					j++;
				}
				int notes = 0;
				while (Character.isDigit(songText.charAt(j))) {
					notes = notes * 10 + songText.charAt(j) - '0';
					j++;
				}
//				System.out.print(" " + notes);
				charts.getJSONObject(k).put("notes", notes);
				if (k < charts.size() - 1 && charts.getJSONObject(k + 1).getString("level").contains("Error") && charts.getJSONObject(k + 1).getString("level").startsWith(charts.getJSONObject(k).getString("level"))) {
					charts.getJSONObject(k + 1).put("notes", notes + (charts.getJSONObject(k).getString("level").equals("EZ") ? 1 : (charts.getJSONObject(k).getString("level").equals("HD") ? 2 : 3)));
					k++;
				}
			}
//			System.out.print("\nCharters:");
			j = i = songText.indexOf("谱师") + 8;
			while (songText.charAt(j) != '\n') {
				j++;
			}
			if (songText.substring(i, j).contains("colspan=")) {
				while (songText.charAt(i) != '|') {
					i++;
				}
				i += 2;
			}
			String charters = songText.substring(i, j);
			for (String charter : charters.split("<br>")) {
				int p = charter.indexOf('（'), q = charter.indexOf('）');
				if (p == -1) {
					p = charter.indexOf('(');
				}
				if (q == -1) {
					q = charter.indexOf(')');
				}
				if (p != q) {
					String[] levels = charter.substring(p + 1, q).split("/");
					for (String level : levels) {
						for (int k = 0; k < charts.size(); k++) {
							if (charts.getJSONObject(k).getString("level").equals(level)) {
//								System.out.printf(" - %s [%s]", charter.substring(0, p), level);
								charts.getJSONObject(k).put("charter", f(charter.substring(0, p)));
								if (k < charts.size() - 1 && charts.getJSONObject(k + 1).getString("level").contains("Error") && charts.getJSONObject(k + 1).getString("level").startsWith(charts.getJSONObject(k).getString("level"))) {
									charts.getJSONObject(k + 1).put("charter", charts.getJSONObject(k).getString("charter"));
								}
								break;
							}
						}
					}
				} else {
					for (int k = 0; k < charts.size(); k++) {
//						System.out.printf(" - %s [%s]", charter, charts.getJSONObject(k).getString("level"));
						charts.getJSONObject(k).put("charter", f(charter));
					}
				}
			}
			song.put("charts", charts);
			if (chapterName[0].equals("Introduction")) {
				song.put("chapter", name);
			} else {
				i = songText.indexOf("属章节");
				j = i;
				while (songText.charAt(j) != '|') {
					j++;
				}
				j += 3;
				if (songText.contains("原属章节")) {
					j--;
				}
				i = j;
				while (songText.charAt(j) != '\n') {
					j++;
				}
				JSONObject obj = new JSONObject(true);
				String str = songText.substring(i, j).replaceAll("（已删除）", "");
				obj.put("subtitle", f(ChapterManager.getFormalName(str)[0]));
				obj.put("title", f(ChapterManager.getFormalName(str)[1]));
				song.put("chapter", obj);
			}
			j = i = songText.indexOf("曲师") + 8;
			while (songText.charAt(j) != '\n') {
				j++;
			}
			if (songText.substring(i, j).contains("colspan=")) {
				while (songText.charAt(i) != '|') {
					i++;
				}
				i += 2;
			}
//			System.out.println("\nComposer: " + songText.substring(i, j));
			song.put("composer", f(songText.substring(i, j)));
			j = songText.indexOf("BPM");
			while (songText.charAt(j) != '|') {
				j++;
			}
			j += 3;
			i = j;
			while (songText.charAt(j) != '\n') {
				j++;
			}
//			System.out.println("BPM: " + bpm);
			song.put("bpm", songText.substring(i, j));
			j = i = songText.indexOf("画师") + 8;
			while (songText.charAt(j) != '\n') {
				j++;
			}
			if (songText.substring(i, j).contains("colspan=")) {
				while (songText.charAt(i) != '|') {
					i++;
				}
				i += 2;
			}
//			System.out.println("Illustrator: " + songText.substring(i, j));
			song.put("illustrator", f(songText.substring(i, j)));
			j = songText.indexOf("长度");
			while (songText.charAt(j) != '|') {
				j++;
			}
			j += 3;
			i = j;
			while (songText.charAt(j) != '\n') {
				j++;
			}
//			System.out.println("Length: " + songText.substring(i, j));
			song.put("length", f(songText.substring(i, j)));
			songs.add(song);
			wikiText = wikiText.substring(next);
			i = wikiText.indexOf("{| class=");
			if (i == -1) {
				break;
			}
		}
		result.put("songs", songs);
		return result;
	}

	private static String f(String str) {
		return str.replaceAll("-\\{|}-", "").replaceAll("\\{\\{lj\\||}}", "").trim();
	}

}
