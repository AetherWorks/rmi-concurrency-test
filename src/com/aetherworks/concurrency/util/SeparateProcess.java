/*
 * Copyright 2014, AetherWorks LLC.
 */

package com.aetherworks.concurrency.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

/**
 * Class for starting a separate process and maintaining it during its lifetime.
 * 
 * @author Michael Zaccardo (mzaccardo@aetherworks.com)
 */
public abstract class SeparateProcess {

	private final static Logger LOGGER = Logger.getLogger(SeparateProcess.class.getName());

	/**
	 * The separate process being wrapped.
	 */
	protected Process process;

	/**
	 * Thread managing the redirection of standard output for this process to a particular print stream.
	 */
	private Thread outWriter;

	/**
	 * Thread managing the redirection of standard error for this process to a particular print stream.
	 */
	private Thread errWriter;

	/**
	 * Start the execution of the separate process.
	 * 
	 * @param consolePrefix
	 *        The prefix for all redirected console output.
	 * 
	 * @throws IOException
	 *         If the process fails to start.
	 */
	public abstract void executeProcess(String consolePrefix) throws IOException;

	/**
	 * Waits for the running process to complete.
	 * 
	 * @return The exit value of the process.
	 * 
	 * @throws DeadProcessException
	 *         If the process has not yet been created.
	 * @throws InterruptedException
	 *         If the current thread is interrupted while waiting for the process to terminate.
	 */
	public int waitForProcessToComplete() throws DeadProcessException, InterruptedException {
		if (process == null) {
			throw new DeadProcessException("The process has not yet been created so no exit value can be retrieved.");
		}

		try {
			return process.waitFor();
		} finally {
			stopThreads();
		}
	}

	/**
	 * Kills the process if it is running.
	 * 
	 * @throws DeadProcessException
	 *         If the process has not yet been created, or is running and has not yet been terminated.
	 */
	public void killProcess() throws DeadProcessException {
		if (!isAlive()) {
			throw new DeadProcessException("The process is not alive so it cannot be killed.");
		}

		process.destroy();

		stopThreads();
	}

	/**
	 * Determines if the process is running.
	 * 
	 * @return <code>true</code> if the process is running and <code>false</code> otherwise.
	 */
	public boolean isAlive() {
		if (process == null) {
			return false;
		}

		try {
			process.exitValue();

			// Process is already dead.
			return false;
		} catch (final IllegalThreadStateException e) {
			LOGGER.log(Level.FINER, "Expected exception. Checked for exit value, but process is active.", e);

			// Process is alive.
			return true;
		}
	}

	/**
	 * The exit value of the process. 0 indicates successful termination.
	 * 
	 * @throws IllegalThreadStateException
	 *         The process has not yet been created, or is running and has not yet been terminated.
	 */
	public int exitValue() throws IllegalThreadStateException {
		if (process == null) {
			throw new IllegalThreadStateException("The process has not yet been created so no exit value can be retrieved.");
		}

		return process.exitValue();
	}

	private void stopThreads() {
		if (outWriter != null) {
			outWriter.interrupt();
			outWriter = null;
		}

		if (errWriter != null) {
			errWriter.interrupt();
			errWriter = null;
		}
	}

	protected void redirectConsoleOutput(@Nonnull final String consolePrefix, @Nonnull final String threadName) {
		outWriter = ConsoleWriterFactory.createConsoleWriter(consolePrefix, threadName + "-OutConsoleWriter", process.getInputStream(),
				System.out);
		errWriter = ConsoleWriterFactory.createConsoleWriter(consolePrefix, threadName + "-ErrConsoleWriter", process.getErrorStream(),
				System.err);
	}
}
