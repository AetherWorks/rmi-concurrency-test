// $codepro.audit.disable commandExecution
/*
 * Copyright 2012, AetherWorks LLC.
 */

package com.aetherworks.concurrency.util;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for starting a Java class in a separate process and maintaining it during its lifetime.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class JavaProcess extends SeparateProcess {

	private final static Logger LOGGER = Logger.getLogger(JavaProcess.class.getName());

	private static final String DEFAULT_JAVA_COMMAND_PATH = "java ";

	private static final String USE_SPLIT_VERIFIER_ARG = "-XX:-UseSplitVerifier ";

	private final String javaLibraryPath;
	private final String classpath;

	private final Class<?> classToRun;
	private final List<String> classArgs;

	private final String vmArgs;

	private final boolean useSplitVerifier;

	/**
	 * Run a given Java class with the provided arguments. Use the classpath and java.library.path of the current
	 * process. By default, the <code>-XX:-UseSplitVerifier</code> VM argument will not be used.
	 * <p>
	 * Use the other constructor to specify the classpath or java.library.path.
	 * 
	 * @param classToRun
	 *        Java class to be run in a separate process.
	 * @param classArgs
	 *        Arguments to be provided to the Java classes main method.
	 */
	public JavaProcess(final Class<?> classToRun, final List<String> classArgs) {
		this(classToRun, classArgs, System.getProperty("java.class.path"), System.getProperty("java.library.path"), null, false);
	}

	/**
	 * Run a given Java class with the provided arguments. Use the classpath and java.library.path of the current
	 * process. By default, the <code>-XX:-UseSplitVerifier</code> VM argument will not be used.
	 * <p>
	 * Use the other constructor to specify the classpath or java.library.path.
	 * 
	 * @param classToRun
	 *        Java class to be run in a separate process.
	 * @param classArgs
	 *        Arguments to be provided to the Java classes main method.
	 * @param vmArgs
	 *        Arguments to be provided to the VM.
	 */
	public JavaProcess(final Class<?> classToRun, final List<String> classArgs, final String vmArgs) {
		this(classToRun, classArgs, System.getProperty("java.class.path"), System.getProperty("java.library.path"), vmArgs, false);
	}

	/**
	 * Run a given Java class with the provided arguments. Use the classpath and java.library.path of the current
	 * process.
	 * <p>
	 * Use the other constructor to specify the classpath or java.library.path.
	 * 
	 * @param classToRun
	 *        Java class to be run in a separate process.
	 * @param classArgs
	 *        Arguments to be provided to the Java classes main method.
	 * @param useSplitVerifier
	 *        Whether to use the <code>-XX:-UseSplitVerifier</code> VM argument.
	 */
	public JavaProcess(final Class<?> classToRun, final List<String> classArgs, final boolean useSplitVerifier) {
		this(classToRun, classArgs, System.getProperty("java.class.path"), System.getProperty("java.library.path"), null, useSplitVerifier);
	}

	/**
	 * Run a given Java class with the provided arguments.
	 * 
	 * @param classToRun
	 *        Java class to be run in a separate process.
	 * @param classArgs
	 *        Arguments to be provided to the Java classes main method.
	 * @param classpath
	 *        Classpath for this process.
	 * @param javaLibraryPath
	 *        java.library.path for this process.
	 * @param useSplitVerifier
	 *        Whether to use the <code>-XX:-UseSplitVerifier</code> VM argument.
	 */
	public JavaProcess(final Class<?> classToRun, final List<String> classArgs, final String classpath, final String javaLibraryPath,
			final String vmArgs, final boolean useSplitVerifier) {

		this.classToRun = classToRun;
		this.classArgs = classArgs;
		this.javaLibraryPath = javaLibraryPath;
		this.classpath = classpath;
		this.vmArgs = vmArgs;
		this.useSplitVerifier = useSplitVerifier;
	}

	@Override
	public void executeProcess(final String consolePrefix) throws IOException {
		final String commandToRun = createJavaCommand();

		LOGGER.log(Level.INFO, "Executing command: '" + commandToRun + "'.");

		try {
			process = Runtime.getRuntime().exec(commandToRun);
		} catch (final IOException e) {
			LOGGER.log(Level.WARNING, "Exception thrown while attempting to start process for class: '" + classToRun.getName() + "'.", e);
			throw e;
		}

		redirectConsoleOutput(consolePrefix, classToRun.getSimpleName());
	}

	/**
	 * Use all of the provided information to create the java command necessary to start the given class as a separate
	 * process.
	 */
	private String createJavaCommand() {

		final StringBuilder command = new StringBuilder();

		command.append(getJavaCommandPath());

		if (useSplitVerifier) {
			command.append(USE_SPLIT_VERIFIER_ARG);
		}

		if (vmArgs != null) {
			command.append(vmArgs + " ");
		}

		command.append(getJavaLibraryPath());
		command.append(getClasspath());
		command.append(classToRun.getName() + " ");
		command.append(getClassArgs());

		return command.toString();
	}

	/**
	 * Returns the class args as a single string formatted for the 'java' command.
	 */
	private StringBuilder getClassArgs() {
		if (classArgs == null || classArgs.size() == 0) {
			return new StringBuilder("");
		}

		final StringBuilder sb = new StringBuilder();

		for (final String arg : classArgs) {
			sb.append(arg + " ");
		}
		return sb;
	}

	/**
	 * Returns the classpath as a single string (beginning -cp) formatted for the 'java' command.
	 */
	private String getClasspath() {
		return "-cp \"" + classpath + "\" ";
	}

	/**
	 * Returns the java.library.path as a single string (beginning -Djava.library.path=) formatted for the 'java'
	 * command.
	 */
	private String getJavaLibraryPath() {
		if (javaLibraryPath == null) {
			return "";
		} else {
			final String sanitizedPath = javaLibraryPath.replaceAll("\"", "");
			return "-Djava.library.path=\"" + sanitizedPath + "\" ";
		}
	}

	/**
	 * Returns the java command path. This may be just 'java', but in future we might have to specify a full path in
	 * some systems if 'java' is not set.
	 */
	private static String getJavaCommandPath() {
		return DEFAULT_JAVA_COMMAND_PATH;
	}
}
