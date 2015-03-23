/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client;

import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aetherworks.concurrency.client.RemoteCalls.CallType;
import com.aetherworks.concurrency.client.call.executor.CallExecutor;
import com.aetherworks.concurrency.client.call.factory.CallFactory;
import com.aetherworks.concurrency.client.call.factory.SeparateProcessCallFactory;
import com.aetherworks.concurrency.client.call.factory.SingleProcessCallFactory;
import com.aetherworks.concurrency.server.ServerRemote;
import com.aetherworks.concurrency.util.CommandLineArgs;

/**
 * Start class for the client.
 * <p>
 * Modify the provided class variables to set up the program, and run the client by starting through the main method.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class StartClient {

	private final static Logger LOGGER = Logger.getLogger(StartClient.class.getName());

	private static final String REMOTE_SERVICE_BINDING = "server-remote";

	private static final String SERVER_HOSTNAME = "localhost";
	private static final int SERVER_PORT = 1099;

	private static final int NUM_THREADS = 2;

	private static final boolean IS_SINGLE_PROCESS = false;

	public static void main(final String[] args) throws RemoteException, NotBoundException, InterruptedException, ExecutionException {

		for (final CallType callType : CallType.values()) {
			final long avgTimeToExecute = CallExecutor.execute(NUM_THREADS, createNewCall(IS_SINGLE_PROCESS, callType));
			LOGGER.log(Level.INFO, callType.name() + ": " + avgTimeToExecute + " ms.");
		}

	}

	private static CallFactory createNewCall(final boolean singleProcess, final CallType callType) throws RemoteException,
			NotBoundException {
		if (singleProcess) {
			final Registry registry = LocateRegistry.getRegistry(SERVER_HOSTNAME, SERVER_PORT);
			final ServerRemote server = getServer(registry);

			return new SingleProcessCallFactory(server, RemoteCalls.getCall(callType, server));
		} else {
			final CommandLineArgs args = new CommandLineArgs();
			args.put('p', SERVER_PORT);
			args.put('h', SERVER_HOSTNAME);
			args.put('n', REMOTE_SERVICE_BINDING);
			args.put('t', callType.name());
			return new SeparateProcessCallFactory(args.getArgsAsList());
		}
	}

	private static ServerRemote getServer(final Registry registry) throws RemoteException, NotBoundException, AccessException {
		try {
			return (ServerRemote) registry.lookup(REMOTE_SERVICE_BINDING);
		} catch (final ConnectException e) {
			LOGGER.log(Level.SEVERE, "Remote server not active.");
			System.exit(1);
		}
		return null;
	}
}
