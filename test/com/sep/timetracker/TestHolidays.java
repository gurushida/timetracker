package com.sep.timetracker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;

import com.sep.timetracker.Holidays.HolidayType;

public class TestHolidays {

	@Test
	public void testHolidays() throws ParseException {
		ArrayList<String> list = new ArrayList<>();
		list.add("# foobar");
		list.add("12/03/2010");
		list.add("");
		list.add("13/03/2010 1/2");
		
		Holidays h = new Holidays(list);
		assertEquals(HolidayType.NOT_HOLIDAY, h.isHoliday("11/03/2010"));
		assertEquals(HolidayType.HOLIDAY, h.isHoliday("12/03/2010"));
		assertEquals(HolidayType.HALF_DAY, h.isHoliday("13/03/2010"));		
	}

	@Test
	public void testRemoveHolidays() throws ParseException {
		Holidays h = new Holidays("18/03/2010 1/2", "19/03/2010", "removed 18/03/2010", "removed 19/03/2010");
		assertEquals(HolidayType.NOT_HOLIDAY, h.isHoliday("18/03/2010"));
		assertEquals(HolidayType.NOT_HOLIDAY, h.isHoliday("19/03/2010"));
	}

	@Test
	public void testRemoveAndReAddHolidays() throws ParseException {
		Holidays h = new Holidays("18/03/2010 1/2", "19/03/2010", "removed 18/03/2010", "removed 19/03/2010", "18/03/2010", "19/03/2010 1/2");
		assertEquals(HolidayType.HOLIDAY, h.isHoliday("18/03/2010"));
		assertEquals(HolidayType.HALF_DAY, h.isHoliday("19/03/2010"));
	}


	@Test
	public void testBadHolidays1() throws ParseException {
		try {
			new Holidays("foobar");
			fail();
		} catch (Throwable e) {
		}
	}

	@Test
	public void testBadHolidays2() throws ParseException {
		try {
			new Holidays(" 1/2");
			fail();
		} catch (Throwable e) {
		}
	}

	@Test
	public void testBadHolidays3() throws ParseException {
		try {
			new Holidays("11/02/2010 abc");
			fail();
		} catch (Throwable e) {
		}
	}

	@Test
	public void testBadHolidays4() throws ParseException {
		try {
			new Holidays("11/02/2010 abc 1/2");
			fail();
		} catch (Throwable e) {
		}
	}
}
