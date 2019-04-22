package com.sep.timetracker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;

import com.sep.timetracker.ReportedTime;

public class TestReportedTime {

	@Test
	public void testReportedTime() throws ParseException {
		ArrayList<String> list = new ArrayList<>();
		list.add("# foobar");
		list.add("12/03/2010 13:49 +0100 --> 12/03/2010 13:59 +0100");
		list.add("");
		list.add("12/03/2010 16:49 +0100 --> 12/03/2010 18:49 +0100");
		list.add("12/03/2010 21:00 +0100 --> 13/03/2010 01:00 +0100");
		
		ReportedTime rt = new ReportedTime(list);
		assertEquals(370, rt.getTimeWorkedInMinutes("12/03/2010"));
	}

	@Test
	public void testBadReportedTime1() throws ParseException {
		try {
			new ReportedTime("foobar");
		} catch (Throwable e) {
			return;
		}
		fail();
	}

	@Test
	public void testBadReportedTime2() throws ParseException {
		try {
			new ReportedTime("12/03/2010 13:49 +0100 --> ");
		} catch (Throwable e) {
			return;
		}
		fail();
	}

	@Test
	public void testBadReportedTime3() throws ParseException {
		try {
			new ReportedTime("12/03/2010 13:49 +0100 -> 12/03/2010 13:59 +0100");
		} catch (Throwable e) {
			return;
		}
		fail();
	}

	@Test
	public void testBadReportedTime4() throws ParseException {
		try {
			new ReportedTime("12/03/2010 13:49 +0100 --> 10/03/2010 13:59 +0100");
		} catch (Throwable e) {
			return;
		}
		fail();
	}
}
