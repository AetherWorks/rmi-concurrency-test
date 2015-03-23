/*
 * Copyright 2015, AetherWorks LLC.
 */

package com.aetherworks.concurrency.client;

import java.rmi.RemoteException;

import com.aetherworks.concurrency.server.ServerRemote;
import com.google.common.base.Function;

/**
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class RemoteCalls {
	public enum CallType {
		BASIC, SYNC, SLEEP, SYNC_SLEEP
	}

	public static Function<ServerRemote, Integer> getCall(final CallType type, final ServerRemote server) {
		Function<ServerRemote, Integer> func;

		switch (type) {
			case BASIC:
				func = getCountCallsFunction(server);
				break;
			case SYNC:
				func = getCountCallsWithSynchFunction(server);
				break;
			case SLEEP:
				func = getCountCallsWithSleepFunction(server);
				break;
			case SYNC_SLEEP:
				func = getCountCallsWithSleepAndSynchFunction(server);
				break;
			default:
				func = null;
				break;
		}

		return func;
	}

	private static Function<ServerRemote, Integer> getCountCallsFunction(final ServerRemote server) {
		final Function<ServerRemote, Integer> countCalls = new Function<ServerRemote, Integer>() {
			@Override
			public Integer apply(final ServerRemote input) {
				try {
					return server.countCalls();
				} catch (final RemoteException e) {
					e.printStackTrace();
					return -1;
				}
			}
		};
		return countCalls;
	}

	private static Function<ServerRemote, Integer> getCountCallsWithSynchFunction(final ServerRemote server) {
		final Function<ServerRemote, Integer> countCalls = new Function<ServerRemote, Integer>() {
			@Override
			public Integer apply(final ServerRemote input) {
				try {
					return server.countCallsWithSynchronization();
				} catch (final RemoteException e) {
					e.printStackTrace();
					return -1;
				}
			}
		};
		return countCalls;
	}

	private static Function<ServerRemote, Integer> getCountCallsWithSleepFunction(final ServerRemote server) {
		final Function<ServerRemote, Integer> countCalls = new Function<ServerRemote, Integer>() {
			@Override
			public Integer apply(final ServerRemote input) {
				try {
					return server.countCallsWithSleep();
				} catch (final RemoteException e) {
					e.printStackTrace();
					return -1;
				}
			}
		};
		return countCalls;
	}

	private static Function<ServerRemote, Integer> getCountCallsWithSleepAndSynchFunction(final ServerRemote server) {
		final Function<ServerRemote, Integer> countCalls = new Function<ServerRemote, Integer>() {
			@Override
			public Integer apply(final ServerRemote input) {
				try {
					return server.countCallsWithSleepAndSynchronization();
				} catch (final RemoteException e) {
					e.printStackTrace();
					return -1;
				}
			}
		};
		return countCalls;
	}
}
