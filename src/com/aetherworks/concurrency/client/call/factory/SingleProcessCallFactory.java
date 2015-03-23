/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.factory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.aetherworks.concurrency.client.TimedFunctionExecutor;
import com.aetherworks.concurrency.client.call.future.SameProcessCallFuture;
import com.aetherworks.concurrency.server.ServerRemote;
import com.google.common.base.Function;

/**
 * Creates a task that executes the provided call in a new thread within the current process.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class SingleProcessCallFactory implements CallFactory {

	private final ServerRemote server;
	private final Function<ServerRemote, Integer> call;

	public SingleProcessCallFactory(final ServerRemote server, final Function<ServerRemote, Integer> call) {
		this.server = server;
		this.call = call;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SameProcessCallFuture createAndSubmitTask() {
		final TimedFunctionExecutor clientRunner = new TimedFunctionExecutor(server, call);
		final Future<Long> future = Executors.newSingleThreadExecutor().submit(clientRunner);
		return new SameProcessCallFuture(future);
	}

}
