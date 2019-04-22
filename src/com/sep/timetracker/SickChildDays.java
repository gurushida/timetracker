package com.sep.timetracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SickChildDays extends TimeInfoManager {

	public static final String FILENAME = "sick_child_days";

	private final Set<String> sickChildDays = new HashSet<>();
	
	public SickChildDays(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	public SickChildDays(List<String> lines) throws ParseException {
		super(lines);
		loadData();
	}

	public SickChildDays(String... lines) throws ParseException {
		super(lines);
		loadData();
	}

	@Override
	protected void processLine(String line, String comment) throws ParseException {
		Date d = Util.DAY_FORMAT.parse(line);
		if (Util.isWeekEndDay(d)) {
			throw new IllegalStateException("Sick child data file contains date " + line + " that is during a weekend");
		}
		String dateNormalized = Util.DAY_FORMAT.format(d);
		sickChildDays.add(dateNormalized);
	}
	
	/**
	 * Given a date, returns true if the given day is registered as a sick child day.
	 */
	public boolean isSickChildDay(String d) throws ParseException {
		return isSickChildDay(Util.DAY_FORMAT.parse(d));
	}

	/**
	 * Given a date, returns true if the given day is registered as a sick child day.
	 */
	public boolean isSickChildDay(Date d) {
		String date = Util.DAY_FORMAT.format(d);
		return sickChildDays.contains(date);
	}

	public static void initialize(File f) throws IOException, ParseException {
		f.createNewFile();
		SickChildDays s = new SickChildDays(f);
		s.addLine("# This file list days spent home taking care of a sick child in the dd/mm/yyyy format.");
		s.addLine("#");
		s.addLine("# Example:");
		s.addLine("# 15/04/2019     # Kid has fever");
		s.addLine("#");
		s.addLine("# DON'T EDIT MANUALLY WITHOUT GREAT CARE !");
		s.addLine("");
	}

	public void addSickChildDay(Date d) throws IOException {
		addLine(Util.DAY_FORMAT.format(d));
	}

}
