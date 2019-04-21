package com.sep.timetracker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;

import com.sep.timetracker.Vacations;

public class TestVacations {

	@Test
	public void testVacations() throws ParseException {
		Vacations v = new Vacations("# foobar", "12/03/2010", "10/03/2010 #comment");
		assertFalse(v.isVacationDay("11/03/2010"));
		assertTrue(v.isVacationDay("12/03/2010"));
	}

	@Test
	public void testBadVacations1() throws ParseException {
		try {
			new Vacations("foobar");
		} catch (Throwable e) {
			return;
		}
		fail();
	}

	@Test
	public void testBadVacations2() throws ParseException {
		try {
			new Vacations("14/04/2019"); // This is a Sunday, which should not be allowed as a valid vacation date
		} catch (Throwable e) {
			return;
		}
		fail();
	}

	@Test
	public void testBadVacations3() throws ParseException {
		try {
			new Vacations("11/02/2010 abc");
		} catch (Throwable e) {
			return;
		}
		fail();
	}

	@Test
	public void testVacationsThisYear() throws ParseException {
		ArrayList<String> list = new ArrayList<>();
		list.add("15/03/2010");
		list.add("16/03/2010");
		list.add("17/03/2010");
		list.add("18/03/2010");
		list.add("19/03/2010");
		
		list.add("17/03/2011");
		list.add("18/03/2011");
		
		Vacations v = new Vacations(list);
		assertEquals(5, v.getVacationDays(2010).size());
		assertEquals(2, v.getVacationDays(2011).size());
	}

}
