/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.factory;

import java.util.concurrent.Future;

import com.aetherworks.concurrency.client.call.future.CallFuture;

/**
 * Factory for the creation of tasks that's output can later be queried through a related {@link CallFuture} instance.
 * <p>
 * This factory creates the task and starts its execution, and the provided {@link CallFuture} is used to query it's
 * result (similar to the java {@link Future} class).
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public interface CallFactory {

	/**
	 * Create a {@link CallFuture}, start it's execution
	 */
	CallFuture createAndSubmitTask();

}