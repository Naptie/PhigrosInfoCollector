package me.naptie.phigros.infocollector.objects;

import java.io.File;

public class ChartRequired {

	private final File folder;
	private final String level;
	private final int notes;

	public ChartRequired(File folder, String level, int notes) {
		this.folder = folder;
		this.level = level;
		this.notes = notes;
	}

	public File getFolder() {
		return folder;
	}

	public String getLevel() {
		return level;
	}

	public int getNotes() {
		return notes;
	}
}
