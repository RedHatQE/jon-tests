package com.redhat.qe.jon.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class AS7SSHClient extends SSHClient {

	private final String asHome;
	private static final SimpleDateFormat sdfServerLog = new SimpleDateFormat("HH:mm:ss");
	public AS7SSHClient(String asHome) {
		super();
		this.asHome = asHome;
	}
	public AS7SSHClient(String asHome, String user,String host, String pass) {
		super(user,host,pass);
		this.asHome = asHome;
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
	 * @param script name of startup script located in {@link AS7SSHClient#getAsHome()} / bin
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
	 * @param script name of startup script located in {@link AS7SSHClient#getAsHome()} / bin
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
	 * @return true if server process is running
	 */
	public boolean isRunning() {
		return runAndWait("ps ax | grep "+asHome+" | grep java | grep -v bash").getStdout().contains(asHome);
	}
	/**
	 * gets server startup time by parsing 1st line of it's log file
	 * @param logFile relative path located in {@link AS7SSHClient#getAsHome()} to server's boot.log logFile
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
