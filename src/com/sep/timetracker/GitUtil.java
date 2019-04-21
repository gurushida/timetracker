package com.sep.timetracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.sep.timetracker.ExecUtil.Result;

public class GitUtil {

	public static boolean isGitPresent() {
		return null != ExecUtil.execute(null, "git");
	}

	public static void init(File directory) throws FileNotFoundException, UnsupportedEncodingException {
		ExecUtil.execute(directory, "git", "init");
		// The file containing the start of the current time track session is not meant to be
		// archived with git. Let's ignore it.
		File ignore = new File(directory, ".gitignore");
		try (PrintWriter p = new PrintWriter(ignore, "UTF-8")) {
			p.println(CurrentTrackingStart.FILENAME);
		}
		ExecUtil.execute(directory, "git", "add", ".");
		ExecUtil.execute(directory, "git", "commit", "-m", "Starting time tracking");
	}

	public static void checkUncommittedChanges(File directory) {
		Result r = ExecUtil.execute(directory, "git", "status", "--porcelain=1");
		if (!r.stdout.isEmpty()) {
			System.out.println("There are uncommitted changes in " + directory.getAbsolutePath() +" !");
			System.out.println("You need to review them and either revert or commit these modifications.");
			System.exit(0);
		}
	}

	public static void addAllAndCommit(File directory, String commitMsg) {
		ExecUtil.execute(directory, "git", "init");
		ExecUtil.execute(directory, "git", "add", ".");
		ExecUtil.execute(directory, "git", "commit", "-m", commitMsg);
	}

}
