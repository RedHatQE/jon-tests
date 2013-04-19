package com.redhat.qe.jon.common.util;

import com.redhat.qe.tools.SSHCommandResult;

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
        String grepFiltering = getGrepFiltering();

        if (isJpsSupported()) {
            pids = runAndWait(getJpsCommand() + " | " + grepFiltering + " | awk '{print $1}'").getStdout();
        } else {
            pids = runAndWait("ps -ef | " +  grepFiltering + " | awk '{print $2}'").getStdout();
        }

		if (pids!=null && pids.length()>0) {
			for (String pid : pids.split("\n")) {
				runAndWait("kill -9 "+pid);
			}
		}
	}

    private String getGrepFiltering() {
        String grepFiltering = "";
        if (serverConfig != null) {
            grepFiltering = "grep "+asHome+" | grep "+serverConfig+" | grep java | grep -v bash | grep -v -w grep";
        } else {
            grepFiltering = "grep "+asHome+" | grep java | grep -v bash | grep -v -w grep";
        }
        return grepFiltering;
    }

    /**
     * checks if jps is supported on the remote machine
     * @return true if jps command is available on the remote machine, false otherwise
     */
    public boolean isJpsSupported() {

        SSHCommandResult res = runAndWait("jps &>/dev/null || $JAVA_HOME/bin/jps &>/dev/null || "+getJavaHome()+"/bin/jps -mlvV &>/dev/null");

        return res.getExitCode().intValue() == 0;
    }

    /**
     *
     * @return value of JAVA_HOME sys environment, if not set, empty string is returned
     */
    public String getJavaHome() {
        // there is great chance, that the path to JAVA_HOME on this machine would be also valid in the other machine
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            javaHome = "";
        }
        return javaHome;
    }

    /**
     * @return jps command which takes into account JAVA_HOME env variable
     */
    public String getJpsCommand() {
        return "{ " +getJavaHome()+"/bin/jps -mlvV; || $JAVA_HOME/bin/jps -mlvV || jps -mlvV }";
    }

	/**
	 * check whether EAP server is running
	 * @return true if server process is running
	 */
	public boolean isRunning() {
        String grepFiltering = getGrepFiltering();
        boolean running = false;
        boolean jpsSupported = isJpsSupported();
        if (jpsSupported) {
            running = runAndWait(getJpsCommand() + " | " + grepFiltering).getStdout().contains(asHome);
        }
        if (!jpsSupported || !running) {
            running = runAndWait("ps -ef | " +  grepFiltering).getStdout().contains(asHome);
        }
        return running;
	}

	/**
	 * @param logFile relative path located in {@link AS7SSHClient#getAsHome()} to server's log with boot information (for 6.0.x it is boot.log, for 6.1.x standalone mode it is server.log) logFile
	 * @return server startup time by parsing 1st line of it's log file
	 */
	public Date getStartupTime(String logFile) {
        String dateStringFilteringCommand = "";
        if (logFile.endsWith("server.log")) {
            dateStringFilteringCommand += "grep '\\[org\\.jboss\\.modules\\]' " +asHome+"/"+logFile +" | tail -1 | awk -F, '{print $1}'";
        } else {
             dateStringFilteringCommand += "head -n1 "+asHome+"/"+logFile+" | awk -F, '{print $1}' ";
        }
        String dateStr = runAndWait(dateStringFilteringCommand).getStdout().trim();
        try {
            if (dateStr.isEmpty() && logFile.endsWith("boot.log")) { // done for managing that boot.log information are put in server.log since EAP 6.1 and boot.log no longer exists
                return getStartupTime(logFile.replace("boot.log", "server.log"));
            } else {
                return sdfServerLog.parse(dateStr);
            }
        } catch (ParseException e) {
			throw new RuntimeException("Unable to determine server startup time", e);
		}
	}


}
