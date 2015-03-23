/*
 * Copyright 2012, AetherWorks LLC.
 */

package com.aetherworks.concurrency.util;

/**
 * Thrown when the application attempts to perform an operation on a child process that requires it to be active, but
 * the process itself is not currently active.
 * <p>
 * It has either never started, or has died at some point.
 * 
 * @author Angus Macdonald (amacdonald@aetherworks.com)
 */
public class DeadProcessException extends Exception {

	private static final long serialVersionUID = 5118301517596161520L;

	public DeadProcessException(final String message) {
		super(message);
	}

}
