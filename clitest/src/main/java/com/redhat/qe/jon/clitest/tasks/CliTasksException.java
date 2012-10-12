package com.redhat.qe.jon.clitest.tasks;

public class CliTasksException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CliTasksException(String message) {
		super(message);
	}
	public CliTasksException(String message, Throwable tw) {
		super(message, tw);
	}
}
