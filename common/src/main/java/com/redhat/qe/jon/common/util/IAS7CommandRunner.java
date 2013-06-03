package com.redhat.qe.jon.common.util;

import java.util.Date;

/**
 * @author: rhatlapa@redhat.com
 */
public interface IAS7CommandRunner {

    public String getAsHome();
    public void restart(String script);
    public void start(String script);
    public void stop();
    public boolean isRunning();
    public Date getStartupTime(String logFile);
    public String getJavaHome();

    public void killProcess(String pid);

}
