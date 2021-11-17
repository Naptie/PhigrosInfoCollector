package me.naptie.phigros.infocollector.objects;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.naptie.phigros.infocollector.Main;

import java.io.File;

public class ChartProvided {

	private final int notes;
	private double minBpm, maxBpm;
	private final File file;

	public ChartProvided(File file) {
		this.file = file;
		JSONObject obj = JSONObject.parseObject(Main.readJSONFile(file));
		notes = obj.getIntValue("numOfNotes");
		JSONArray judgeLineList = obj.getJSONArray("judgeLineList");
		minBpm = Integer.MAX_VALUE;
		for (int i = 0; i < judgeLineList.size(); i++) {
			JSONObject judgeLine = judgeLineList.getJSONObject(i);
			minBpm = Double.min(minBpm, judgeLine.getDoubleValue("bpm"));
			maxBpm = Double.max(maxBpm, judgeLine.getDoubleValue("bpm"));
		}
	}

	public File getFile() {
		return file;
	}

	public int getNotes() {
		return notes;
	}

	public double getMinBpm() {
		return minBpm;
	}

	public double getMaxBpm() {
		return maxBpm;
	}
}
