package com.redhat.qe.jon.common.util;

import com.redhat.qe.tools.SSHCommandResult;

import java.util.Date;

/**
 * @author rhatlapa@redhat.com
 */
public interface IAS7CommandRunner {

    public String getAsHome();
    public void restart(String script);
    public void restart(String script, String[] envp);
    public void start(String script);
    public void start(String script, String[] envp);
    public void stop();
    public boolean isRunning();
    public Date getStartupTime(String logFile);
    public String getJavaHome();

    public void killProcess(String pid);

    public void connect();
    public void disconnect();
    public boolean isRemote();

    public SSHCommandResult runAndWait(String command);

    /**
     * Creates directory including ancestors if they don't exist
     * @param dir directory to be created
     */
    public boolean mkdirs(String dir);

}
