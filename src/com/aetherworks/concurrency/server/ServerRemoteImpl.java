package com.aetherworks.concurrency.server;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRemoteImpl implements ServerRemote {
	private final static Logger LOGGER = Logger.getLogger(ServerRemoteImpl.class.getName());

	int countCalls = 0;
	int countCallsWithSynch = 0;
	int countCallsWithSleep = 0;
	int countCallsWithSleepAndSynch = 0;

	Object synch = new Object();

	@Override
	public int countCalls() throws RemoteException {
		countCalls++;
		LOGGER.log(Level.INFO, "Call number: " + countCalls);

		return countCalls;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countCallsWithSynchronization() throws RemoteException {
		synchronized (synch) {

			countCallsWithSynch++;
			LOGGER.log(Level.INFO, "Call number (synch): " + countCallsWithSynch);

			return countCallsWithSynch;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countCallsWithSleep() throws RemoteException {
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		countCallsWithSleep++;
		LOGGER.log(Level.INFO, "Call number (sleep): " + countCallsWithSleep);

		return countCallsWithSleep;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countCallsWithSleepAndSynchronization() throws RemoteException {
		synchronized (synch) {
			try {

				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			countCallsWithSleep++;
			LOGGER.log(Level.INFO, "Call number (sleep): " + countCallsWithSleep);

			return countCallsWithSleep;
		}
	}
}
