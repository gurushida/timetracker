package com.sep.timetracker;

import java.util.Objects;

public class Pair<U,V> {

	public final U first;
	public final V second;

	public Pair(U first, V second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) {
			return false;
		}
		Pair<?,?> other = (Pair<?, ?>)obj;
		return Objects.equals(first, other.first) && Objects.equals(second, other.second);
	}
}
