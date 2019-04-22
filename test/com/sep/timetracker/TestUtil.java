package com.sep.timetracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class TestUtil {

	@Test
	public void testGoodDurationPattern() {
		assertEquals(Integer.valueOf(83), Util.getDurationInMinutes("1:23"));
	}

	@Test
	public void testBadDurationPatterns() {
		assertNull(Util.getDurationInMinutes("1:2"));
		assertNull(Util.getDurationInMinutes("1:234"));
		assertNull(Util.getDurationInMinutes("1:23 "));
		assertNull(Util.getDurationInMinutes("1:a3"));
		assertNull(Util.getDurationInMinutes(":23"));
		assertNull(Util.getDurationInMinutes(" 1:23 "));
	}

	@Test
	public void testDurationBetween() throws ParseException {
		// Both dates are outside daylight savings
		assertEquals(4 * 60L, Util.between(Util.TIME_FORMAT.parse("30/03/2019 00:10 +0100"), Util.TIME_FORMAT.parse("30/03/2019 04:10 +0100")));

		// Both dates have daylight savings
		assertEquals(4 * 60L, Util.between(Util.TIME_FORMAT.parse("31/03/2019 12:10 +0200"), Util.TIME_FORMAT.parse("31/03/2019 16:10 +0200")));

		// Start is before daylight savings, end is after
		assertEquals(3 * 60L, Util.between(Util.TIME_FORMAT.parse("31/03/2019 00:10 +0100"), Util.TIME_FORMAT.parse("31/03/2019 04:10 +0200")));
		assertEquals(1L, Util.between(Util.TIME_FORMAT.parse("31/03/2019 01:59 +0100"), Util.TIME_FORMAT.parse("31/03/2019 03:00 +0200")));

		// Start is in daylight savings, end is after
		assertEquals(5 * 60L, Util.between(Util.TIME_FORMAT.parse("27/10/2019 00:10 +0200"), Util.TIME_FORMAT.parse("27/10/2019 04:10 +0100")));
		assertEquals(1L, Util.between(Util.TIME_FORMAT.parse("27/10/2019 03:00 +0200"), Util.TIME_FORMAT.parse("27/10/2019 02:01 +0100")));
		assertEquals(61L, Util.between(Util.TIME_FORMAT.parse("27/10/2019 02:10 +0200"), Util.TIME_FORMAT.parse("27/10/2019 02:11 +0100")));
		// If we start the stopwatch in Paris and stop it 10 hours later in Nwe York, we expect a duration of 10 hours
		assertEquals(10 * 60L, Util.between(Util.TIME_FORMAT.parse("22/04/2019 10:30 +0200"), Util.TIME_FORMAT.parse("22/04/2019 14:30 -0400")));
	}

	@Test
	public void testAddMinutes1() throws ParseException {
		// 00:10 on a normal day
		Date start = Util.TIME_FORMAT.parse("20/03/2019 00:10 +0100");
		Date end = Util.addMinutes(start, 600);
		assertEquals("20/03/2019 10:10 +0100", Util.TIME_FORMAT.format(end));
		assertEquals(600L, Util.between(start, end));
	}

	@Test
	public void testAddMinutes2() throws ParseException {
		// 23:10 on a normal day
		Date start = Util.TIME_FORMAT.parse("20/03/2019 23:10 +0100");
		// 10 hours later means 09:10 on the next day
		Date end = Util.addMinutes(start, 600);
		assertEquals("21/03/2019 09:10 +0100", Util.TIME_FORMAT.format(end));
		assertEquals(600L, Util.between(start, end));
	}

	@Test
	public void testAddMinutes3() throws ParseException {
		// 00:10 on the day we change to daylight savings in Western Europe
		Date start = Util.TIME_FORMAT.parse("31/03/2019 00:10 +0100");
		// So 10 hours later we have changed daylight savings
		Date end = Util.addMinutes(start, 600);
		assertEquals("31/03/2019 11:10 +0200", Util.TIME_FORMAT.format(end));
		assertEquals(600L, Util.between(start, end));
	}

	@Test
	public void testAddMinutes4() throws ParseException {
		// 00:10 on the day we change back from daylight savings in Western Europe
		Date start = Util.TIME_FORMAT.parse("27/10/2019 00:10 +0200");
		// So 10 hours later we have changed daylight savings
		Date end = Util.addMinutes(start, 600);
		assertEquals("27/10/2019 09:10 +0100", Util.TIME_FORMAT.format(end));
		assertEquals(600L, Util.between(start, end));
	}
}
