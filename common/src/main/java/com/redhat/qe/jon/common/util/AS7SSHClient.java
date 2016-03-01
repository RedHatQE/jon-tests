package com.redhat.qe.jon.common.util;

import com.redhat.qe.jon.common.*;
import com.redhat.qe.tools.*;

import java.io.*;
import java.text.*;
import java.util.*;



public class AS7SSHClient extends SSHClient implements IAS7CommandRunner {

    private String serverConfig; // allows to recognize the correct AS7 server process
	private final String asHome;
    private String asIdentifier;
    private static final Platform platform = new Platform();

	public AS7SSHClient(String asHome) {
		super();
		this.asHome = asHome;
        setAS7Identifier();
	}
	public AS7SSHClient(String asHome, String user,String host, String pass) {
		super(user,host,pass);
		this.asHome = asHome;
        setAS7Identifier();

	}
	public AS7SSHClient(String asHome, String user,String host, File keyFile, String pass) {
	    super(user,host,keyFile,pass);
	    this.asHome = asHome;
        setAS7Identifier();
	}
    public AS7SSHClient(String asHome, String serverConfig, String user,String host, File keyFile, String pass) {
        super(user,host,keyFile,pass);
        this.asHome = asHome;
        this.serverConfig = serverConfig;
        setAS7Identifier();
    }

    /**
     * Sets identifier for AS7 server based on asHome
     */
    private void setAS7Identifier() {
        if (asHome == null) {
            throw new IllegalArgumentException("Unable to count as7Identifier without asHome specified");
        }
        File asHomeDir = new File(asHome);
        if (asHomeDir.getParent() != null) {
            this.asIdentifier = asHomeDir.getParentFile().getName() + File.separator + asHomeDir.getName();
        } else {
            this.asIdentifier = asHomeDir.getName();
        }
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
		restart(script,null);
	}

    public void restart(String script, String[] envp) {
        stop();
        try {
            Thread.currentThread().join(10*1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        start(script,envp);
    }

    /**
	 * starts server
	 * @param script name of startup script located in {@link AS7SSHClient#getAsHome()} / bin
	 */
	public void start(String script) {
		start(script,null);
	}

    /**
     * starts server
     * @param script name of startup script located in {@link AS7SSHClient#getAsHome()} / bin
     * @param envp array of environment variables (VAR=value) to get exported for startup script
     */
    public void start(String script, String[] envp) {
        StringBuilder sb = new StringBuilder();
        if (envp!=null && envp.length>0) {
            for (String e : envp) {
                sb.append("export ").append(e).append("; ");
            }
        }
        //run("cd "+asHome+"/bin && "+sb.toString()+" nohup ./"+script+" &");
        run("cd "+asHome+"/bin && "+sb.toString()+" nohup ./"+script);
    }

    /**
	 * stops server by killing it
	 */
	public void stop() {
		String pids = null;
        String grepFiltering = getGrepFiltering(false);
        boolean jpsSupported = isJpsSupported();
        if (jpsSupported) {
            pids = runAndWait(getJpsCommand() + " | " + grepFiltering + " | awk '{print $1}'").getStdout();
        }
        if (!jpsSupported || pids.trim().isEmpty()) {
            pids = runAndWait("ps -ef | " +  getGrepFiltering(true) + " | awk '{print $2}'").getStdout();
        }

		if (pids!=null && pids.length()>0) {
			for (String pid : pids.split("\n")) {
				killProcess(pid);
			}
		}
	}

    /**
     *
     * @param filterByJava if the output is from jps it doesn't need to contain java
     * @return string usable for filtering using grep commands
     */
    private String getGrepFiltering(boolean filterByJava) {
        String grepFiltering = "";
        String grepToRemoveNonJava = "grep -v bash | grep -v -w grep";
        if (filterByJava) {
            grepToRemoveNonJava = "grep java | " + grepToRemoveNonJava;
        }
        if (serverConfig != null) {
            grepFiltering = "grep "+asIdentifier+" | grep " +serverConfig+ " | " + grepToRemoveNonJava;
        } else {
            grepFiltering = "grep "+asIdentifier+" | " + grepToRemoveNonJava;
        }
        return grepFiltering;
    }

    /**
     * checks if jps is supported on the remote machine
     * @return true if jps command is available on the remote machine, false otherwise
     */
    public boolean isJpsSupported() {

        SSHCommandResult res = runAndWait("jps &>/dev/null || $JAVA_HOME/bin/jps &>/dev/null || "+getJavaHome()+"/bin/jps &>/dev/null");

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

    @Override
    public void killProcess(String pid) {
        runAndWait("kill -9 " + pid);
    }

    /**
     * @return jps command which takes into account JAVA_HOME env variable
     */
    public String getJpsCommand() {
        return "{ " +getJavaHome()+"/bin/jps -mlvV || $JAVA_HOME/bin/jps -mlvV || jps -mlvV; }";
    }

	/**
	 * check whether EAP server is running
	 * @return true if server process is running
	 */
	public boolean isRunning() {
        String grepFiltering = getGrepFiltering(false);
        boolean running = false;
        boolean jpsSupported = isJpsSupported();
        if (jpsSupported) {
            running = runAndWait(getJpsCommand() + " | " + grepFiltering).getStdout().contains(asIdentifier);
            // helps in case of Solaris and 32bit vs 64bit java
            if (!running && runAndWait("jps -mlvV &>/dev/null").getExitCode().intValue()==0) {
                running = runAndWait("jps -mlvV | " + grepFiltering).getStdout().contains(asIdentifier);
            }
        }
        if (!jpsSupported || !running) {
            running = runAndWait("ps -ef | " +  getGrepFiltering(true)).getStdout().contains(asIdentifier);
        }
        return running;
	}

	/**
	 * @param logFile relative path located in {@link AS7SSHClient#getAsHome()} to server's log with boot information (for 6.0.x it is boot.log, for 6.1.x standalone mode it is server.log) logFile
	 * @return server startup time by parsing 1st line of it's log file
	 */
	public Date getStartupTime(String logFile) {
        String dateStringFilteringCommand = "";
        String startupTimeFilter = "\"\\[org\\.jboss\\.modules\\]\"";
        String startupTimesFilteringCommand = "";
        String filteringUtil;
        if (platform.isWindows()) {
            filteringUtil = "FINDSTR";
        } else {
            filteringUtil = "grep";
        }

        // getting all the lines with the specific String
        startupTimesFilteringCommand = filteringUtil + " " + startupTimeFilter + " " + asHome + File.separator + logFile;
        String startupTimesStrOut = runAndWait(startupTimesFilteringCommand).getStdout().trim();

        try {
            if (startupTimesStrOut.isEmpty() && logFile.endsWith("boot.log")) { // done for managing that boot.log information are put in server.log since EAP 6.1 boot.log no longer exists
                return getStartupTime(logFile.replace("boot.log", "server.log"));
            } else {
                // retrieve only the last line:
                String[] dateStrArray = startupTimesStrOut.split("[\r\n]+");
                String lastStartupDateStr = dateStrArray[dateStrArray.length-1];

                // retrive only the date time part
                String dateStr = lastStartupDateStr.split(",")[0];

                SimpleDateFormat sdfServerLog = null;
                // create correct date format based on EAP version
                if (dateStr.contains("-")){
                    // long format in EAP7
                    sdfServerLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }else{
                    // short format in EAP6
                    sdfServerLog = new SimpleDateFormat("HH:mm:ss");
                }
                return sdfServerLog.parse(dateStr.trim());
            }

        } catch (ParseException e) {
            throw new RuntimeException("Unable to determine server startup time", e);
        }
	}

    /**
     * Creates directory including ancestors if they don't exist
     * @param dir directory to be created
     */
    public boolean mkdirs(String dir) {
        return runAndWait("mkdir -p " + dir).getExitCode()==0;
    }
}
