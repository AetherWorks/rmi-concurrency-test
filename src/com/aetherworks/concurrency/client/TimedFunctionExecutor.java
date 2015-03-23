/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aetherworks.concurrency.client.RemoteCalls.CallType;
import com.aetherworks.concurrency.server.ServerRemote;
import com.aetherworks.concurrency.util.CommandLineArgs;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.primitives.Ints;

/**
 * Executes the provided function and times how long execution took to complete.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class TimedFunctionExecutor implements Callable<Long> {
	private final static Logger LOGGER = Logger.getLogger(TimedFunctionExecutor.class.getName());

	private final ServerRemote server;

	private final Function<ServerRemote, Integer> call;

	public TimedFunctionExecutor(final ServerRemote server, final Function<ServerRemote, Integer> call) {
		this.server = server;
		this.call = call;
	}

	/**
	 * Returns the time in MS it took to complete execution.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public Long call() throws Exception {
		final Stopwatch timer = Stopwatch.createStarted();

		final int callNumber = call.apply(server);

		timer.stop();

		final long timeElapsed = timer.elapsed(TimeUnit.MILLISECONDS);

		LOGGER.log(Level.FINE, "Call number: " + callNumber + " (" + timeElapsed + " ms)");

		return timeElapsed;
	}

	public static void main(final String[] args) throws Exception {
		final CommandLineArgs parsedArgs = CommandLineArgs.parseArgs(args);

		final int registryPort = Integer.parseInt(parsedArgs.getNotNull('p'));
		final String serviceName = parsedArgs.getNotNull('n');
		final CallType callType = CallType.valueOf(parsedArgs.getNotNull('t'));

		final Registry registry = LocateRegistry.getRegistry(registryPort);
		final ServerRemote server = (ServerRemote) registry.lookup(serviceName);

		final TimedFunctionExecutor runner = new TimedFunctionExecutor(server, RemoteCalls.getCall(callType, server));

		final Long result = runner.call();

		LOGGER.log(Level.INFO, callType.name() + " executed in " + result + " ms.");

		System.exit(Ints.checkedCast(result));
	}
}
