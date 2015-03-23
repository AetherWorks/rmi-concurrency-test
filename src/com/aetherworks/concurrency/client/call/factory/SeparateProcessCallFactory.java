/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client.call.factory;

import java.io.IOException;

import com.aetherworks.concurrency.client.TimedFunctionExecutor;
import com.aetherworks.concurrency.client.RemoteCalls.CallType;
import com.aetherworks.concurrency.client.call.future.CallFuture;
import com.aetherworks.concurrency.client.call.future.SeparateProcessCallFuture;
import com.aetherworks.concurrency.util.CommandLineArgs;
import com.aetherworks.concurrency.util.JavaProcess;

/**
 * Creates a process that executes the specified call in a new java process.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class SeparateProcessCallFactory implements CallFactory {

	private static int creationNumber = 0;

	private final CallType callType;

	public SeparateProcessCallFactory(final CallType callType) {
		this.callType = callType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CallFuture createAndSubmitTask() {
		final CommandLineArgs args = new CommandLineArgs();
		args.put('p', 1099);
		args.put('n', "server-remote");
		args.put('t', callType.name());
		final JavaProcess process = new JavaProcess(TimedFunctionExecutor.class, args.getArgsAsList());

		try {
			process.executeProcess(creationNumber++ + ": ");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new SeparateProcessCallFuture(process);
	}

}
