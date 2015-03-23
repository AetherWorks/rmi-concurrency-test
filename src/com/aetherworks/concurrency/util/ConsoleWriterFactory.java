/*
 * Copyright 2014, AetherWorks LLC.
 */

package com.aetherworks.concurrency.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates instances of {@link Thread}s which write the contents of an {@link InputStream} to a {@link PrintStream}.
 * 
 * @author Michael Zaccardo (mzaccardo@aetherworks.com)
 */
public class ConsoleWriterFactory {

	private final static Logger LOGGER = Logger.getLogger(ConsoleWriterFactory.class.getName());

	/**
	 * Creates a new thread which writes the contents of an {@link InputStream} to a {@link PrintStream}. The
	 * {@link InputStream} is typically from an other java process through the {@link Process#getInputStream()} method,
	 * and the {@link PrintStream} is typically either {@link System#out} or {@link System#err}.
	 * 
	 * @param prefix
	 *        Text to be prepended to all output from the given stream. Leave null if nothing is needed.
	 * @param threadName
	 *        Name of the thread being created.
	 * @param streamToWrite
	 *        The stream that is being redirected. Typically this is {@link Process#getInputStream()}.
	 * @param streamToWriteTo
	 *        Print stream that text should be written to. Often this is {@link System#out} or {@link System#err}.
	 * @return The thread created as a result of this call. This is returned so that the application can keep track of
	 *         it and terminate it when necessary.
	 */
	public static Thread createConsoleWriter(final String prefix, final String threadName, final InputStream streamToWrite,
			final PrintStream streamToWriteTo) {

		final Thread processConsoleWriter = new Thread() {

			@Override
			public void run() {
				String line;

				try (final BufferedReader input = new BufferedReader(new InputStreamReader(streamToWrite))) {
					while ((line = input.readLine()) != null && !super.isInterrupted()) {
						streamToWriteTo.println(prefix + line);
					}
				} catch (final IOException e) {
					LOGGER.log(Level.WARNING, "Error reading from process stream: '" + threadName + "'.", e);
				}
			}
		};

		processConsoleWriter.setName(threadName);
		processConsoleWriter.start();

		return processConsoleWriter;
	}
}
