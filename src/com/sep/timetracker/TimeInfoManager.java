package com.sep.timetracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public abstract class TimeInfoManager {

	protected final File dataSource;
	private final Iterator<String> iterator;

	public TimeInfoManager(File dataSource) throws FileNotFoundException, ParseException {
		this.dataSource = dataSource;
		Scanner sc = new Scanner(dataSource, "UTF-8");
		sc.useDelimiter("\\R");
		this.iterator = sc;
	}

	public TimeInfoManager(List<String> lines) throws ParseException {
		this.dataSource = null;
		this.iterator = lines.iterator();
	}

	public TimeInfoManager(String[] lines) throws ParseException {
		this.dataSource = null;
		this.iterator = Util.toIterator(lines);
	}

	protected void loadData() throws ParseException {
		while (iterator.hasNext()) {
			String line = iterator.next().trim();
			int pos = line.indexOf('#');
			if (pos != -1) {
				line = line.substring(0, pos).trim();
			}

			if (line.isEmpty()) {
				continue;
			}
			processLine(line);
		}
		if (iterator instanceof Scanner) {
			((Scanner) iterator).close();
		}
	}

	protected abstract void processLine(String line) throws ParseException;

	protected void addLine(String line) throws IOException {
		if (dataSource == null)
			return;

		try (PrintStream out = new PrintStream(new FileOutputStream(dataSource, true), true, "UTF-8")) {
		    out.println(line);
		}
	}
}
