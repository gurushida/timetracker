package com.sep.timetracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SickDays extends TimeInfoManager {

	public static final String FILENAME = "sick_days";

	private final Set<String> sickDays = new HashSet<>();
	
	public SickDays(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	public SickDays(List<String> lines) throws ParseException {
		super(lines);
		loadData();
	}

	public SickDays(String... lines) throws ParseException {
		super(lines);
		loadData();
	}

	@Override
	protected void processLine(String line) throws ParseException {
		Date d = Util.DAY_FORMAT.parse(line);
		if (Util.isWeekEndDay(d)) {
			throw new IllegalStateException("Sick days data contains date " + line + " that is during a weekend");
		}
		String dateNormalized = Util.DAY_FORMAT.format(d);
		sickDays.add(dateNormalized);
	}
	
	/**
	 * Given a date, returns true if the given day is registered as a sick day.
	 */
	public boolean isSickDay(String d) throws ParseException {
		return isSickDay(Util.DAY_FORMAT.parse(d));
	}

	/**
	 * Given a date, returns true if the given day is registered as a sick day.
	 */
	public boolean isSickDay(Date d) {
		String date = Util.DAY_FORMAT.format(d);
		return sickDays.contains(date);
	}
	
	public static void initialize(File f) throws IOException, ParseException {
		f.createNewFile();
		SickDays s = new SickDays(f);
		s.addLine("# This file list sick days in the dd/mm/yyyy format.");
		s.addLine("#");
		s.addLine("# Example:");
		s.addLine("# 15/04/2019     # Flu");
		s.addLine("#");
		s.addLine("# DON'T EDIT MANUALLY WITHOUT GREAT CARE !");
		s.addLine("");
	}

	public void addSickDay(Date d) throws IOException {
		addLine(Util.DAY_FORMAT.format(d));
	}

}
