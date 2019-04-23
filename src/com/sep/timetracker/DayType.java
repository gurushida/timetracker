package com.sep.timetracker;

public enum DayType {
	WORKING_DAY(1),
	HALF_WORKING_DAY(0.5),
	WEEK_END(0),
	HOLIDAY(0),
	SICKNESS(0),
	CHILD_SICKNESS(0),
	VACATION(0);

	// 0   = no work
	// 0.5 = working half the day
	// 1   = working all day
	public final double workRatio;

	private DayType(double workRatio) {
		this.workRatio = workRatio;
	}
}