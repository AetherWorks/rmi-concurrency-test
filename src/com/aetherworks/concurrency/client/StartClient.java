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

/**
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class StartClient {
	private final static Logger LOGGER = Logger.getLogger(StartClient.class.getName());

	public static void main(final String[] args) throws RemoteException, NotBoundException, InterruptedException, ExecutionException {
		final Registry registry = LocateRegistry.getRegistry(1099);
		final ServerRemote server = getServer(registry);

		final int numThreads = 3;
		final boolean isSingleProcess = true;

		for (final CallType callType : CallType.values()) {
			final long avgTimeToExecute = CallExecutor.execute(numThreads, createNewCall(isSingleProcess, server, callType));
			LOGGER.log(Level.INFO, callType.name() + ": " + avgTimeToExecute + " ms.");
		}
	}

	private static CallFactory createNewCall(final boolean singleProcess, final ServerRemote server, final CallType callType) {
		if (singleProcess) {
			return new SingleProcessCallFactory(server, RemoteCalls.getCall(callType, server));
		} else {
			return new SeparateProcessCallFactory(callType);
		}
	}

	private static ServerRemote getServer(final Registry registry) throws RemoteException, NotBoundException, AccessException {
		try {
			return (ServerRemote) registry.lookup("server-remote");
		} catch (final ConnectException e) {
			LOGGER.log(Level.SEVERE, "Remote server not active.");
			System.exit(1);
		}
		return null;
	}
}
