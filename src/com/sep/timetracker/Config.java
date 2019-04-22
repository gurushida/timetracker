package com.sep.timetracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class Config extends TimeInfoManager {

	public final static String FILENAME = ".config";

	private Date start;

	public Config(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	@Override
	protected void processLine(String line) throws ParseException {
		if (line.startsWith("start=")) {
			line = line.substring("start=".length());
			if (start != null) {
				throw new IllegalStateException();
			}
			start = Util.DAY_FORMAT.parse(line);
		} else {
			System.err.println("Invalid line in " + dataSource.getAbsolutePath() + ": " + line);
			System.err.println();
			System.exit(1);
		}
	}

	public Date getTrackingStart() {
		return start;
	}

	public static void initialize(File f, Date d) throws IOException, ParseException {
		f.createNewFile();
		Config t = new Config(f);
		t.addLine("# Day the time tracking started from in dd/mm/yyyy format");
		t.addLine("start=" + Util.DAY_FORMAT.format(d));
	}
}
