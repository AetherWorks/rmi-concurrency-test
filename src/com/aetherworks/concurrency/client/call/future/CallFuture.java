/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Similar to the {@link Future} class, this allows the program to wait for the execution of an asynchronously executing
 * task, and get its result.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public interface CallFuture {

	/**
	 * Block until the call has finished.
	 */
	void waitForCompletion();

	/**
	 * The time it took to execute the given call in milliseconds.
	 */
	long getExecutionTime() throws InterruptedException, ExecutionException;

}