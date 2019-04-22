package com.sep.timetracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sep.timetracker.Holidays.HolidayType;

public class TimeTracker {

	private final Config config;
	private final ReportedTime reportedTime;
	private final Holidays holidays;
	private final Vacations vacations;
	private final SickDays sickDays;
	private final SickChildDays sickChildDays;
	
	public TimeTracker() throws ParseException {
		this(new Config(), new ReportedTime(), new Holidays(), new Vacations(), new SickDays(), new SickChildDays());
	}

	public TimeTracker(File directory) throws ParseException, FileNotFoundException {
		this(new Config(new File(directory, Config.FILENAME)),
				new ReportedTime(new File(directory, ReportedTime.FILENAME)),
				new Holidays(new File(directory, Holidays.FILENAME)),
				new Vacations(new File(directory, Vacations.FILENAME)),
				new SickDays(new File(directory, SickDays.FILENAME)),
				new SickChildDays(new File(directory, SickChildDays.FILENAME)));
	}
	
	public TimeTracker(Config config, ReportedTime reportedTime, Holidays holidays, Vacations vacations, SickDays sickDays, SickChildDays sickChildDays) {
		this.config = config;
		this.reportedTime = reportedTime;
		this.holidays = holidays;
		this.vacations = vacations;
		this.sickDays = sickDays;
		this.sickChildDays = sickChildDays;
	}

	public void printReport(Report report, Date dateEnd, boolean printComments) throws ParseException {
		Date dateStart = config.getTrackingStart();
		if (dateStart.after(dateEnd)) {
			System.err.println("Cannot print report: end date " + Util.DAY_FORMAT.format(dateEnd) + " is before start date " + Util.DAY_FORMAT.format(dateStart));
			System.exit(1);
		}
		String normalizedStart = Util.DAY_FORMAT.format(dateStart);
		dateStart = Util.DAY_FORMAT.parse(normalizedStart);
		
		String normalizedEnd = Util.DAY_FORMAT.format(dateEnd);
		dateEnd = Util.DAY_FORMAT.parse(normalizedEnd);
		
		Date startOfWeek = Util.startOfWeek(dateEnd);
		Date reportStart;
		switch(report) {
		case WEEK: reportStart = startOfWeek; break;
		case MONTH: reportStart = Util.startOfMonth(dateEnd); break;
		case YEAR: reportStart = Util.startOfYear(dateEnd); break;
		case ALL: reportStart = dateStart; break;
		default: throw new IllegalArgumentException();
		}
		
		if (reportStart.compareTo(config.getTrackingStart()) < 0) {
			System.err.println("Cannot start report at " + Util.DAY_FORMAT.format(reportStart)
				+ " as time tracking started on " + Util.DAY_FORMAT.format(dateStart));
			reportStart = dateStart;
		}

		String current = normalizedStart;
		long totalAmountDue = 0;
		long totalAmountWorked = 0;
		long totalAmountDueThisWeek = 0;
		long totalAmountWorkedThisWeek = 0;

		Date currentDate = dateStart;
		while (currentDate.compareTo(dateEnd) <= 0) {
			DayType dayType = getDayType(current);
			long minutesWorked = reportedTime.getTimeWorkedInMinutes(current);
			if (currentDate.compareTo(reportStart) >= 0) {
				printReportLine(currentDate, dayType, minutesWorked, printComments);
			}
			
			totalAmountDue += dayType.minutesToWork;
			totalAmountWorked += minutesWorked;
			if (currentDate.compareTo(startOfWeek) >= 0) {
				totalAmountDueThisWeek += dayType.minutesToWork;
				totalAmountWorkedThisWeek += minutesWorked;
			}

			currentDate = Util.nextDay(currentDate);
			current = Util.DAY_FORMAT.format(currentDate);
		}
		double balance = totalAmountWorked - totalAmountDue;
		double days = balance / (double)Util.WORKING_DAY;
		System.out.println();
		System.out.println(String.format("So far this week: %.1fh/%.1fh worked", totalAmountWorkedThisWeek / 60.0, totalAmountDueThisWeek / 60.0));
		System.out.println(String.format("Total overtime:   %.1f days (from %s to %s)", days,
				Util.DAY_FORMAT.format(config.getTrackingStart()), Util.DAY_FORMAT.format(dateEnd)));
		System.out.println();
	}

	private void printReportLine(Date currentDate, DayType dayType, long minutesWorked, boolean printComments) {
		String dayName = Util.getDay(currentDate);
		StringBuilder b = new StringBuilder();
		b.append(dayName);
		switch(dayName) {
		case "Monday": b.append("    "); break;
		case "Tuesday": b.append("   "); break;
		case "Wednesday": b.append(" "); break;
		case "Thursday": b.append("  "); break;
		case "Friday": b.append("    "); break;
		case "Saturday": b.append("  "); break;
		case "Sunday": b.append("    "); break;
		default: throw new IllegalStateException();
		}
		b.append(Util.DAY_FORMAT.format(currentDate));
		b.append(": ");
		List<String> comments = new ArrayList<>();
		switch(dayType) {
		case SICKNESS:       b.append("sick        "); break;
		case CHILD_SICKNESS: b.append("sick child  "); break;
		case VACATION:       b.append("vacation    "); break;
		case HALF_WORKING_DAY:
		case HOLIDAY:{
			b.append(dayType == DayType.HOLIDAY ? "holiday     " : "1/2 holiday ");
			String c = holidays.getHolidayDescription(currentDate);
			if (c != null) {
				comments.add(c);
			}
			break;
		}
		case WORKING_DAY:
		case WEEK_END:       b.append("            "); break;
		default: throw new IllegalStateException();
		}
		
		long hourQuartersDue = dayType.minutesToWork / 15;
		long hourQuartersWorked = minutesWorked / 15;
		long delta = hourQuartersWorked - hourQuartersDue;
		if (delta <= 0) {
			for (int i = 0 ; i < hourQuartersWorked ; i++) {
				b.append("#");
			}
			for (int i = 0 ; i < -delta ; i++) {
				b.append("-");
			} 
		} else {
			for (int i = 0 ; i < hourQuartersDue ; i++) {
				b.append("#");
			}
			for (int i = 0 ; i < delta ; i++) {
				b.append("+");
			}
		}
		List<String> list = reportedTime.getComments(currentDate);
		if (list != null) {
			comments.addAll(list);
		}
		
		System.out.println(b.toString());
		if (printComments) {
			for (String comment : comments) {
				System.out.println("                      > " + comment);
			}
		}

		if ("Sunday".equals(dayName)) {
			System.out.println();
		}
	}
	
	/**
	 * Given a date, return the type of day it is.
	 */
	public DayType getDayType(String d) throws ParseException {
		return getDayType(Util.DAY_FORMAT.parse(d));
	}
	
	/**
	 * Given a date, return the type of day it is.
	 */
	public DayType getDayType(Date d) {
		if (Util.isWeekEndDay(d)) {
			return DayType.WEEK_END;
		}
		
		if (sickDays.isSickDay(d)) {
			return DayType.SICKNESS;
		}
		
		if (sickChildDays.isSickChildDay(d)) {
			return DayType.CHILD_SICKNESS;
		}
		
		if (vacations.isVacationDay(d)) {
			return DayType.VACATION;
		}
		
		switch (holidays.isHoliday(d)) {
		case NOT_HOLIDAY: return DayType.WORKING_DAY;
		case HALF_DAY: return DayType.HALF_WORKING_DAY;
		case HOLIDAY: return DayType.HOLIDAY;
		}
		
		throw new IllegalStateException();
	}
	
	/**
	 * Adds the given date as a vacation day and returns null;
	 * Does nothing and returns an error message if the given day is a week-end day,
	 * is a full holiday day, is already a vacation day or if this
	 * vacation day would overflow the annual days/year limit.
	 */
	public String addVacationDay(Date d) throws ParseException, IOException {
		if (Util.isWeekEndDay(d))  {
			return "Cannot add vacation day on " + Util.DAY_FORMAT.format(d) + " as it is a " + Util.getDay(d);
		}
		if (vacations.isVacationDay(d)) {
			return "Cannot add vacation day on " + Util.DAY_FORMAT.format(d) + " as it is already marked as a vacation day";
		}

		double vacationsDays = 0;

		switch (holidays.isHoliday(d)) {
		case HOLIDAY: return "Cannot add vacation day on " + Util.DAY_FORMAT.format(d) + " as it is a public holiday";
		case HALF_DAY: vacationsDays += 0.5; break;
		case NOT_HOLIDAY: vacationsDays += 1.0; break;
		}
		
		int year = Util.getYear(d);
		List<String> vacationsThisYear = vacations.getVacationDays(year);
		for (String vacationDay : vacationsThisYear) {
			switch (holidays.isHoliday(vacationDay)) {
			case NOT_HOLIDAY: vacationsDays += 1.0; break;
			case HALF_DAY: vacationsDays += 0.5; break;
			default: throw new IllegalStateException();
			}
		}
		
		if (vacationsDays > config.getVacationDaysPerYear()) {
			return "Cannot add vacation day on " + Util.DAY_FORMAT.format(d) + " as it would exceed the "
					+ config.getVacationDaysPerYear() + " vacation days per year limit";
		}
		
		vacations.addVacationDay(d);
		return null;
	}

	/**
	 * Adds the given date as a sick day and returns null;
	 * Does nothing and returns an error message if the given day is a week-end day,
	 * is a full holiday day, is already a sick day or a child sick day.
	 */
	public String addSickDay(Date d) throws ParseException, IOException {
		if (Util.isWeekEndDay(d))  {
			return "Cannot add sick day on " + Util.DAY_FORMAT.format(d) + " as it is a " + Util.getDay(d);
		}

		if (sickDays.isSickDay(d)) {
			return "Cannot add sick day on " + Util.DAY_FORMAT.format(d) + " as it is already marked as a sick day";
		}

		if (sickChildDays.isSickChildDay(d)) {
			return "Cannot add sick day on " + Util.DAY_FORMAT.format(d) + " as it is already marked as a sick child day";
		}

		if (HolidayType.HOLIDAY == holidays.isHoliday(d)) {
			return "Cannot add sick day on " + Util.DAY_FORMAT.format(d) + " as it is a public holiday";
		}

		sickDays.addSickDay(d);
		return null;
	}

	/**
	 * Adds the given date as a sick child day and returns null;
	 * Does nothing and returns an error message if the given day is a week-end day,
	 * is a full holiday day, is already a sick day or a child sick day.
	 */
	public String addSickChildDay(Date d) throws ParseException, IOException {
		if (Util.isWeekEndDay(d))  {
			return "Cannot add sick child day on " + Util.DAY_FORMAT.format(d) + " as it is a " + Util.getDay(d);
		}

		if (sickDays.isSickDay(d)) {
			return "Cannot add child sick day on " + Util.DAY_FORMAT.format(d) + " as it is already marked as a sick day";
		}

		if (sickChildDays.isSickChildDay(d)) {
			return "Cannot add child sick day on " + Util.DAY_FORMAT.format(d) + " as it is already marked as a sick child day";
		}

		if (HolidayType.HOLIDAY == holidays.isHoliday(d)) {
			return "Cannot add sick child day on " + Util.DAY_FORMAT.format(d) + " as it is a public holiday";
		}

		sickChildDays.addSickChildDay(d);
		return null;
	}

	/**
	 * Adds the given date as a public holiday;
	 * Does nothing and returns an error message if the given day is
	 * already a public holiday.
	 */
	public String addHoliday(Date d, boolean halfDay, String description) throws ParseException, IOException {
		if (HolidayType.NOT_HOLIDAY != holidays.isHoliday(d)) {
			return "Cannot add holiday on " + Util.DAY_FORMAT.format(d) + " as it is already registered as a public holiday";
		}

		holidays.addHoliday(d, halfDay, description);
		return null;
	}

	public void reportWorkedTime(Date start, Date end, String description) throws IOException {
		reportedTime.reportWorkedTimeInterval(start, end, description);
	}

	/**
	 * Removes the given vacation day. Asks for confirmation unless force is true.
	 * Returns null if the removal is done; otherwise returns an error message.
	 */
	public String removeVacationDay(Date d, boolean force) throws IOException {
		if (!vacations.isVacationDay(d)) {
			return "Cannot remove " + Util.DAY_FORMAT.format(d) + " as it is not registered as a vacation day !";
		}
		if (!force) {
			Util.askForConfirmation("Are you sure you want to remove the vacation day " + Util.DAY_FORMAT.format(d) + " ?");
		}
		vacations.removeVacationDay(d);
		return null;
	}

	/**
	 * Removes the given holiday. Asks for confirmation unless force is true.
	 * Returns null if the removal is done; otherwise returns an error message.
	 */
	public String removeHoliday(Date d, boolean force) throws IOException {
		if (HolidayType.NOT_HOLIDAY == holidays.isHoliday(d)) {
			return "Cannot remove " + Util.DAY_FORMAT.format(d) + " as it is not registered as a holiday !";
		}
		if (!force) {
			Util.askForConfirmation("Are you sure you want to remove the " + (HolidayType.HALF_DAY == holidays.isHoliday(d) ? "half " : "")
					+ "holiday " + Util.DAY_FORMAT.format(d) + " ?");
		}
		holidays.removeHoliday(d);
		return null;
	}

	/**
	 * Removes the given sick child day. Asks for confirmation unless force is true.
	 * Returns null if the removal is done; otherwise returns an error message.
	 */
	public String removeSickChildDay(Date d, boolean force) throws IOException {
		if (!sickChildDays.isSickChildDay(d)) {
			return "Cannot remove " + Util.DAY_FORMAT.format(d) + " as it is not registered as a sick child day !";
		}
		if (!force) {
			Util.askForConfirmation("Are you sure you want to remove the sick child day " + Util.DAY_FORMAT.format(d) + " ?");
		}
		sickChildDays.removeSickChildDay(d);
		return null;
	}
}
