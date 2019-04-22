package com.sep.timetracker;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;

public class Main {

	public static File dir = new File(System.getProperty("user.home"), ".timetracker");

	public static void main(String[] args) throws ParseException, IOException {
		if (!GitUtil.isGitPresent()) {
			System.err.println("git not found ! This time tracker application needs git to work as intended.");
			System.err.println();
			System.exit(1);
		}

		if (!dir.exists() && (args.length == 0 || !"init".equals(args[0]))) {
			System.err.println("Time tracker configuration not found !\n");
			System.err.println("Initialize it with 'init <start>' where <start> is the date to start time tracking from in dd/mm/yyyy format");
			System.err.println();
			System.exit(1);
		}

		if (args.length == 0) {
			printHelp();
			return;
		}

		if ("init".equals(args[0])) {
			initialize(args);
			return;
		}

		TimeTracker timeTracker = new TimeTracker(dir);

		switch(args[0]) {
		case "help": printHelp(); return;
		case "report": printReport(timeTracker, args); return;
		case "vacation": addVacation(timeTracker, args); return;
		case "sick": addSickDay(timeTracker, args); return;
		case "child": addSickChildDay(timeTracker, args); return;
		case "holiday": addHoliday(timeTracker, args); return;
		case "start": startTimeTracking(); return;
		case "stop": stopTimeTracking(timeTracker, args); return;
		case "add": reportTime(timeTracker, args); return;
		default: {
			System.err.println("Unknown command: '" + args[0] + "'");
			System.exit(1);
		}
		}
	}

	private static void startTimeTracking() throws IOException, ParseException {
		File f = new File(dir, CurrentTrackingStart.FILENAME);
		if (f.exists()) {
			System.err.println("There is already a time tracking session going on !");
			System.err.println("You must stop it before you can start a new one.");
			System.err.println();
			System.exit(1);
		}

		Date now = new Date();
		CurrentTrackingStart.startTracking(f, now);
		System.out.println("Starting to time track from " + Util.TIME_FORMAT.format(now));
		System.out.println();
	}

	private static void stopTimeTracking(TimeTracker timeTracker, String[] args) throws IOException, ParseException {
		GitUtil.checkUncommittedChanges(dir);

		boolean force = args.length > 1 && "--force".equals(args[1]);
		int descriptionPos = force ? 2 : 1;
		String description = descriptionPos < args.length ? args[descriptionPos] : null;

		File f = new File(dir, CurrentTrackingStart.FILENAME);
		if (!f.exists()) {
			System.err.println("There is no time tracking session going on !");
			System.err.println();
			System.exit(1);
		}

		CurrentTrackingStart c = new CurrentTrackingStart(f);
		Date start = c.getCurrentTrackingStart();
		Date now = new Date();
		if (!now.after(start)) {
			System.err.println("The start of the current time tracking session is in the future: " + Util.TIME_FORMAT.format(start) + " !");
			System.err.println("Travel in time, delete " + f.getAbsolutePath() + " or fix this file manually with great care.");
			System.err.println();
			System.exit(1);
		}

		Duration dur = Duration.between(start.toInstant(), now.toInstant());
		if (dur.toHours() >= 24) {
			System.err.println("More than 24 hours have elapsed since the start of the current time tracking session: " + Util.TIME_FORMAT.format(start));
			System.err.println("This is highly unlikely to be correct, so either delete " + f.getAbsolutePath() + " or fix this file manually with great care.");
			System.exit(1);
		}

		if (!force && !Util.DAY_FORMAT.format(start).equals(Util.DAY_FORMAT.format(now))) {
			System.err.println("The current time tracking session did not start today: " + Util.TIME_FORMAT.format(start));
			System.err.println("Re-run with stop --force [description] if this is not a mistake");
			System.err.println();
			System.exit(1);
		}

		long hours = dur.toMinutes() / 60;
		long minutes = dur.toMinutes() % 60;
		String workedTime = "";
		if (hours > 1) {
			workedTime = hours + " hours and ";
		} else if (hours == 1) {
			workedTime = "1 hour and ";
		} else {
			workedTime = "";
		}
		workedTime += minutes + " minute" + (minutes > 1 ? "s" : "");

		if (!force && dur.toHours() >= 10) {
			System.err.println("The reported worked time would be " + workedTime);
			System.err.println("Re-run with stop --force [description] if this is not a mistake");
			System.err.println();
			System.exit(1);
		}

		f.delete();
		timeTracker.reportWorkedTime(start, now, description);
		String msg = "Reported worked time: " + workedTime;
		System.out.println(msg);
		System.out.println();

		GitUtil.addAllAndCommit(dir, msg);
		new TimeTracker(dir).printReport(Report.WEEK, new Date());
	}

	private static void reportTime(TimeTracker timeTracker, String[] args) throws IOException, ParseException {
		GitUtil.checkUncommittedChanges(dir);

		boolean force = args.length > 1 && "--force".equals(args[1]);
		int argPos = force ? 2 : 1;
		if (argPos == args.length) {
			System.err.println("Missing <day> argument");
			System.err.println();
			System.exit(1);
		}

		Date d;
		try {
			d = Util.DAY_FORMAT.parse(args[argPos]);
		} catch (ParseException e) {
			System.err.println(args[argPos] + " is not a valid dd/mm/yyyy date");
			System.err.println();
			System.exit(1);
			// The compiler is not smart enough to figure out that System.exit()
			// means leaving the method so we need a return to prevent it
			// from complaining below about d not being initialized
			return;
		}

		argPos++;

		if (argPos == args.length) {
			System.err.println("Missing <duration> argument");
			System.err.println();
			System.exit(1);
		}
		Integer duration = Util.getDurationInMinutes(args[argPos]);
		if (duration == null) {
			System.err.println(args[argPos] + " is not a valid hh:mm duration");
			System.err.println();
			System.exit(1);
		}

		if (duration >= 24 * 60) {
			System.err.println(args[argPos] + " is larger than 24 hours. This is highly unlikely be correct.");
			System.err.println();
			System.exit(1);
		}

		if (!force && duration >= 10 * 60) {
			System.err.println("The reported worked time is greater than 10 hours.");
			System.err.println("Re-run with add --force <day> <duration> [description] if this is not a mistake");
			System.err.println();
			System.exit(1);
		}

		argPos++;
		String description = null;
		if (argPos < args.length) {
			description = args[argPos];
		}

		long hours = duration / 60;
		long minutes = duration % 60;
		String workedTime = "";
		if (hours > 1) {
			workedTime = hours + " hours and ";
		} else if (hours == 1) {
			workedTime = "1 hour and ";
		} else {
			workedTime = "";
		}
		workedTime += minutes + " minute" + (minutes > 1 ? "s" : "");

		Date end = Util.addMinutes(d, duration);
		timeTracker.reportWorkedTime(d, end, description);
		String msg = "Reported worked time: " + workedTime;
		System.out.println(msg);
		System.out.println();

		GitUtil.addAllAndCommit(dir, msg);
		new TimeTracker(dir).printReport(Report.WEEK, new Date());
	}

	private static void addVacation(TimeTracker timeTracker, String[] args) throws ParseException, IOException {
		GitUtil.checkUncommittedChanges(dir);
		if (args.length < 2) {
			System.err.println("Missing <day> argument");
			System.err.println();
			System.exit(1);
		}

		Date d;
		try {
			d = Util.DAY_FORMAT.parse(args[1]);
		} catch (ParseException e) {
			System.err.println(args[1] + " is not a valid dd/mm/yyyy date");
			System.err.println();
			System.exit(1);
			// See comment in reportTime()
			return;
		}
		String err = timeTracker.addVacationDay(d);
		if (err != null) {
			System.err.println(err);
			System.err.println();
			System.exit(1);
		}
		String msg = "Vacation day added for " + args[1];
		System.out.println(msg);
		System.out.println();
		GitUtil.addAllAndCommit(dir, msg);
	}

	private static void addSickDay(TimeTracker timeTracker, String[] args) throws ParseException, IOException {
		GitUtil.checkUncommittedChanges(dir);
		if (args.length < 2) {
			System.err.println("Missing <day> argument");
			System.err.println();
			System.exit(1);
		}

		Date d;
		try {
			d = Util.DAY_FORMAT.parse(args[1]);
		} catch (ParseException e) {
			System.err.println(args[1] + " is not a valid dd/mm/yyyy date");
			System.err.println();
			// See comment in reportTime()
			System.exit(1);
			return;
		}
		String err = timeTracker.addSickDay(d);
		if (err != null) {
			System.err.println(err);
			System.err.println();
			System.exit(1);
		}
		String msg = "Sick day added for " + args[1];
		System.out.println(msg);
		System.out.println();
		GitUtil.addAllAndCommit(dir, msg);
	}

	private static void addSickChildDay(TimeTracker timeTracker, String[] args) throws ParseException, IOException {
		GitUtil.checkUncommittedChanges(dir);
		if (args.length < 2) {
			System.err.println("Missing <day> argument");
			System.err.println();
			System.exit(1);
		}

		Date d;
		try {
			d = Util.DAY_FORMAT.parse(args[1]);
		} catch (ParseException e) {
			System.err.println(args[1] + " is not a valid dd/mm/yyyy date");
			System.err.println();
			System.exit(1);
			// See comment in reportTime()
			return;
		}
		String err = timeTracker.addSickChildDay(d);
		if (err != null) {
			System.err.println(err);
			System.err.println();
			System.exit(1);
		}
		String msg = "Sick child day added for " + args[1];
		System.out.println(msg);
		System.out.println();
		GitUtil.addAllAndCommit(dir, msg);
	}

	private static void addHoliday(TimeTracker timeTracker, String[] args) throws ParseException, IOException {
		GitUtil.checkUncommittedChanges(dir);
		boolean halfDay = false;

		int pos = 1;
		if (pos < args.length && "--half".equals(args[pos])) {
			pos++;
			halfDay = true;
		}
		if (pos == args.length) {
			System.err.println("Missing <day> argument");
			System.err.println();
			System.exit(1);
		}

		Date d;
		try {
			d = Util.DAY_FORMAT.parse(args[pos]);
		} catch (ParseException e) {
			System.err.println(args[1] + " is not a valid dd/mm/yyyy date");
			System.err.println();
			System.exit(1);
			// See comment in reportTime()
			return;
		}
		pos++;
		String description = null;
		if (pos < args.length) {
			description = args[pos];
		}

		String err = timeTracker.addHoliday(d, halfDay, description);
		if (err != null) {
			System.err.println(err);
			System.err.println();
			System.exit(1);
		}
		String msg = "Public " + (halfDay ? "1/2 " : "") + "holiday added for " + Util.DAY_FORMAT.format(d);
		System.out.println(msg);
		System.out.println();
		GitUtil.addAllAndCommit(dir, msg);
	}

	private static void printReport(TimeTracker timeTracker, String[] args) throws ParseException {
		Date reportEnd = null;
		Report report = Report.WEEK;
		if (args.length > 1) {
			switch (args[1]) {
			case "week": break;
			case "month": report = Report.MONTH; break;
			case "year": report = Report.YEAR; break;
			case "all": report = Report.ALL; break;
			default: {
				try {
					reportEnd = Util.DAY_FORMAT.parse(args[1]);
				} catch (ParseException e) {
					System.err.println(args[1] + " is neither a valid dd/mm/yyyy date nor in [week, month, year, all]");
					System.err.println();
					System.exit(1);
					// See comment in reportTime()
					return;
				}
			}
			}
		}
		if (reportEnd == null) {
			if (args.length > 2) {
				try {
					reportEnd = Util.DAY_FORMAT.parse(args[2]);
				} catch (ParseException e) {
					System.err.println(args[2] + " is not a valid dd/mm/yyyy date");
					System.err.println();
					System.exit(1);
					// See comment in reportTime()
					return;
				}
			} else {
				reportEnd = new Date(); // now
			}
		}
		timeTracker.printReport(report, reportEnd);
	}

	private static void initialize(String[] args) throws IOException, ParseException {
		if (dir.exists()) {
			System.err.println("Configuration already exists !");
			System.err.println("If you are sure to want to replace it and lose all your data, you need to manually delete the directory " + dir.getAbsolutePath());
			System.err.println();
			System.exit(1);
		}

		if (args.length < 2) {
			System.err.println("Missing <start> argument");
			System.err.println();
			System.exit(1);
		}

		Date d;
		try {
			d = Util.DAY_FORMAT.parse(args[1]);
		} catch (ParseException e) {
			System.err.println(args[1] + " is not a valid dd/mm/yyyy date");
			System.err.println();
			System.exit(1);
			// See comment in reportTime()
			return;
		}
		dir.mkdir();

		TrackingStart.initialize(new File(dir, TrackingStart.FILENAME), d);
		Holidays.initialize(new File(dir, Holidays.FILENAME));
		Vacations.initialize(new File(dir, Vacations.FILENAME));
		SickDays.initialize(new File(dir, SickDays.FILENAME));
		SickChildDays.initialize(new File(dir, SickChildDays.FILENAME));
		ReportedTime.initialize(new File(dir, ReportedTime.FILENAME));
		GitUtil.init(dir);
	}

	private static void printHelp() {
		System.out.println("Time tracking utility that keeps track of things with git");
		System.out.println();
		System.out.println("Commands:");
		System.out.println();
		System.out.println("  help             Prints this basic help.");
		if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
			System.out.println("                   Use 'man tt' for more details.");
		}
		System.out.println();
		System.out.println("  start            Starts tracking time from now.");
		System.out.println();
		System.out.println("  stop [description]");
		System.out.println("                   Stops the current time tracking. The description is optional and is only");
		System.out.println("                   meant as a hint for humans.");
		System.out.println();
		System.out.println("  add <day> <duration> [description]");
		System.out.println("                   Registers an amount of worked item. <day> is given in dd/mm/yyyy format.");
		System.out.println("                   <duration> is the amount of worked time in the hh:mm format. The description");
		System.out.println("                   is optional and is only meant as a hint for humans.");
		System.out.println();
		System.out.println("  holiday [--half] <day> [description]");
		System.out.println("                   Registers a public holiday given in dd/mm/yyyy format. If --half is used,");
		System.out.println("                   it will be registered as a half day. The description is optional and is");
		System.out.println("                   only meant as a hint for humans.");
		System.out.println();
		System.out.println("  vacation <day>   Registers a vacation day given in dd/mm/yyyy format.");
		System.out.println();
		System.out.println("  sick <day>       Registers a sick day given in dd/mm/yyyy format.");
		System.out.println();
		System.out.println("  child <day>      Registers a sick child day given in dd/mm/yyyy format.");
		System.out.println();
		System.out.println("  report [<type>] [<end>]");
		System.out.println("                   Prints a summary of the time tracking up until now or <end> if specified in");
		System.out.println("                   dd/mm/yyyy format. <type> can have the following values:");
		System.out.println();
		System.out.println("                     week   Prints the current week (default)");
		System.out.println("                     month  Prints the current month");
		System.out.println("                     year   Prints the current year");
		System.out.println("                     all    Prints everyting since the time tracking began");
		System.out.println();
		System.out.println("                   Legend:");
		System.out.println("                     ---    Hours due and not worked");
		System.out.println("                     ###    Hours due and worked");
		System.out.println("                     +++    Overtime hours (any time worked past the expected duration for the day)");
		System.out.println();
		System.out.println("  init <start>     Initializes time tracking. <start> is the date to start time tracking from");
		System.out.println("                   in dd/mm/yyyy format.");
		System.out.println();
	}
}
