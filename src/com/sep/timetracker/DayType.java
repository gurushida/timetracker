package com.sep.timetracker;

public enum DayType {
	WORKING_DAY(Util.WORKING_DAY),
	HALF_WORKING_DAY(Util.WORKING_DAY / 2),
	WEEK_END(0),
	HOLIDAY(0),
	SICKNESS(0),
	CHILD_SICKNESS(0),
	VACATION(0);
	
	public final int minutesToWork;
	private DayType(int minutesToWork) {
		this.minutesToWork = minutesToWork;
	}
}