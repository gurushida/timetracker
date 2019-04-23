package com.sep.timetracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

public class Config extends TimeInfoManager {

	public final static String FILENAME = ".config";

	private Date start;
	private Integer vacationDaysPerYear;
	private WeekPattern weekPattern;

	public Config(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	public Config() throws ParseException {
		super(Collections.emptyList());
		this.start = new Date();
		this.vacationDaysPerYear = 25;
		this.weekPattern = new WeekPattern();
	}

	@Override
	protected void processLine(String line, String comment) throws ParseException {
		if (line.startsWith("start=")) {
			line = line.substring("start=".length());
			start = Util.DAY_FORMAT.parse(line);
		} else if (line.startsWith("vacation_days_per_year=")) {
				line = line.substring("vacation_days_per_year=".length());
				try {
					vacationDaysPerYear = Integer.valueOf(line);
				} catch (Exception e) {
					System.err.println("Invalid line in " + dataSource.getAbsolutePath() + ": vacation_days_per_year=" + line);
					System.err.println();
					System.exit(1);
				}
		} else if (line.startsWith("week_pattern=")) {
			line = line.substring("week_pattern=".length());
			try {
				weekPattern = new WeekPattern(line);
			} catch (Exception e) {
				System.err.println("Invalid line in " + dataSource.getAbsolutePath() + ": week_pattern=" + line);
				System.err.println();
				System.exit(1);
			}
		} else {
			System.err.println("Invalid line in " + dataSource.getAbsolutePath() + ": " + line);
			System.err.println();
			System.exit(1);
		}
	}

	public Date getTrackingStart() {
		return start;
	}

	public int getVacationDaysPerYear() {
		return vacationDaysPerYear;
	}

	public WeekPattern getWeekPattern() {
		return weekPattern;
	}

	public static void initialize(File f, Date d, int vacationDaysPerYear, String weekPattern) throws IOException, ParseException {
		f.createNewFile();
		Config t = new Config(f);
		t.addLine("# Day the time tracking started from in dd/mm/yyyy format");
		t.addLine("start=" + Util.DAY_FORMAT.format(d));
		t.addLine("# Vacation days per year");
		t.addLine("vacation_days_per_year=" + vacationDaysPerYear);
		t.addLine("week_pattern=" + (weekPattern == null ? "" : weekPattern));
	}
}
