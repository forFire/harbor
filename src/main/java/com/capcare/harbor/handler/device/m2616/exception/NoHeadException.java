package com.capcare.harbor.handler.device.m2616.exception;

/**
 * @author fyq
 */
public class NoHeadException extends RuntimeException {

	private static final long serialVersionUID = -1709573112253418317L;

	public NoHeadException() {
		super();
	}

	public NoHeadException(Throwable t) {
		super(t);
	}

	public NoHeadException(String message) {
		super(message);
	}

	public NoHeadException(String message, Throwable t) {
		super(message, t);
	}
}
