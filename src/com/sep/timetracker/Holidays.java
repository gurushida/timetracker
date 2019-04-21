package com.sep.timetracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Holidays extends TimeInfoManager {

	public static enum HolidayType {
		HOLIDAY,
		HALF_DAY,
		NOT_HOLIDAY
	}
	
	public static final String FILENAME = "holidays";

	private final Map<String, Boolean> map = new HashMap<>();
	
	public Holidays(File f) throws ParseException, FileNotFoundException {
		super(f);
		loadData();
	}

	public Holidays(List<String> lines) throws ParseException {
		super(lines);
		loadData();
	}

	public Holidays(String... lines) throws ParseException {
		super(lines);
		loadData();
	}

	@Override
	protected void processLine(String line) throws ParseException {
		boolean halfDay = line.endsWith("1/2");
		String dateRaw = line.split(" ")[0];
		Date d = Util.DAY_FORMAT.parse(dateRaw);
		String dateNormalized = Util.DAY_FORMAT.format(d);
		map.put(dateNormalized, halfDay);
	}
	
	/**
	 * Given a date, returns a value indicating whether it is holiday or not.
	 * @throws ParseException 
	 */
	public HolidayType isHoliday(String d) throws ParseException {
		return isHoliday(Util.DAY_FORMAT.parse(d));
	}

	/**
	 * Given a date, returns a value indicating whether it is holiday or not.
	 * @throws ParseException 
	 */
	public HolidayType isHoliday(Date d) {
		String date = Util.DAY_FORMAT.format(d);
		Boolean halfDay = map.get(date);
		if (halfDay == null) {
			return HolidayType.NOT_HOLIDAY;
		} else if (halfDay) {
			return HolidayType.HALF_DAY;
		} else {
			return HolidayType.HOLIDAY;
		}
	}

	public static void initialize(File f) throws IOException, ParseException {
		f.createNewFile();
		Holidays h = new Holidays(f);
		h.addLine("# This file list public holidays in the dd/mm/yyyy format.");
		h.addLine("# It is possible to specify half-days by adding ' 1/2'.");
		h.addLine("#");
		h.addLine("# Examples:");
		h.addLine("# 24/12/2008 1/2 # Half-day before Christmas");
		h.addLine("# 25/12/2008     # Christmas");
		h.addLine("#");
		h.addLine("# DON'T EDIT MANUALLY WITHOUT GREAT CARE !");
		h.addLine("");
	}

	public void addHoliday(Date d, boolean halfDay, String description) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.DAY_FORMAT.format(d));
		if (halfDay) {
			sb.append(" 1/2");
		}
		if (description != null && !description.isEmpty()) {
			sb.append(" # ").append(description);
		}
		addLine(sb.toString());
	}

}
