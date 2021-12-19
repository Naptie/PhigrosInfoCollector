package me.naptie.phigros.infocollector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.naptie.phigros.infocollector.objects.ChartProvided;
import me.naptie.phigros.infocollector.objects.ChartRequired;
import me.naptie.phigros.infocollector.objects.MySQL;
import me.naptie.phigros.infocollector.utils.ChapterManager;
import me.naptie.phigros.infocollector.utils.HttpManager;
import me.naptie.phigros.infocollector.utils.SongManager;
import me.naptie.phigros.infocollector.utils.WikiTextParser;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Main {

	public static Map<Integer, File> chapterMap = new HashMap<>();

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		Scanner scanner = new Scanner(System.in);
		String command;
		do {
			System.out.print(">>> ");
			command = scanner.next().toLowerCase();
			switch (command) {
				case "crawl": {
					crawl(scanner);
					break;
				}
				case "summarize": {
					summarize(scanner.next());
					break;
				}
				case "copy": {
					command = scanner.next().toLowerCase();
					switch (command.charAt(0)) {
						case 'i': {
							getIllustration(new File(scanner.next()));
							break;
						}
						case 'c': {
							copyCharts(new File(scanner.next()));
							break;
						}
						case 'a':
						case 'm': {
							getAudio(new File(scanner.next()));
							break;
						}
					}
					break;
				}
				case "get": {
					command = scanner.next().toLowerCase();
					//noinspection SwitchStatementWithTooFewBranches
					switch (command.charAt(0)) {
						case 'c': {
							getCharts(new File(scanner.next()), scanner);
							break;
						}
					}
					break;
				}
				case "check": {
					check(new File(scanner.next()));
					break;
				}
				case "transfer": {
					transfer(scanner);
					break;
				}
			}
		} while (!command.equalsIgnoreCase("stop") && !command.equalsIgnoreCase("end"));
	}

	private static void transfer(Scanner scanner) {
		System.out.println("Enter MySQL credentials (address, port, username, password, database):");
		String address = scanner.next(), port = scanner.next(), username = scanner.next(), password = scanner.next(), database = scanner.next();
		MySQL mysql = new MySQL(address, Integer.parseInt(port), username, password, database);
		while (true) {
			System.out.print("- What would you like to transfer? A (c)hapter or a (s)ong? Or you would like to (a)bort me? (c / s / a)\n- ");
			String op = scanner.next().toLowerCase();
			if (op.charAt(0) == 'c') {
				JSONArray chapters = JSON.parseObject(readJSONFile(new File("phigros", "info.json"))).getJSONArray("chapters");
				System.out.println("- Sure. We'll transfer a chapter. Which chapter should we transfer? (0 ~ " + (chapters.size() - 1) + ")");
				for (int i = 0; i < chapters.size(); i++) {
					JSONObject chapter = chapters.getJSONObject(i);
					System.out.printf("%2d. %s - %s\n", i, chapter.getJSONObject("name").getString("subtitle"), chapter.getJSONObject("name").getString("title"));
				}
				System.out.print("- ");
				int choice = scanner.nextInt();
				JSONObject chapter = chapters.getJSONObject(choice);
				String subtitle = chapter.getJSONObject("name").getString("subtitle");
				String title = chapter.getJSONObject("name").getString("title");
				String path = chapter.getString("loc");
				int chapterIndex, songBegin;
				try {
					chapterIndex = mysql.countRows("phizone_chapter") + 1;
					songBegin = mysql.countRows("phizone_song");
				} catch (SQLException e) {
					System.out.println("- Failed. Caught an SQL Exception!");
					e.printStackTrace();
					break;
				}
				System.out.printf("- Sure. We'll transfer %s - %s. This chapter contains the following songs:\n", subtitle, title);
				JSONArray songs = JSON.parseObject(readJSONFile(new File("phigros" + File.separator + chapter.getString("loc"), "info.json"))).getJSONArray("songs");
				for (int i = 0; i < songs.size(); i++) {
					System.out.printf("  %2d. %s\n", i, songs.getJSONObject(i).getString("name"));
				}
				System.out.print("  Would you like to add them in the (d)efault order or (a)nother order, or (m)anually add them one by one? (d / a / m)\n- ");
				int originalCnt = chapter.getIntValue("songs");
				String songIndexes, opt = scanner.next().toLowerCase();
				if (opt.charAt(0) == 'a') {
					System.out.print("- Please input an order. (integers separated with commas)\n- ");
					String orderStr = "";
					long cnt = -1;
					while (originalCnt != cnt) {
						orderStr = scanner.next();
						cnt = Arrays.stream(orderStr.split(",")).count();
						if (cnt != originalCnt) {
							System.out.println("- Your order contains " + cnt + " numbers rather than " + originalCnt + " numbers. Please input your order again.\n- ");
						}
					}
					List<Integer> order = toInt(orderStr.split(","));
					for (int i = 1; i <= order.size(); i++) {
						JSONObject song = JSON.parseObject(readJSONFile(new File("phigros" + File.separator + path + songs.getJSONObject(order.get(i - 1)).getString("loc"), "info.json")));
						String songPath = path + songs.getJSONObject(order.get(i - 1)).getString("loc");
						System.out.println("- Transferring " + song.getString("name") + "...");
						transferSong(song, songPath, mysql, chapterIndex, songBegin + i, scanner);
					}
					songIndexes = toString(songBegin + 1, songBegin + originalCnt);
				} else if (opt.charAt(0) == 'd') {
					for (int i = 1; i <= originalCnt; i++) {
						JSONObject song = JSON.parseObject(readJSONFile(new File("phigros" + File.separator + path + songs.getJSONObject(i - 1).getString("loc"), "info.json")));
						String songPath = path + songs.getJSONObject(i - 1).getString("loc");
						System.out.println("- Transferring " + song.getString("name") + "...");
						transferSong(song, songPath, mysql, chapterIndex, songBegin + i, scanner);
					}
					songIndexes = toString(songBegin + 1, songBegin + originalCnt);
				} else {
					System.out.print("- Please input indexes of songs that makes up this chapter. (integers separated with commas)\n- ");
					songIndexes = scanner.next();
					long cnt = Arrays.stream(songIndexes.split(",")).count();
					System.out.println("- If I'm right, there'll be " + cnt + " song(s) included, " + (cnt == originalCnt ? "which is right the value recorded in the chapter info. " : "which is different from the value " + originalCnt + " recorded in the chapter info."));
				}
				System.out.println("Inserting chapter into the phizone_chapter table...");
				try {
					if (mysql.insert("phizone_chapter", new String[]{"subtitle", "title", "songs", "path"}, new String[]{subtitle, title, songIndexes, path})) {
						System.out.println("  Succeeded!");
					} else {
						System.out.println("  Failed!");
					}
				} catch (SQLException e) {
					System.out.println("  Failed. Caught an SQL Exception!");
					e.printStackTrace();
				}
			} else if (op.charAt(0) == 's') {
				System.out.print("- Sure. We'll transfer a song. What's the name for it?\n- ");
				String name = scanner.next();
				System.out.print("- Looking into chapters for " + name + "...");
				List<Song> result = new ArrayList<>();
				JSONArray chapters = JSON.parseObject(readJSONFile(new File("phigros", "info.json"))).getJSONArray("chapters");
				for (int i = 0; i < chapters.size(); i++) {
					JSONObject chapter = chapters.getJSONObject(i);
					if (!chapter.getBooleanValue("direct")) {
						continue;
					}
					JSONArray songs = JSON.parseObject(readJSONFile(new File("phigros" + File.separator + chapter.getString("loc"), "info.json"))).getJSONArray("songs");
					for (int j = 0; j < songs.size(); j++) {
						JSONObject song = songs.getJSONObject(j);
						if (song.getString("name").equalsIgnoreCase(name) || song.getString("loc").replaceAll("/", "").equalsIgnoreCase(name)) {
							result.add(new Song(song.getString("name"), (new File("phigros" + File.separator + chapter.getString("loc"), song.getString("loc"))).getAbsolutePath().split("phigros" + File.separator)[1]));
						}
					}
				}
				for (Song s : result) {
					System.out.println("  Found " + s.name + " at " + s.path);
				}
				Song target = result.get(0);
				if (result.size() > 1) {
					System.out.print("  Which song would you like to transfer? (0 ~ " + (result.size() - 1) + ")\n- ");
					int choice = scanner.nextInt();
					target = result.get(choice);
				}
				System.out.println("- Sure. We'll transfer " + target.name + " at " + target.path + ".");
				JSONObject song = JSON.parseObject(readJSONFile(new File("phigros" + File.separator + target.path, "info.json")));
				transferSong(song, target.path, mysql, -1, -1, scanner);
			} else if (op.charAt(0) == 'a') {
				System.out.println("- Okay. Looking forward to seeing you again.");
				break;
			} else {
				System.out.println("- Sorry, but I can't understand your command.");
			}
		}
	}

	private static void transferSong(JSONObject song, String path, MySQL mysql, int chapter, int songIndex, Scanner scanner) {
		JSONArray charts = song.getJSONArray("charts");
		int begin = -1, end = charts.size();
		try {
			begin = mysql.countRows("phizone_chart");
			if (songIndex == -1) {
				songIndex = mysql.countRows("phizone_song") + 1;
			}
			for (int i = 0; i < end; i++) {
				JSONObject chart = charts.getJSONObject(i);
				String level = chart.getString("level");
				String difficulty = chart.getString("difficulty2");
				if (difficulty.equals("?")) {
					difficulty = "0.0";
				}
				if (difficulty.contains("(?)")) {
					difficulty = difficulty.replace("(?)", "").trim();
				}
				int notes = chart.getIntValue("notes");
				String charter = chart.getString("charter");
				mysql.insert("phizone_chart", new String[]{"level", "difficulty", "notes", "charter", "song"}, new String[]{level, difficulty, String.valueOf(notes), charter, String.valueOf(songIndex)});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (begin == -1) {
			System.out.println("  Fatal error: Cannot get row count from phizone_chart!");
			return;
		}
		end += begin;
		try {
			String name = song.getString("name");
			String composer = song.getString("composer");
			String illustration = song.getString("illustration");
			String illustrator = song.getString("illustrator");
			String chartIndexes = toString(begin + 1, end);
			if (chapter == -1) {
				System.out.print("  Which chapter does it belong to? (single integer)\n- ");
				chapter = scanner.nextInt();
				System.out.print("- ");
			} else {
				System.out.print("  ");
			}
			String bpm = song.getString("bpm");
			String duration = getDuration(song.getString("length"));
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			f.setTimeZone(TimeZone.getTimeZone("GMT"));
			String time = f.format(new Date()) + "000";
			if (mysql.insert("phizone_song", new String[]{"name", "composer", "illustration", "illustrator", "charts", "chapter", "bpm", "duration", "path", "official", "time", "score", "votes"}, new String[]{name, composer, illustration, illustrator, chartIndexes, String.valueOf(chapter), bpm, duration, path, "1", time, "0", "0"})) {
				System.out.println("Succeeded!");
			} else {
				System.out.println("Failed!");
			}
		} catch (SQLException e) {
			System.out.println("Failed. Caught SQLException!");
			e.printStackTrace();
		}
	}

	private static List<Integer> toInt(String[] src) {
		List<Integer> result = new ArrayList<>();
		for (String e : src) {
			result.add(Integer.parseInt(e));
		}
		return result;
	}

	private static String getDuration(String src) {
		String[] spl = src.split(":");
		int min = Integer.parseInt(spl[0]);
		int sec = Integer.parseInt(spl[1]);
		return (min * 60 + sec) + "000000";
	}

	private static String toString(int a, int b) {
		StringBuilder result = new StringBuilder();
		for (int i = a; i <= b; i++) {
			if (result.length() > 0) {
				result.append(",");
			}
			result.append(i);
		}
		return result.toString();
	}

	static class Song {
		String name, path;

		Song(String name, String path) {
			this.name = name;
			this.path = path;
		}
	}

	private static void crawl(Scanner scanner) throws IOException {
		System.out.println("Choose which chapter to crawl info from:");
		JSONArray chapters = HttpManager.readJsonFromUrl("https://zh.moegirl.org.cn/api.php?action=parse&format=json&prop=sections&page=Phigros/%E8%B0%B1%E9%9D%A2%E4%BF%A1%E6%81%AF").getJSONObject("parse").getJSONArray("sections");
		for (int i = 0; i < chapters.size(); i++) {
			JSONObject obj = chapters.getJSONObject(i);
			if (obj.getString("line").equals("注释")) {
				chapters.remove(obj);
				continue;
			}
			File folder = new File("phigros", ChapterManager.getShortenedName(obj.getString("line")));
			chapterMap.put(obj.getIntValue("index"), folder);
			System.out.printf("%2d. %s\n", obj.getIntValue("index"), obj.getString("line"));
		}
		while (true) {
			try {
				int index = Integer.parseInt(scanner.next());
				JSONObject result = HttpManager.readJsonFromUrl("https://zh.moegirl.org.cn/api.php?&action=parse&prop=sections&page=Phigros/%E8%B0%B1%E9%9D%A2%E4%BF%A1%E6%81%AF&prop=wikitext&format=json&section=" + index);
				String wikitext = result.getJSONObject("parse").getJSONObject("wikitext").getString("*");
				JSONObject chapter = WikiTextParser.parse(wikitext);
				JSONObject chapterInfo = new JSONObject(true);
				chapterInfo.put("name", chapter.getJSONObject("name"));
				JSONArray array = new JSONArray();
				for (int i = 0; i < chapter.getJSONArray("songs").size(); i++) {
					JSONObject song = chapter.getJSONArray("songs").getJSONObject(i);
					JSONObject simplified = new JSONObject(true);
					simplified.put("name", song.getString("name"));
					simplified.put("loc", SongManager.getShortenedName(song.getString("name")) + File.separator);
					write(song, new File(chapterMap.get(index), simplified.getString("loc") + "info.json"));
					array.add(simplified);
				}
				chapterInfo.put("songs", array);
				write(chapterInfo, new File(chapterMap.get(index), "info.json"));
				System.out.printf("Successfully retrieved information for %s - %s\n", chapter.getJSONObject("name").getString("subtitle"), chapter.getJSONObject("name").getString("title"));
			} catch (NumberFormatException e) {
				break;
			}
		}
	}

	private static void copyCharts(File chartPath) {
		int counter = 0;
		for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
			JSONObject chapter = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
			JSONArray songs = chapter.getJSONArray("songs");
			for (int i = 0; i < songs.size(); i++) {
				File folder = new File(entry.getValue(), songs.getJSONObject(i).getString("loc"));
				File srcDir = new File(chartPath, entry.getValue().getName() + File.separator + folder.getName());
				for (File f : Objects.requireNonNull(srcDir.listFiles())) {
					if (f.getName().startsWith("Chart") && f.getName().endsWith(".json")) {
						try {
							File dest = new File(folder, f.getName());
							FileUtils.copyFile(f, dest);
							System.out.printf("[SUCCESS] %s\n", dest.getAbsolutePath());
							counter++;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		System.out.printf("Successfully copied %d charts.\n", counter);
	}

	private static void check(File chartPath) throws NoSuchAlgorithmException, IOException {
		Map<String, File> provided = new HashMap<>();
		Map<String, File> retrieved = new HashMap<>();
		for (File file : Objects.requireNonNull(chartPath.listFiles())) {
			provided.put(getFileChecksum(MessageDigest.getInstance("MD5"), file), file);
		}
		for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
			JSONObject chapter = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
			JSONArray songs = chapter.getJSONArray("songs");
			for (int i = 0; i < songs.size(); i++) {
				File folder = new File(entry.getValue(), songs.getJSONObject(i).getString("loc"));
				for (File f : Objects.requireNonNull(folder.listFiles())) {
					if (!f.getName().endsWith(".json")) {
						continue;
					}
					if (f.getName().matches("Chart_[A-z]*_\\d{13,}.json")) {
						System.out.println("[DUPLICATED] " + f.getAbsolutePath());
					}
					String md5 = getFileChecksum(MessageDigest.getInstance("MD5"), f);
					if (retrieved.containsKey(md5)) {
						System.out.println("[DUPLICATED] " + retrieved.get(md5).getAbsolutePath() + " and " + f.getAbsolutePath());
						continue;
					}
					retrieved.put(md5, f);
				}
				JSONArray charts = JSONObject.parseObject(readJSONFile(new File(folder, "info.json"))).getJSONArray("charts");
				for (int j = 0; j < charts.size(); j++) {
					JSONObject chart = charts.getJSONObject(j);
					File f = new File(folder, "Chart_" + chart.getString("level") + ".json");
					if (f.exists()) {
						provided.remove(getFileChecksum(MessageDigest.getInstance("MD5"), f));
						ChartProvided p = new ChartProvided(f);
						if (p.getNotes() != chart.getIntValue("notes")) {
							System.out.printf("[ERROR] %s - %s - %s - %s: requires %d notes but is given %d notes.\n", chapter.getJSONObject("name").getString("subtitle"), chapter.getJSONObject("name").getString("title"), songs.getJSONObject(i).getString("name"), chart.getString("level"), chart.getIntValue("notes"), p.getNotes());
						}
					} else {
						System.out.printf("[MISSING] %s - %s - %s - %s\n", chapter.getJSONObject("name").getString("subtitle"), chapter.getJSONObject("name").getString("title"), songs.getJSONObject(i).getString("name"), chart.getString("level"));
					}
				}
			}
		}
		if (!provided.isEmpty()) {
			for (Map.Entry<String, File> entry : provided.entrySet()) {
				System.out.println("[UNUSED] " + entry.getValue().getAbsolutePath());
			}
		}
	}

	private static void getIllustration(File illustrationPath) {
		int counter = 0;
		for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
			JSONObject chapter = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
			JSONArray songs = chapter.getJSONArray("songs");
			for (int i = 0; i < songs.size(); i++) {
				File folder = new File(entry.getValue(), songs.getJSONObject(i).getString("loc"));
				File srcDir = new File(illustrationPath, entry.getValue().getName() + File.separator + folder.getName()), src = null;
				for (File f : Objects.requireNonNull(srcDir.listFiles())) {
					if (f.getName().toLowerCase().startsWith("illustration") && f.getName().endsWith(".png")) {
						src = f;
						break;
					}
				}
				if (src == null) {
					System.out.println("[NOT FOUND] No illustration in " + srcDir.getAbsolutePath());
					continue;
				}
				try {
					File dest = new File(folder, "illustration.png");
					FileUtils.copyFile(src, dest);
					System.out.printf("[SUCCESS] %s\n", dest.getAbsolutePath());
					counter++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.printf("Successfully copied %d illustrations.\n", counter);
	}

	private static void getAudio(File audioPath) {
		int counter = 0;
		for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
			JSONObject chapter = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
			JSONArray songs = chapter.getJSONArray("songs");
			for (int i = 0; i < songs.size(); i++) {
				File folder = new File(entry.getValue(), songs.getJSONObject(i).getString("loc"));
				File src = new File(audioPath, entry.getValue().getName() + File.separator + folder.getName() + File.separator + "music.wav");
				if (!src.exists()) {
					System.out.printf("[NOT FOUND] %s\n", src.getAbsolutePath());
					continue;
				}
				try {
					File dest = new File(folder, "music.wav");
					FileUtils.copyFile(src, dest);
					System.out.printf("[SUCCESS] %s\n", dest.getAbsolutePath());
					counter++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.printf("Successfully copied %d audio files.\n", counter);
	}

	private static void getCharts(File chartPath, Scanner scanner) {
		int chartsRequired = 0, chartsProvided = Objects.requireNonNull(chartPath.listFiles()).length;
		for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
			JSONObject chapter = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
			JSONArray songs = chapter.getJSONArray("songs");
			for (int i = 0; i < songs.size(); i++) {
				File folder = new File(entry.getValue(), songs.getJSONObject(i).getString("loc"));
				JSONArray charts = JSONObject.parseObject(readJSONFile(new File(folder, "info.json"))).getJSONArray("charts");
				chartsRequired += charts.size();
			}
		}
		if (chartsProvided != chartsRequired) {
			System.out.printf("[WARN] %d chart files are required, but there are %d files provided.\n", chartsRequired, chartsProvided);
		}
		Map<Integer, List<ChartProvided>> map = new HashMap<>();
		for (File file : Objects.requireNonNull(chartPath.listFiles())) {
			ChartProvided c = new ChartProvided(file);
			int notes = c.getNotes();
			System.out.printf("%s: %d, %.1f, %.1f\n", file.getName(), notes, c.getMinBpm(), c.getMaxBpm());
			if (map.containsKey(notes)) {
				List<ChartProvided> list = new ArrayList<>(map.get(notes));
				list.add(c);
				map.put(notes, list);
			} else {
				map.put(notes, Collections.singletonList(c));
			}
		}
		List<String> missingChartStrings = new ArrayList<>();
		List<ChartRequired> missingCharts = new ArrayList<>();
		int counter = 0;
		for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
			JSONObject chapter = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
			JSONArray songs = chapter.getJSONArray("songs");
			System.out.printf("%s - %s:\n", chapter.getJSONObject("name").getString("subtitle"), chapter.getJSONObject("name").getString("title"));
			for (int i = 0; i < songs.size(); i++) {
				File folder = new File(entry.getValue(), songs.getJSONObject(i).getString("loc"));
				System.out.print(songs.getJSONObject(i).getString("name") + ":");
				JSONObject song = JSONObject.parseObject(readJSONFile(new File(folder, "info.json")));
				JSONArray charts = song.getJSONArray("charts");
				for (int j = 0; j < charts.size(); j++) {
					JSONObject chart = charts.getJSONObject(j);
					String level = chart.getString("level");
					System.out.printf(" %s", level);
					int notes = chart.getIntValue("notes");
					if (!map.containsKey(notes)) {
						System.out.print("[MISSING]");
						missingChartStrings.add(chapter.getJSONObject("name").getString("subtitle") + " - " + chapter.getJSONObject("name").getString("title") + " - " + songs.getJSONObject(i).getString("name") + " - " + level + " (NOTES: " + notes + ")");
						continue;
					}
					boolean suc = false;
					for (ChartProvided a : map.get(notes)) {
						if (compare(a, chart, getBPM(song.getString("bpm")))) {
							File c = new File(folder, "Chart_" + level + ".json");
							if (c.exists()) {
								c.renameTo(new File(folder, "Chart_" + level + "_" + System.currentTimeMillis() + ".json"));
							}
							try {
								FileUtils.copyFile(a.getFile(), c);
								suc = true;
								counter++;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					if (!suc) {
						System.out.print("[MISSING]");
						double[] bpm = getBPM(song.getString("bpm"));
						missingCharts.add(new ChartRequired(folder, level, notes));
						missingChartStrings.add(chapter.getJSONObject("name").getString("subtitle") + " - " + chapter.getJSONObject("name").getString("title") + " - " + songs.getJSONObject(i).getString("name") + " - " + level + " (NOTES: " + notes + ", BPM: " + (bpm.length > 1 ? bpm[0] + "-" + bpm[1] : bpm[0]) + ")");
					}
				}
				System.out.println();
			}
		}
		System.out.printf("Successfully copied %d chart files to respective song folders.\n", counter);
		if (!missingChartStrings.isEmpty()) {
			System.out.println("The following charts are missing:");
			for (String str : missingChartStrings) {
				System.out.println(str);
			}
			System.out.println("Would you like to retry matching charts without verifying BPM? (y/n)");
			if (scanner.next().toLowerCase().startsWith("y")) {
				counter = 0;
				for (ChartRequired r : missingCharts) {
					boolean suc = false;
					for (ChartProvided p : map.get(r.getNotes())) {
						if (p.getFile().getName().startsWith("Chart_" + r.getLevel())) {
							File c = new File(r.getFolder(), "Chart_" + r.getLevel() + ".json");
							if (c.exists()) {
								c.renameTo(new File(r.getFolder(), "Chart_" + r.getLevel() + "_" + System.currentTimeMillis() + ".json"));
							}
							try {
								FileUtils.copyFile(p.getFile(), c);
								suc = true;
								counter++;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					if (!suc) {
						System.out.println("Still couldn't find " + new File(r.getFolder(), "Chart_" + r.getLevel() + ".json").getAbsolutePath());
					}
				}
				System.out.printf("Successfully copied %d more chart files to respective song folders.\n", counter);
			}
		}
	}

	private static double[] getBPM(String bpm) {
		if (bpm.contains("-")) {
			double minBpm = Double.parseDouble(bpm.split("-")[0]);
			double maxBpm = Double.parseDouble(bpm.split("-")[1]);
			return new double[]{minBpm, maxBpm};
		} else {
			return new double[]{Double.parseDouble(bpm)};
		}
	}

	private static boolean compare(ChartProvided c, JSONObject obj, double[] bpm) {
		if (!c.getFile().getName().startsWith("Chart_" + obj.getString("level"))) {
			return false;
		}
		if (c.getNotes() != obj.getIntValue("notes")) {
			return false;
		}
		if (bpm.length > 1) {
			return c.getMinBpm() == bpm[0] && c.getMaxBpm() == bpm[1];
		} else {
			return c.getMinBpm() == c.getMaxBpm() && c.getMinBpm() == bpm[0];
		}
	}

	private static void summarize(String version) throws IOException {
		JSONObject result = new JSONObject(true);
		result.put("version", version);
		result.put("last-updated-ts", System.currentTimeMillis() / 1000);
		result.put("last-updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		result.put("cover", "covers" + File.separator + "phigros.png");
		JSONArray chapters = new JSONArray();
		int total = 0;
		for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
			JSONObject chapter = new JSONObject(true);
			JSONObject info = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
			chapter.put("name", info.getJSONObject("name"));
			chapter.put("direct", true);
			chapter.put("loc", entry.getValue().getName() + File.separator);
			File illustration = new File("phigros", "covers" + File.separator + entry.getValue().getName() + ".png");
			if (illustration.exists()) {
				chapter.put("cover", "covers" + File.separator + entry.getValue().getName() + ".png");
			}
			File locked = new File("phigros", "covers" + File.separator + entry.getValue().getName() + "-locked.png");
			if (locked.exists()) {
				chapter.put("cover-locked", "covers" + File.separator + entry.getValue().getName() + "-locked.png");
			}
			chapter.put("songs", info.getJSONArray("songs").size());
			total += info.getJSONArray("songs").size();
//			System.out.printf("There are %d songs in %s - %s\n", info.getJSONArray("songs").size(), chapter.getJSONObject("name").getString("subtitle"), chapter.getJSONObject("name").getString("title"));
			chapters.add(chapter);
		}
		result.put("songs", total);
		appendIndirectChapters("hidden", chapters);
		appendIndirectChapters("unavailable", chapters);
		result.put("chapters", chapters);
		File out = new File("phigros", "info.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		writer.write(result.toJSONString());
		writer.close();
		System.out.println("Successfully summarized info to " + out.getAbsolutePath());
	}

	private static void appendIndirectChapters(String name, JSONArray chapters) {
		File dir = new File("phigros", name);
		JSONArray songs = JSONObject.parseObject(readJSONFile(new File(dir, "info.json"))).getJSONArray("songs");
		for (int i = 0; i < songs.size(); i++) {
			JSONObject song = JSONObject.parseObject(readJSONFile(new File(dir, songs.getJSONObject(i).getString("loc") + "info.json")));
			JSONObject obj = song.getJSONObject("chapter");
			boolean suc = false;
			for (Map.Entry<Integer, File> entry : chapterMap.entrySet()) {
				JSONObject info = JSONObject.parseObject(readJSONFile(new File(entry.getValue(), "info.json")));
				if (info.getJSONObject("name").getString("subtitle").equals(obj.getString("subtitle")) && info.getJSONObject("name").getString("title").equals(obj.getString("title"))) {
					suc = true;
					break;
				}
			}
			if (!suc) {
				for (int j = 0; j < chapters.size(); j++) {
					if (chapters.getJSONObject(j).getJSONObject("name").getString("subtitle").equals(obj.getString("subtitle")) && chapters.getJSONObject(j).getJSONObject("name").getString("title").equals(obj.getString("title"))) {
						suc = true;
						chapters.getJSONObject(j).put("songs", chapters.getJSONObject(j).getIntValue("songs") + 1);
						break;
					}
				}
				if (!suc) {
					JSONObject chapter = new JSONObject(true);
					chapter.put("name", obj);
					chapter.put("direct", false);
					String shortened = ChapterManager.getShortenedName(chapter.getJSONObject("name").getString("subtitle") + "-" + chapter.getJSONObject("name").getString("title"));
					File illustration = new File("phigros", "covers" + File.separator + shortened + ".png");
					if (illustration.exists()) {
						chapter.put("cover", "covers" + File.separator + shortened + ".png");
					}
					File locked = new File("phigros", "covers" + File.separator + shortened + "-locked.png");
					if (locked.exists()) {
						chapter.put("cover-locked", "covers" + File.separator + shortened + "-locked.png");
					}
					chapter.put("songs", 1);
					chapters.add(chapter);
				}
			}
		}
	}

	public static String readJSONFile(File jsonFile) {
		String jsonStr;
		try {
			FileReader fileReader = new FileReader(jsonFile);
			Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
			int ch = 0;
			StringBuilder builder = new StringBuilder();
			while ((ch = reader.read()) != -1) {
				builder.append((char) ch);
			}
			fileReader.close();
			reader.close();
			jsonStr = builder.toString();
			return jsonStr;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void write(JSONObject obj, File out) throws IOException {
		if (!out.exists()) {
			out.getParentFile().mkdirs();
			out.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		writer.write(obj.toJSONString());
		writer.close();
	}

	private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
		//Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);

		//Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		//Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}

		//close the stream; We don't need it now.
		fis.close();

		//Get the hash's bytes
		byte[] bytes = digest.digest();

		//This bytes[] has bytes in decimal format;
		//Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		//return complete hash
		return sb.toString();
	}

}
