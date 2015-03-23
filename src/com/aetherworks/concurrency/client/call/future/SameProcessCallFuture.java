/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Wrapper around a {@link Future} class to provide compatibility with multi-process calls.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class SameProcessCallFuture implements CallFuture {

	private final Future<Long> future;

	public SameProcessCallFuture(final Future<Long> future) {
		this.future = future;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitForCompletion() {
		while (!future.isDone()) {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getExecutionTime() throws InterruptedException, ExecutionException {
		return future.get();
	}

}
