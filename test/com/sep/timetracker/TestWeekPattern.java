package com.sep.timetracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestWeekPattern {

	@Test
	public void testGoodPatterns() {
		assertEquals(new Pair<>("Monday", 90), WeekPattern.parseDaySchedule("Mon=1:30"));
		assertEquals(new Pair<>("Monday", 0), WeekPattern.parseDaySchedule("Mon=00:00"));
		assertEquals(new Pair<>("Tuesday", 23 * 60), WeekPattern.parseDaySchedule("Tue=23:00"));
	}

	@Test
	public void testBadPatterns() {
		assertNull(WeekPattern.parseDaySchedule("abc"));
		assertNull(WeekPattern.parseDaySchedule("Mon="));
		assertNull(WeekPattern.parseDaySchedule("Mon=0"));
		assertNull(WeekPattern.parseDaySchedule("Mon=0:66"));
		assertNull(WeekPattern.parseDaySchedule("Mon=24:00"));
		assertNull(WeekPattern.parseDaySchedule("Mon=23:000"));
		assertNull(WeekPattern.parseDaySchedule("Mon=23:00a"));
		assertNull(WeekPattern.parseDaySchedule("mon=23:00"));
		assertNull(WeekPattern.parseDaySchedule("mon23:00"));
	}

	@Test
	public void testDefaultPattern() {
		WeekPattern p = new WeekPattern();
		assertEquals(7 * 60 + 30, p.getTimeToWork("Monday"));
		assertEquals(7 * 60 + 30, p.getTimeToWork("Tuesday"));
		assertEquals(7 * 60 + 30, p.getTimeToWork("Wednesday"));
		assertEquals(7 * 60 + 30, p.getTimeToWork("Thursday"));
		assertEquals(7 * 60 + 30, p.getTimeToWork("Friday"));
		assertEquals(0, p.getTimeToWork("Saturday"));
		assertEquals(0, p.getTimeToWork("Sunday"));
	}

	@Test
	public void testPattern() {
		WeekPattern p = new WeekPattern("Sun=6:00,Mon=0:00,Tue=10:00,Tue=8:00");
		assertEquals(0, p.getTimeToWork("Monday"));
		assertEquals(8 * 60, p.getTimeToWork("Tuesday"));
		assertEquals(0, p.getTimeToWork("Wednesday"));
		assertEquals(0, p.getTimeToWork("Thursday"));
		assertEquals(0, p.getTimeToWork("Friday"));
		assertEquals(0, p.getTimeToWork("Saturday"));
		assertEquals(6 * 60, p.getTimeToWork("Sunday"));
	}
}
