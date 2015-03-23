/*
 * Copyright 2012, AetherWorks LLC.
 */

package com.aetherworks.concurrency.util;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

/**
 * Utility class for handling command line arguments.
 * <p>
 * Arguments are expected to be of the format '-aValue', where 'a' is a flag and the value immediately follows it.
 * <p>
 * Use {@link #put(char, Object)} to add arguments, {@link #getArgsAsList()} to create the args in a format that can be
 * given to {@link JavaProcess}, and {@link #parseArgs(List)} or {@link #parseArgs(String[])} to parse incoming
 * arguments in the main method of a starting application.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class CommandLineArgs {

	private final Map<String, Object> args = new HashMap<>();

	/**
	 * Add a new value to the list of command line args.
	 * <p>
	 * Values are stored as objects so that types such as integers can be accepted by this method, but they are returned
	 * by the {@link #get(char)} method as a string, so you must implement equals if you want anything sensible out of
	 * your own objects.
	 * 
	 */
	public void put(final char flag, final Object value) {
		Preconditions.checkNotNull(value);

		args.put(Character.toString(flag), value);

	}

	public List<String> getArgsAsList() {
		final List<String> listOfArgs = new LinkedList<>();

		for (final Entry<String, Object> arg : args.entrySet()) {
			listOfArgs.add("-" + arg.getKey() + arg.getValue());
		}

		return listOfArgs;
	}

	public static CommandLineArgs parseArgs(final List<String> args) {

		if (args == null || args.size() == 0) {
			return new CommandLineArgs();
		}

		return parseArgs(args.toArray(new String[0]));
	}

	public static CommandLineArgs parseArgs(final String[] arrayOfArgs) {

		if (arrayOfArgs == null || arrayOfArgs.length == 0) {
			return new CommandLineArgs();
		}

		final CommandLineArgs parsedArgs = new CommandLineArgs();

		for (final String arg : arrayOfArgs) {
			final char flag = arg.charAt(1); // the first character is '-'
			final String value = arg.substring(2);

			parsedArgs.put(flag, value);
		}

		return parsedArgs;
	}

	/**
	 * Get the value associated with the given command line flag.
	 * 
	 * Null will be returned if this flag is not contained in the map of args.
	 */
	public String get(final char flag) {
		final Object arg = args.get(Character.toString(flag));

		if (arg == null) {
			return null;
		} else {
			return arg.toString();
		}
	}

	/**
	 * Same as {@link #get(char)} but an exception is thrown if the argument is not found.
	 */
	public String getNotNull(final char flag) {
		final Object value = args.get(Character.toString(flag));
		Preconditions.checkNotNull(value, "The argument '" + flag + "' was needed by this program, but was not provided.");

		return value.toString();
	}

	/**
	 * Parse a file path to remove any extraneous surrounding characters such as quotes or whitespace.
	 * 
	 * @param path
	 *        A file path to be trimmed. It will be unaltered if it doesn't start or end with quotes or whitespace.
	 * @return A path that can be interpreted by {@link File}.
	 */
	public static String parsePath(String path) {
		if (path == null) {
			return null;
		}

		path = path.trim();

		if (path.charAt(0) == '\"') {
			path = path.substring(1, path.length());
		}
		if (path.endsWith("\"")) {
			path = path.substring(0, path.length() - 1);
		}

		path = path.trim();

		return path;
	}
}
