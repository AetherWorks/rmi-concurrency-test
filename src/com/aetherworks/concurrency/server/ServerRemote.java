/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public interface ServerRemote extends Remote {
	int countCalls() throws RemoteException;

	int countCallsWithSynchronization() throws RemoteException;

	int countCallsWithSleep() throws RemoteException;

	int countCallsWithSleepAndSynchronization() throws RemoteException;
}
