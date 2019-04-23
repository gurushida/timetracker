package com.sep.timetracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class WorkPlanning extends TimeInfoManager {

	public static final String FILENAME = "work_planning";
	private final static String REMOVED_PREFIX = "removed ";

	private final Map<String, Integer> planning = new HashMap<>();

	public WorkPlanning(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	public WorkPlanning(List<String> lines) throws ParseException {
		super(lines);
		loadData();
	}

	public WorkPlanning(String... lines) throws ParseException {
		super(lines);
		loadData();
	}

	@Override
	protected void processLine(String line, String comment) throws ParseException {
		boolean removed = line.startsWith(REMOVED_PREFIX);
		if (removed) {
			line = line.substring(REMOVED_PREFIX.length());
		}
		try (Scanner sc = new Scanner(line)) {
			Date d = Util.DAY_FORMAT.parse(sc.next());
			String dateNormalized = Util.DAY_FORMAT.format(d);
			if (removed) {
				planning.remove(dateNormalized);
				return;
			}

			Integer minutes;
			if (!sc.hasNext() || null == (minutes = Util.getDurationInMinutes(sc.next()))) {
				throw new IllegalStateException("Invalid work planning line: " + line);
			}
			planning.put(dateNormalized, minutes);
		}
	}

	/**
	 * Returns the amount of minutes specified for the given day in the work planning file
	 * or null if the file does not specify anything for this day.
	 */
	public Integer getTimeToWork(Date d) {
		String date = Util.DAY_FORMAT.format(d);
		return planning.get(date);
	}

	public void addPlanningDay(Date d, int minutesToWork, String description) throws IOException {
		int hours = minutesToWork / 60;
		int minutes = minutesToWork % 60;
		StringBuilder b = new StringBuilder();
		b.append(Util.DAY_FORMAT.format(d));
		b.append(" ");
		b.append(hours);
		b.append(":");
		if (minutes < 10) {
			b.append("0");
		}
		b.append(minutes);
		if (description != null && !description.isEmpty()) {
			b.append(" # ").append(description);
		}
		addLine(b.toString());
	}

	public void removePlanningDay(Date d) throws IOException {
		addLine(REMOVED_PREFIX + Util.DAY_FORMAT.format(d));
	}

	public static void initialize(File f) throws IOException, ParseException {
		f.createNewFile();
		WorkPlanning v = new WorkPlanning(f);
		v.addLine("# This file declares durations to work for given days that take precedence");
		v.addLine("# Over the regular work pattern. The format of a line is dd/mm/yyyy hours:minutes");
		v.addLine("#");
		v.addLine("# Examples:");
		v.addLine("# 13/04/2019 7:00   # Exceptionally working on a Sunday");
		v.addLine("# 14/04/2019 0:00   # Exceptionally not working on a Monday");
		v.addLine("#");
		v.addLine("# DON'T EDIT MANUALLY WITHOUT GREAT CARE !");
		v.addLine("");
	}
}
