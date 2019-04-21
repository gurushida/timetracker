package com.sep.timetracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class TrackingStart extends TimeInfoManager {

	public final static String FILENAME = ".tracking_start";

	private Date start;

	public TrackingStart(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	@Override
	protected void processLine(String line) throws ParseException {
		if (start != null) {
			throw new IllegalStateException();
		}
		start = Util.DAY_FORMAT.parse(line);
	}

	public Date getTrackingStart() {
		return start;
	}

	public static void initialize(File f, Date d) throws IOException, ParseException {
		f.createNewFile();
		TrackingStart t = new TrackingStart(f);
		t.addLine("# Day the time tracking started from in dd/mm/yyyy format");
		t.addLine(Util.DAY_FORMAT.format(d));
	}
}
