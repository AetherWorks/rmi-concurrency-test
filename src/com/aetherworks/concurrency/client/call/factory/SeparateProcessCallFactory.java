/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.factory;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aetherworks.concurrency.client.TimedFunctionExecutor;
import com.aetherworks.concurrency.client.call.future.CallFuture;
import com.aetherworks.concurrency.client.call.future.SeparateProcessCallFuture;
import com.aetherworks.concurrency.util.JavaProcess;

/**
 * Creates a process that executes the specified call in a new java process.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class SeparateProcessCallFactory implements CallFactory {
	private final static Logger LOGGER = Logger.getLogger(SeparateProcessCallFactory.class.getName());

	private static int creationNumber = 0;

	private final List<String> argsToProcess;

	public SeparateProcessCallFactory(final List<String> argsToProcess) {
		this.argsToProcess = argsToProcess;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CallFuture createAndSubmitTask() {

		final JavaProcess process = new JavaProcess(TimedFunctionExecutor.class, argsToProcess);

		try {
			process.executeProcess(creationNumber++ + ": ");
		} catch (final IOException e) {
			LOGGER.log(Level.WARNING, "Failed to execute process.", e);
		}

		return new SeparateProcessCallFuture(process);
	}

}
