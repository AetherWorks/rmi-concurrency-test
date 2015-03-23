package com.aetherworks.concurrency.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartServer {
	private final static Logger LOGGER = Logger.getLogger(StartServer.class.getName());

	public static void main(final String[] args) throws RemoteException, InterruptedException, AlreadyBoundException {
		final ServerRemoteImpl server = new ServerRemoteImpl();

		final ServerRemote exportedServer = (ServerRemote) UnicastRemoteObject.exportObject(server, 0);

		final Registry registry = LocateRegistry.createRegistry(1099);
		registry.bind("server-remote", exportedServer);
		LOGGER.log(Level.INFO, "Server exported.");
	}
}
