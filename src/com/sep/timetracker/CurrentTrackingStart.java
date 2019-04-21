package com.sep.timetracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class CurrentTrackingStart extends TimeInfoManager {

	public final static String FILENAME = ".current_tracking_start";

	private Date start;

	public CurrentTrackingStart(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	@Override
	protected void processLine(String line) throws ParseException {
		if (start != null) {
			throw new IllegalStateException();
		}
		start = Util.TIME_FORMAT.parse(line);
	}

	public Date getCurrentTrackingStart() {
		return start;
	}

	public static void startTracking(File f, Date d) throws IOException, ParseException {
		f.createNewFile();
		CurrentTrackingStart t = new CurrentTrackingStart(f);
		t.addLine("# Beginning of the current time tracking session in dd/mm/yyyy hh:mm z format");
		t.addLine(Util.TIME_FORMAT.format(d));
	}
}
