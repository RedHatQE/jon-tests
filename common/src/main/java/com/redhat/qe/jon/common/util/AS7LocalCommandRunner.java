package com.redhat.qe.jon.common.util;

import com.redhat.qe.jon.common.*;
import com.redhat.qe.tools.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * AS7LocalCommandRunner provides platform independent implementation of IAS7CommandRunner
 * @author rhatlapa@redhat.com
 */
public class AS7LocalCommandRunner extends LocalCommandRunner implements IAS7CommandRunner {

    private String serverConfig; // allows to recognize the correct AS7 server process
    private final String asHome;
    private static final SimpleDateFormat sdfServerLog = new SimpleDateFormat("HH:mm:ss");
    private String asIdentifier;
    private static final Platform platform = new Platform();
    private static final String sep = File.separator;

    public AS7LocalCommandRunner(String asHome) {
        super(asHome);
        this.asHome = asHome;
        setAS7Identifier();
    }

    public AS7LocalCommandRunner(String asHome, String serverConfig) {
        super(asHome);
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
        this.asIdentifier = this.asIdentifier.replaceAll(Constants.TWO_DOUBLE_BACKSLASHES, Constants.FOUR_DOUBLE_BACKSLASHES);
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
        if (envp != null && envp.length > 0) {
            for (String e : envp) {
                sb.append(e).append(" ");
            }
        }

        runCommand(sb.toString()+"./"+script, new File(asHome, "bin"));
        Library.sleepFor(3 * 1000);
    }

    /**
     * stops server by killing it
     */
    public void stop() {
        String pids = null;

        if (platform.isWindows()) {
            String winFilter = "Commandline like '%" + asIdentifier + "%' AND Commandline like '%java%'";
            if (serverConfig != null) {
                winFilter += "AND Commandline like '%" + serverConfig + "%'";
            }
            pids = runAndWait("wmic process where (" + winFilter + " AND name!='WMIC.exe') get Processid").getStdout();
        } else {
            String grepFiltering = getGrepFiltering(false);
            boolean jpsSupported = isJpsSupported();
            if (jpsSupported) {
                pids = runAndWait(getJpsCommand() + " | " + grepFiltering + " | awk '{print $1}'").getStdout();
            }
            if (!jpsSupported || pids.trim().isEmpty()) {
                pids = runAndWait("ps -ef | " + getGrepFiltering(true) + " | awk '{print $2}'").getStdout();
            }
        }
        if (pids!=null && pids.length()>0) {
            for (String pid : pids.split("[\r\n]+")) {
                pid = pid.trim();
                if (!pid.equalsIgnoreCase("processid")) {
                  killProcess(pid);
                }
            }
        }
    }

    /**
     * checks if jps is supported on the remote machine
     * @return true if jps command is available on the remote machine, false otherwise
     */
    public boolean isJpsSupported() {

        SSHCommandResult res = runAndWait("jps -mlvV &>/dev/null || $JAVA_HOME/bin/jps -mlvV &>/dev/null || "+getJavaHome()+"/bin/jps -mlvV &>/dev/null");

        return res.getExitCode().intValue() == 0;
    }

    /**
     * check whether EAP server is running
     * @return true if server process is running
     */
    public boolean isRunning() {
        boolean running = false;
        if (platform.isWindows()) {
            String winFilter = "Commandline like '%" + asIdentifier + "%' AND Commandline like '%java%'";
            if (serverConfig != null) {
                winFilter += " AND Commandline like '%" + serverConfig + "%'";
            }
            String pids = runAndWait("wmic process where (" + winFilter + " AND name!='WMIC.exe') get Processid").getStdout();
            running = !(pids.trim().isEmpty());
        } else {

            String grepFiltering = getGrepFiltering(false);

            boolean jpsSupported = isJpsSupported();
            if (jpsSupported) {
                SSHCommandResult jpsResult = runAndWait(getJpsCommand() + " | " + grepFiltering);
                running = jpsResult.getStdout().contains(asIdentifier) || jpsResult.getStderr().contains(asIdentifier);
                // helps in case of Solaris and 32bit vs 64bit java
                if (!running && runAndWait("jps -mlvV &>/dev/null").getExitCode().intValue()==0) {
                    running = runAndWait("jps -mlvV | " + grepFiltering).getStdout().contains(asIdentifier);
                }
            }
            if (!jpsSupported || !running) {
                grepFiltering = getGrepFiltering(true);
                running = runAndWait("ps -ef | " + grepFiltering).getStdout().contains(asIdentifier);
            }
        }
        return running;
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
        String killCommand;
        if (platform.isWindows()) {
            killCommand = "taskkill /F /T /PID " + pid;
        } else {
            killCommand = "kill -9 " + pid;
        }
        log.info("Killing " + pid + " using command " + killCommand);
        runAndWait(killCommand);
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
     * @return jps command which takes into account JAVA_HOME env variable
     */
    public String getJpsCommand() {
        return  "{ " +getJavaHome()+sep+"bin"+sep+"jps -mlvV || $JAVA_HOME/bin/jps -mlvV || jps -mlvV; }";
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
                return sdfServerLog.parse(dateStr.trim());
            }

        } catch (ParseException e) {
            throw new RuntimeException("Unable to determine server startup time", e);
        }
    }
}
