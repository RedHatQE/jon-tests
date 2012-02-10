package com.redhat.qe.jon.sahi.tests.plugins.eap6.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
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
	protected static final Logger log = Logger.getLogger(SSHClient.class.getName());
	private static final SimpleDateFormat sdfServerLog = new SimpleDateFormat("HH:mm:ss");
	public SSHClient(String user, String host, String key, String asHome) {
		log.fine("Creating instance that will connect to:" +user+"@"+host+" with private key file="+key+" asHome="+asHome);
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
	/**
	 * gets AS7/EAP home dir
	 * @return
	 */
	public String getAsHome() {
		return asHome;
	}
	/**
	 * restarts server by killing it and starting using given script
	 * @param script name of startup script located in {@link SSHClient#getAsHome()} / bin
	 */
	public void restart(String script) {
		stop();
		try {
			Thread.currentThread().join(10*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start(script);		
	}
	/**
	 * starts server
	 * @param script name of startup script located in {@link SSHClient#getAsHome()} / bin
	 */
	public void start(String script) {
		run("cd "+asHome+"/bin && nohup ./"+script+" &");
	}
	/**
	 * stops server by killing it
	 */
	public void stop() {
		run("kill -9 $(ps ax | grep "+asHome+" | grep java | awk '{print $1}')");
	}
	/**
	 * check whether EAP server is running
	 * @param script name of startup script located in {@link SSHClient#getAsHome()} / bin
	 * @return
	 */
	public boolean isRunning() {
		return runAndWait("ps ax | grep "+asHome+" | grep java | grep -v bash").getStdout().contains(asHome);
	}
	/**
	 * gets server startup time by parsing 1st line of it's log file
	 * @param logFile relative path located in {@link SSHClient#getAsHome()} to server's boot.log logFile
	 * @return
	 */
	public Date getStartupTime(String logFile) {
		String dateStr = runAndWait("head -n1 "+asHome+"/"+logFile+" | awk -F, '{print $1}' ").getStdout().trim();
		try {
			return sdfServerLog.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("Unable to determine server startup time", e);
		}
	}
	
	
}
