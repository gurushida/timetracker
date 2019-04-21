package com.sep.timetracker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class TestTimeTracker {

	@Test
	public void testDayType() throws ParseException {
		TimeTracker t = new TimeTracker();
		assertEquals(DayType.WORKING_DAY, t.getDayType("15/04/2019"));
		assertEquals(DayType.WEEK_END, t.getDayType("14/04/2019")); // Sunday
	}

	@Test
	public void testCannotAddVacationOnWeekEnd() throws ParseException, IOException {
		TimeTracker t = new TimeTracker();
		assertNotNull(t.addVacationDay(Util.DAY_FORMAT.parse("19/03/2011")));
	}

	@Test
	public void testCannotAddVacationDayTwice() throws ParseException, IOException {
		TimeTracker t = new TimeTracker(new Date(), new ReportedTime(), new Holidays(), new Vacations("15/04/2019"), new SickDays(), new SickChildDays());
		assertNotNull(t.addVacationDay(Util.DAY_FORMAT.parse("15/04/2019")));
	}

	@Test
	public void testCannotAddVacationDayOnHoliday() throws ParseException, IOException {
		TimeTracker t = new TimeTracker(new Date(), new ReportedTime(), new Holidays("15/04/2019"), new Vacations(), new SickDays(), new SickChildDays());
		assertNotNull(t.addVacationDay(Util.DAY_FORMAT.parse("15/04/2019")));
	}

	@Test
	public void testCanAddVacationDayOnHalfHoliday() throws ParseException, IOException {
		TimeTracker t = new TimeTracker(new Date(), new ReportedTime(), new Holidays("15/04/2019 1/2"), new Vacations(), new SickDays(), new SickChildDays());
		assertNull(t.addVacationDay(Util.DAY_FORMAT.parse("15/04/2019")));
	}

	@Test
	public void testCannotMoreThan25VacationDays1() throws ParseException, IOException {
		// If we already have registered 25 vacation days for a year,
		// we should not be able to add more
		Vacations vacations = new Vacations(
				"15/04/2019",
				"16/04/2019",
				"17/04/2019",
				"18/04/2019",
				"19/04/2019",

				"22/04/2019",
				"23/04/2019",
				"24/04/2019",
				"25/04/2019",
				"26/04/2019",

				"29/04/2019",
				"30/04/2019",
				"01/05/2019",
				"02/05/2019",
				"03/05/2019",

				"06/05/2019",
				"07/05/2019",
				"08/05/2019",
				"09/05/2019",
				"10/05/2019",

				"13/05/2019",
				"14/05/2019",
				"15/05/2019",
				"16/05/2019",
				"17/05/2019"
				);
		TimeTracker t = new TimeTracker(new Date(), new ReportedTime(), new Holidays(), vacations, new SickDays(), new SickChildDays());
		assertNotNull(t.addVacationDay(Util.DAY_FORMAT.parse("12/04/2019")));
	}

	@Test
	public void testCannotMoreThan25VacationDays2() throws ParseException, IOException {
		// If we already have registered 25 vacation days for a year but 2 of them
		// happen on half-work days, the total should count for 24 days and allow us
		// to add one day more
		Holidays holidays = new Holidays(
				"15/04/2019 1/2",
				"16/04/2019 1/2");

		Vacations vacations = new Vacations(
				"15/04/2019",
				"16/04/2019",
				"17/04/2019",
				"18/04/2019",
				"19/04/2019",

				"22/04/2019",
				"23/04/2019",
				"24/04/2019",
				"25/04/2019",
				"26/04/2019",

				"29/04/2019",
				"30/04/2019",
				"01/05/2019",
				"02/05/2019",
				"03/05/2019",

				"06/05/2019",
				"07/05/2019",
				"08/05/2019",
				"09/05/2019",
				"10/05/2019",

				"13/05/2019",
				"14/05/2019",
				"15/05/2019",
				"16/05/2019",
				"17/05/2019"
				);
		TimeTracker t = new TimeTracker(new Date(), new ReportedTime(), holidays, vacations, new SickDays(), new SickChildDays());
		assertNull(t.addVacationDay(Util.DAY_FORMAT.parse("12/04/2019")));
	}

	@Test
	public void testCannotMoreThan25VacationDays3() throws ParseException, IOException {
		// If we already have registered 25 vacation days for a year and 1 of them
		// happens on a half-work day, the total should count for 24.5 days and should
		// not allow us to add one day more on a regular work day
		Holidays holidays = new Holidays(
				"15/04/2019 1/2");

		Vacations vacations = new Vacations(
				"15/04/2019",
				"16/04/2019",
				"17/04/2019",
				"18/04/2019",
				"19/04/2019",

				"22/04/2019",
				"23/04/2019",
				"24/04/2019",
				"25/04/2019",
				"26/04/2019",

				"29/04/2019",
				"30/04/2019",
				"01/05/2019",
				"02/05/2019",
				"03/05/2019",

				"06/05/2019",
				"07/05/2019",
				"08/05/2019",
				"09/05/2019",
				"10/05/2019",

				"13/05/2019",
				"14/05/2019",
				"15/05/2019",
				"16/05/2019",
				"17/05/2019"
				);
		TimeTracker t = new TimeTracker(new Date(), new ReportedTime(), holidays, vacations, new SickDays(), new SickChildDays());
		assertNotNull(t.addVacationDay(Util.DAY_FORMAT.parse("12/04/2019")));
	}

	@Test
	public void testCannotMoreThan25VacationDays4() throws ParseException, IOException {
		// If we already have registered 25 vacation days for a year and 1 of them
		// happens on a half-work day, the total should count for 24.5 days and should
		// allow us to add one day more on a half-work day
		Holidays holidays = new Holidays(
				"12/04/2019 1/2",
				"15/04/2019 1/2");

		Vacations vacations = new Vacations(
				"15/04/2019",
				"16/04/2019",
				"17/04/2019",
				"18/04/2019",
				"19/04/2019",

				"22/04/2019",
				"23/04/2019",
				"24/04/2019",
				"25/04/2019",
				"26/04/2019",

				"29/04/2019",
				"30/04/2019",
				"01/05/2019",
				"02/05/2019",
				"03/05/2019",

				"06/05/2019",
				"07/05/2019",
				"08/05/2019",
				"09/05/2019",
				"10/05/2019",

				"13/05/2019",
				"14/05/2019",
				"15/05/2019",
				"16/05/2019",
				"17/05/2019"
				);
		TimeTracker t = new TimeTracker(new Date(), new ReportedTime(), holidays, vacations, new SickDays(), new SickChildDays());
		assertNull(t.addVacationDay(Util.DAY_FORMAT.parse("12/04/2019")));
	}
}
