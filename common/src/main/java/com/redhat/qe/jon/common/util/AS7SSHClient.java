package com.redhat.qe.jon.common.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class AS7SSHClient extends SSHClient {

    private String serverConfig; // allows to recognize the correct AS7 server process
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
	public AS7SSHClient(String asHome, String user,String host, File keyFile, String pass) {
	    super(user,host,keyFile,pass);
	    this.asHome = asHome;
	}
    public AS7SSHClient(String asHome, String serverConfig, String user,String host, File keyFile, String pass) {
        super(user,host,keyFile,pass);
        this.asHome = asHome;
        this.serverConfig = serverConfig;
    }
	/**
	 * gets AS7/EAP home dir
	 * @return AS7/EAP home dir
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
		String pids = null;
        if (serverConfig != null) {
            pids = runAndWait("ps ax | grep "+asHome+" | grep "+  serverConfig +" | grep java | grep -v bash | awk '{print $1}'").getStdout();
        } else {
            pids = runAndWait("ps ax | grep "+asHome+" | grep java | grep -v bash | awk '{print $1}'").getStdout();
        }
		if (pids!=null && pids.length()>0) {
			for (String pid : pids.split("\n")) {
				runAndWait("kill -9 "+pid);
			}
		}
	}
	/**
	 * check whether EAP server is running
	 * @return true if server process is running
	 */
	public boolean isRunning() {
        if (serverConfig != null) {
            return runAndWait("ps ax | grep "+asHome+" | grep "+ serverConfig +" | grep java | grep -v bash").getStdout().contains(asHome);
        } else {
		    return runAndWait("ps ax | grep "+asHome+" | grep java | grep -v bash").getStdout().contains(asHome);
        }
	}
	/**
	 *
	 * @param logFile relative path located in {@link AS7SSHClient#getAsHome()} to server's boot.log logFile
	 * @return server startup time by parsing 1st line of it's log file
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
