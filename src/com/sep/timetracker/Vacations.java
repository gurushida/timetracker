package com.sep.timetracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Vacations extends TimeInfoManager {

	public static final String FILENAME = "vacations";

	private final Set<String> vacationDays = new HashSet<>();
	
	public Vacations(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	public Vacations(List<String> lines) throws ParseException {
		super(lines);
		loadData();
	}

	public Vacations(String... lines) throws ParseException {
		super(lines);
		loadData();
	}

	@Override
	protected void processLine(String line) throws ParseException {
		if (line.contains(" ")) {
			throw new IllegalStateException("Vacations data contains invalid entry: " + line);
		}
		Date d = Util.DAY_FORMAT.parse(line);
		if (Util.isWeekEndDay(d)) {
			throw new IllegalStateException("Vacations data contains vacation date " + line + " that is during a weekend");
		}
		String dateNormalized = Util.DAY_FORMAT.format(d);
		vacationDays.add(dateNormalized);
	}
	
	/**
	 * Given a date, returns true if the given day is registered as a vacation day.
	 */
	public boolean isVacationDay(String d) throws ParseException {
		return isVacationDay(Util.DAY_FORMAT.parse(d));
	}

	/**
	 * Given a date, returns true if the given day is registered as a vacation day.
	 */
	public boolean isVacationDay(Date d) {
		String date = Util.DAY_FORMAT.format(d);
		return vacationDays.contains(date);
	}

	/**
	 * Returns a list of all the vacation dates registered for the given year. 
	 * @throws ParseException 
	 */
	public List<String> getVacationDays(int year) throws ParseException {
		ArrayList<String> list = new ArrayList<String>();
		for (String date : vacationDays) {
			Date d = Util.DAY_FORMAT.parse(date);
			if (year == Util.getYear(d)) {
				list.add(date);
			}
		}
		return list;
	}

	public void addVacationDay(Date d) throws IOException {
		addLine(Util.DAY_FORMAT.format(d));
	}

	public static void initialize(File f) throws IOException, ParseException {
		f.createNewFile();
		Vacations v = new Vacations(f);
		v.addLine("# This file list vacation days in the dd/mm/yyyy format.");
		v.addLine("# If a vacation day is registered as a half-worked day in the public holidays,");
		v.addLine("# it will only be counted as 0.5 day when it comes to the annual day limit.");
		v.addLine("#");
		v.addLine("# Example:");
		v.addLine("# 15/04/2019     # Monday on Easter week");
		v.addLine("#");
		v.addLine("# DON'T EDIT MANUALLY WITHOUT GREAT CARE !");
		v.addLine("");
	}
}
