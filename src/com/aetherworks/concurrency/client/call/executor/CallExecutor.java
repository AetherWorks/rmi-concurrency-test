/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.executor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aetherworks.concurrency.client.call.factory.CallFactory;
import com.aetherworks.concurrency.client.call.future.CallFuture;

/**
 * Executor for {@link CallFactory} instances. Used to create and wait for the termination of many {@link CallFuture}
 * objects returned by the {@link CallFactory}.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class CallExecutor {
	private final static Logger LOGGER = Logger.getLogger(CallExecutor.class.getName());

	/**
	 * Create <tt>numExecutions</tt> instances of the {@link CallFuture} created by
	 * {@link CallFactory#createAndSubmitTask()}, and returns the averaged result.
	 */
	public static long execute(final int numExecutions, final CallFactory callFactory) throws InterruptedException, ExecutionException {
		final List<CallFuture> threads = new LinkedList<>();

		LOGGER.log(Level.FINE, "Starting threads.");
		for (int i = 0; i < numExecutions; i++) {
			threads.add(callFactory.createAndSubmitTask());
		}

		waitUntilAllFuturesComplete(threads);

		return getAverageExecutionTime(threads);

	}

	/**
	 * Wait until every executing future is completed.
	 */
	public static void waitUntilAllFuturesComplete(final List<CallFuture> futures) {
		LOGGER.log(Level.FINE, "Waiting for completion of threads.");

		for (final CallFuture callExecutor : futures) {
			callExecutor.waitForCompletion();
		}
	}

	/**
	 * The average execution time of each executing future.
	 */
	private static long getAverageExecutionTime(final List<CallFuture> threads) throws InterruptedException, ExecutionException {
		long total = 0;

		for (final CallFuture future : threads) {
			total += future.getExecutionTime();
		}

		return total / threads.size();
	}

}
