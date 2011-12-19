package com.redhat.qe.jon.sahi.tests.plugins.eap6.util;

import java.io.File;
import java.io.IOException;

import com.redhat.qe.tools.SSHCommandRunner;
import com.trilead.ssh2.Connection;

public class SSHClient {
	protected SSHCommandRunner sshCommandRunner = null;
	protected Connection connection = null;
	public SSHClient() {
	}
	/**
	 * connects to SSH server. This method is a good choice if you wish to check your connection settings
	 */
	public void connect(){
		disconnect();
        String user = System.getProperty("as7.runs.as.user");
        String host = System.getProperty("as7.standalone.hostname");
        String key = System.getProperty("user.home")+"/"+System.getProperty("as7.key");
        connection = new Connection(host, 22);
		try {
			connection.connect();
			connection.authenticateWithPublicKey(user, new File(key), null);
			sshCommandRunner = new SSHCommandRunner(connection, null);
		} catch (IOException e) {
			connection = null;
			throw new RuntimeException(
					"Cannot connect to SSH via pub-key without password as" + user+"@"+host, e);
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
	public void runAndWait(String command) {
		if (!isConnected()) {
			connect();
		}
		sshCommandRunner.runCommandAndWait(command);
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
