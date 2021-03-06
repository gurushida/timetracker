package com.sep.timetracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportedTime extends TimeInfoManager {

	public static final String FILENAME = "reported_time";
	private final static String REMOVED_PREFIX = "removed ";
	private final Map<String, Long> map = new HashMap<>();
	private final Map<String, ArrayList<String>> description = new HashMap<>();

	
	public ReportedTime(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	public ReportedTime(List<String> lines) throws ParseException {
		super(lines);
		loadData();
	}

	public ReportedTime(String... lines) throws ParseException {
		super(lines);
		loadData();
	}

	@Override
	protected void processLine(String line, String comment) throws ParseException {
		boolean removed = line.startsWith(REMOVED_PREFIX);
		if (removed) {
			line = line.substring(REMOVED_PREFIX.length());
			Date d = Util.DAY_FORMAT.parse(line);
			String normalized = Util.DAY_FORMAT.format(d);
			map.remove(normalized);
			description.remove(normalized);
			return;
		}

		String[] parts = line.split("-->");
		if (parts.length != 2) {
			throw new IllegalStateException("Reported time data contains invalid entry: " + line);
		}
		Date start = Util.TIME_FORMAT.parse(parts[0].trim());
		String normalizedStart = Util.DAY_FORMAT.format(start);
		Date end = Util.TIME_FORMAT.parse(parts[1].trim());
		long minutes = Util.between(start, end);
		if (minutes < 0) {
			throw new IllegalStateException("Reported time data contains invalid entry with end point before start point: " + line);
		}
		add(normalizedStart, minutes);

		if (comment != null) {
			ArrayList<String> list = description.get(normalizedStart);
			if (list == null) {
				list = new ArrayList<String>();
				description.put(normalizedStart, list);
			}
			list.add(comment);
		}
	}

	public List<String> getComments(Date d) {
		String date = Util.DAY_FORMAT.format(d);
		return description.get(date);
	}

	private void add(String date, long timeWorkedInMinutes) {
		Long time = map.get(date);
		if (time == null) {
			time = timeWorkedInMinutes;
		} else {
			time += timeWorkedInMinutes;
		}
		map.put(date, time);
	}
	
	/**
	 * Given a date, returns the amount of time in minutes worked that day.
	 */
	public long getTimeWorkedInMinutes(String d) throws ParseException {
		return getTimeWorkedInMinutes(Util.DAY_FORMAT.parse(d));
	}

	/**
	 * Given a date, returns the amount of time in minutes worked that day.
	 */
	public long getTimeWorkedInMinutes(Date d) {
		String date = Util.DAY_FORMAT.format(d);
		return map.getOrDefault(date, 0l);
	}

	public static void initialize(File f) throws IOException, ParseException {
		f.createNewFile();
		ReportedTime r = new ReportedTime(f);
		r.addLine("# This file list all worked periods as time intervals in the following format:");
		r.addLine("#");
		r.addLine("# dd/mm/yyyy hh:mm Z --> dd/mm/yyyy hh:mm Z");
		r.addLine("#");
		r.addLine("# Example:");
		r.addLine("# 19/04/2019 22:00 +0200 --> 19/04/2019 22:05 +0200 # Forgot to push commit before leaving for week-end");
		r.addLine("#");
		r.addLine("# DON'T EDIT MANUALLY WITHOUT GREAT CARE !");
		r.addLine("");
	}

	public void reportWorkedTimeInterval(Date start, Date end, String description) throws IOException {
		StringBuilder b = new StringBuilder();
		b.append(Util.TIME_FORMAT.format(start));
		b.append(" --> ");
		b.append(Util.TIME_FORMAT.format(end));
		if (description != null && !description.isEmpty()) {
			b.append(" # ").append(description);
		}
		addLine(b.toString());
	}

	public void removeWorkedDay(Date d) throws IOException {
		addLine(REMOVED_PREFIX + Util.DAY_FORMAT.format(d));
	}
}
