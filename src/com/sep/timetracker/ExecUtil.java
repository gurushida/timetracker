package com.sep.timetracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;

public class ExecUtil {

	public static class Result {

		public final int exitCode;
		public final String stdout;
		public final String stderr;

		public Result(int exitCode, String stdout, String stderr) {
			this.exitCode = exitCode;
			this.stdout = stdout;
			this.stderr = stderr;
		}
	}

	/**
	 * Executes the given command in the given directory
	 * using the same stdin, stdout and stderr as the parent process.
	 */
	public static void execInheritParentIO(File workingDirectory, String... args) {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(workingDirectory);
		pb.redirectInput(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		try {
			pb.start().waitFor();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	public static Result execute(File workingDirectory, String... args) {
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(workingDirectory);
			Process p = pb.start();
			ByteArrayOutputStream stdout = new ByteArrayOutputStream();
			ByteArrayOutputStream stderr = new ByteArrayOutputStream();
			Thread t1 = consume(p.getInputStream(), stdout);
			Thread t2 = consume(p.getErrorStream(), stderr);
			int exitCode = p.waitFor();
			t1.join(1000);
			t2.join(1000);
			return new Result(exitCode, stdout.toString("UTF-8"), stderr.toString("UTF-8"));
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	private static Thread consume(InputStream input, ByteArrayOutputStream output) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[1024];
				int n;
				try {
					while ((n = input.read(buffer)) != -1) {
						output.write(buffer, 0, n);
						output.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		});
		t.start();
		return t;
	}
}
