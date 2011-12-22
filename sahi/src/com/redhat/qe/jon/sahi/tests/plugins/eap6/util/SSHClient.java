package com.redhat.qe.jon.sahi.tests.plugins.eap6.util;

import java.io.File;
import java.io.IOException;

import com.redhat.qe.tools.SSHCommandResult;
import com.redhat.qe.tools.SSHCommandRunner;
import com.trilead.ssh2.Connection;

public class SSHClient {
	protected SSHCommandRunner sshCommandRunner = null;
	protected Connection connection = null;
	private final String user;
	private final String host;
	private final String key;
	private final String asHome;
	public SSHClient(String user, String host, String key, String asHome) {
		this.user = user;
		this.host  = host;
		this.key = key;
		this.asHome = asHome;
	}
	/**
	 * connects to SSH server. This method is a good choice if you wish to check your connection settings
	 */
	public void connect(){
		disconnect();
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
	public String getAsHome() {
		return asHome;
	}
	/**
	 * restarts server by killing it and starting using given script
	 * @param script name of starting script located in {@link SSHClient#getAsHome()} / bin
	 */
	public void restart(String script) {
		run("kill -9 $(ps ax | grep standalone | grep java | awk '{print $1}')");
		run("sleep 3 && cd "+asHome+"/bin && nohup ./"+script+" &");
	}
	
	
}
