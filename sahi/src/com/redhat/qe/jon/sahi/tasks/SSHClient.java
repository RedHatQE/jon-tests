package com.redhat.qe.jon.sahi.tasks;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.redhat.qe.tools.SSHCommandResult;
import com.redhat.qe.tools.SSHCommandRunner;
import com.trilead.ssh2.Connection;

public class SSHClient {
	protected SSHCommandRunner sshCommandRunner = null;
	protected Connection connection = null;
	private final String user;
	private final String host;
	private final String pass;
	protected static final Logger log = Logger.getLogger(SSHClient.class.getName());

	protected SSHClient(String user, String host, String pass) {
		log.fine("Creating SSHClient that will connect to [" +user+"@"+host+"]");
		this.user = user;
		this.host  = host;
		this.pass = pass;
	}
	public SSHClient() {
		this(System.getenv().get("HOST_USER"), System.getenv().get("HOST_NAME"), System.getenv().get("HOST_PASSWORD"));
	}
	/**
	 * connects to SSH server. This method is a good choice if you wish to check your connection settings
	 */
	public void connect(){
		disconnect();
        connection = new Connection(host, 22);
		try {
			connection.connect();
			connection.authenticateWithPassword(user, pass);
			sshCommandRunner = new SSHCommandRunner(connection, null);
		} catch (IOException e) {
			connection = null;
			throw new RuntimeException(
					"Cannot connect to SSH as " + user+"@"+host, e);
		}
	}
	public boolean isConnected() {
		return connection!=null;
	}

	/**
	 * runs given command and waits for return value on SSH server, if client is not connected it will connect automatically, 
	 * don't forget to disconnect ;)
	 * @param command
	 */
	public SSHCommandResult runAndWait(String command) {
		if (!isConnected()) {
			connect();
		}
		return sshCommandRunner.runCommandAndWait(command);
	}
	/**
	 * runs given command and waits for return value on SSH server, if client is not connected it will connect automatically, 
	 * don't forget to disconnect ;)
	 * @param command
	 */
	public SSHCommandResult runAndWait(String command, Long timeoutMilis) {
		if (!isConnected()) {
			connect();
		}
		return sshCommandRunner.runCommandAndWait(command,timeoutMilis);

	}
	/**
	 * runs given command on SSH server, if client is not connected it will connect automatically, 
	 * don't forget to disconnect ;)
	 * @param command
	 */
	public void run(String command) {
		if (!isConnected()) {
			connect();
		}
		sshCommandRunner.runCommand(command);
	}
	/**
	 * disconnects from SSH server
	 */
	public void disconnect() {
		if (isConnected()) {
			connection.close();
			connection = null;
		}
	}
	
}
