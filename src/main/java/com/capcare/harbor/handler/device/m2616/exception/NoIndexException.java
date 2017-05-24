package com.capcare.harbor.handler.device.m2616.exception;

/**
 * @author fyq
 */
public class NoIndexException extends RuntimeException {

	private static final long serialVersionUID = -1709573112253418317L;

	public NoIndexException() {
		super();
	}

	public NoIndexException(Throwable t) {
		super(t);
	}

	public NoIndexException(String message) {
		super(message);
	}

	public NoIndexException(String message, Throwable t) {
		super(message, t);
	}
}
