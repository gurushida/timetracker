package com.sep.timetracker;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public final static int WORKING_DAY = 7 * 60 + 30; // minutes

	public final static DateFormat DAY_OF_WEEK_FORMAT = new SimpleDateFormat("EEEE", Locale.ENGLISH);
	public final static DateFormat DAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	public final static DateFormat TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm Z", Locale.ENGLISH);

	private final static Pattern DURATION = Pattern.compile("^([0-9]+):([0-5][0-9])$");
	private final static Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
	
	public static Iterator<String> toIterator(String[] lines) {
		List<String> list = new LinkedList<>();
		for (String line : lines) {
			list.add(line);
		}
		return list.iterator();
	}
	
	/**
	 * Returns the duration in minutes elapsed between the given dates, taking
	 * daylight saving changes into account.
	 */
	public static long between(Date a, Date b) {
		return (b.getTime() - a.getTime()) / (1000 * 60);
	}

	/**
	 * Returns the date obtained when adding the given duration to given date.
	 */
	public static Date addMinutes(Date d, long minutes) {
		return Date.from(d.toInstant().plus(minutes, ChronoUnit.MINUTES));
	}

	public static boolean isWeekEndDay(Date d) {
		calendar.setTime(d);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
	}
	
	public static int getYear(Date d) {
		calendar.setTime(d);
		return calendar.get(Calendar.YEAR);
	}

	public static String getDay(Date d) {
		return DAY_OF_WEEK_FORMAT.format(d);
	}

	public static Date nextDay(Date d) {
		calendar.setTime(d);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static Date startOfYear(Date d) {
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		return calendar.getTime();
	}

	public static Date startOfMonth(Date d) {
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static Date startOfWeek(Date d) {
		calendar.setTime(d);
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		return calendar.getTime();
	}

	/**
	 * Takes a duration in the hh:mm format and returns the number of minutes it
	 * represents or null if the given duration is not in the correct format.
	 */
	public static Integer getDurationInMinutes(String duration) {
		Matcher m = DURATION.matcher(duration);
		if (!m.matches()) {
			return null;
		}
		int minutes = 60 * Integer.valueOf(m.group(1)) + Integer.valueOf(m.group(2));
		return minutes;
	}

	public static void askForConfirmation(String question) {
		System.out.print(question + " (y to confirm) ");
		try (Scanner sc = new Scanner(System.in)) {
			String answer = sc.next();
			if (!"y".equals(answer)) {
				System.exit(0);
			}
		}
	}
}
