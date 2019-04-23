package com.sep.timetracker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeekPattern {

	public static String DEFAULT = "Mon=7:30,Tue=7:30,Wed=7:30,Thu=7:30,Fri=7:30";
	private final static Pattern PATTERN = Pattern.compile("^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)=([01]?[0-9]|2[0-3]):([0-5][0-9])$");

	private Map<String, Integer> minutesToWorkPerDay = new HashMap<>();

	public WeekPattern() {
		this(DEFAULT);
	}

	public WeekPattern(String pattern) {
		String[] parts = pattern.split(",");
		for (String part : parts) {
			part = part.trim();
			if (part.isEmpty()) {
				continue;
			}
			Pair<String, Integer> pair = parseDaySchedule(part);
			if (pair == null) {
				System.err.println("Invalid work schedule part: " + part);
				System.exit(1);
			}
			minutesToWorkPerDay.put(pair.first, pair.second);
		}
	}

	public static Pair<String, Integer> parseDaySchedule(String s) {
		Matcher m = PATTERN.matcher(s);
		if (!m.matches()) {
			return null;
		}

		int minutes = Integer.valueOf(m.group(2)) * 60 + Integer.valueOf(m.group(3));
		String day;
		switch(m.group(1)) {
		case "Mon": day = "Monday"; break;
		case "Tue": day = "Tuesday"; break;
		case "Wed": day = "Wednesday"; break;
		case "Thu": day = "Thursday"; break;
		case "Fri": day = "Friday"; break;
		case "Sat": day = "Saturday"; break;
		case "Sun": day = "Sunday"; break;
		default: throw new IllegalStateException();
		}
		return new Pair<String, Integer>(day, minutes);
	}

	/**
	 * Given a date, returns the regular amount of minutes that should be worked
	 * provided that the day is not a vacation day, a sick day, sick child day or
	 * a public holiday.
	 */
	public int getTimeToWork(Date d) {
		return getTimeToWork(Util.getDay(d));
	}

	public int getTimeToWork(String day) {
		switch(day) {
		case "Monday":
		case "Tuesday":
		case "Wednesday":
		case "Thursday":
		case "Friday":
		case "Saturday":
		case "Sunday": return minutesToWorkPerDay.getOrDefault(day, 0);
		default: throw new IllegalArgumentException();
		}
	}
}
