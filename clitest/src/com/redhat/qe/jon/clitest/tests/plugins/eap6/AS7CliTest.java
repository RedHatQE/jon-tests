package com.redhat.qe.jon.clitest.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.testng.annotations.BeforeSuite;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.ServerStartConfig.ConfigFile;
import com.redhat.qe.jon.common.util.AS7SSHClient;

public class AS7CliTest extends CliTest {

	protected static String agentName;
	protected static String standalone1Home;
	protected static String standalone1HostName;
	protected static String domainHome;
	protected static String domainHostName;
	protected AS7SSHClient sshClient;
	@BeforeSuite
	public void loadProperties() {
		try {
            System.getProperties().load(new FileInputStream(new File(System.getProperty("eap6plugin.configfile"))));
        } catch (Exception e) {
            try {
                System.getProperties().load(new FileInputStream(new File("config/eap6plugin.properties")));
            } catch (Exception ex) {
                try {
                    System.getProperties().load(new FileInputStream(new File("automatjon/jon/sahi/config/eap6plugin.properties")));
                } catch (Exception exc) {
                    log.severe("Could not load properties file for EAP6plugin testing: " + exc.getMessage() + " please provide the full path in system property \"eap6plugin.configfile\".");
                }
            }

        }
		agentName = System.getProperty("agent.name");
		standalone1Home = System.getProperty("as7.standalone1.home");
		standalone1HostName = System.getProperty("as7.standalone1.hostname");
		domainHome = System.getProperty("as7.domain.home");
		domainHostName = System.getProperty("as7.domain.hostname");
	}
	public void waitFor(int ms) {
		waitFor(ms, "Waiting");
	}
	public void waitFor(int ms,String message) {
		log.fine(message+" "+(ms/1000)+"s");
		if(ms<=0) {
			ms = 1;
		}
		try {
			Thread.currentThread().join(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void serverStartup(ServerStartConfig start, String jsFile) throws IOException, CliTasksException {
		String params = start.getStartCmd();
		if (start.getConfigs()!=null) {
			for (ConfigFile cf : start.getConfigs()) {
				cliTasks.copyFile(this.getClass().getResource(cf.getLocalPath()).getFile(), cf.getRemotePath());
				params+=" "+cf.getStartupParam();
			}
		}
		if (start.getPreStartCmd()!=null) {
			sshClient.runAndWait("cd "+sshClient.getAsHome()+" && "+start.getPreStartCmd());
		}
		sshClient.restart(params);
		waitFor(30*1000,"Waiting until EAP starts up");
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		sshClient.runAndWait("netstat -pltn | grep java");
		runJSfile(null, "rhqadmin", "rhqadmin", jsFile, "--args-style=named agent="+agentName, start.getExpectedMessage(), null);
	}
}
