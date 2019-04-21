package com.sep.timetracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
