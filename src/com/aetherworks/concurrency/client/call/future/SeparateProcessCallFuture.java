/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.future;

import java.util.concurrent.ExecutionException;

import com.aetherworks.concurrency.util.DeadProcessException;
import com.aetherworks.concurrency.util.JavaProcess;

/**
 * Wrapper around a {@link JavaProcess}, which waits for the completion of the process and returns its result through
 * the exit value of the process.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class SeparateProcessCallFuture implements CallFuture {

	private final JavaProcess process;

	public SeparateProcessCallFuture(final JavaProcess process) {
		this.process = process;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitForCompletion() {
		try {
			process.waitForProcessToComplete();
		} catch (DeadProcessException | InterruptedException e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getExecutionTime() throws InterruptedException, ExecutionException {
		return process.exitValue();
	}

}
