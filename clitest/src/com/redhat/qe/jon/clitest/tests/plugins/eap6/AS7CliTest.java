package com.redhat.qe.jon.clitest.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.jboss.sasl.util.UsernamePasswordHashUtil;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.ServerStartConfig.ConfigFile;
import com.redhat.qe.jon.common.util.AS7DMRClient;
import com.redhat.qe.jon.common.util.AS7SSHClient;

public class AS7CliTest extends CliTest {

	protected static String agentName;
	protected static String standalone1Home;
	protected static String standalone1HostName;
	protected static String domainHome;
	protected static String domainHostName;
	protected static AS7SSHClient sshClient;
	protected static AS7SSHClient sshDomain;
	protected static AS7SSHClient sshStandalone;
	
	@BeforeSuite
	public void loadProperties() {
		try {
            System.getProperties().load(new FileInputStream(new File(System.getProperty("eap6plugin.configfile"))));
            log.fine("eap6plugin.properties loaded");
        } catch (Exception e) {
            try {
                System.getProperties().load(new FileInputStream(new File("config/eap6plugin.properties")));
                log.fine("eap6plugin.properties loaded");
            } catch (Exception ex) {
                try {
                    System.getProperties().load(new FileInputStream(new File("automatjon/jon/sahi/config/eap6plugin.properties")));
                    log.fine("eap6plugin.properties loaded");
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
		sshDomain = new AS7SSHClient(domainHome,"hudson",domainHostName,"hudson");
		sshStandalone = new AS7SSHClient(standalone1Home,"hudson",standalone1HostName,"hudson");
		installRHQUser(sshDomain,null,"/domain/configuration/mgmt-users.properties");
		installRHQUser(sshStandalone,null,"/standalone/configuration/mgmt-users.properties");
	}
	
	/**
	 * installs default RHQ user for given server
	 * if 'server' param exists in RHQ UI, 10min waiting is started  
	 * @param sshClient AS7SSHClient that can work with 'server' resource
	 * @param mgmtClient Management client that can manage 'server' resource (can be null)
	 * @param credFile relative path within {@link AS7SSHClient#getAsHome()} to save user name + hashed pass
	 */
	 public static void installRHQUser(AS7SSHClient sshClient, AS7DMRClient mgmtClient, String credFile) {	
		String user = "rhqadmin";
		String checkCmd = "grep '"+user+"' "+sshClient.getAsHome() + credFile;
		if (sshClient.runAndWait(checkCmd).getStdout().contains(user)) {
			log.info("rhqadmin already exists");
			return;
		}
		String pass = "rhqadmin";
		String hash = null;
		// we also add default user admin:admin
		String defaultUser = "admin";
		String defaultHash = null;
		// let's generate hash for pass and store it into mgmt-users.properties
		try {
			hash = new UsernamePasswordHashUtil().generateHashedHexURP(user,"ManagementRealm", pass.toCharArray());
			defaultHash = new UsernamePasswordHashUtil().generateHashedHexURP(defaultUser,"ManagementRealm", "admin".toCharArray());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"Unable to generate password hash to setup EAP", e);
		}
		
		StringBuilder command = new StringBuilder("echo " + defaultUser + "=" + defaultHash + " > ");
		command.append(sshClient.getAsHome() + credFile);
		sshClient.runAndWait(command.toString());
		log.info("Created default user:pass "+defaultUser+":admin");
		
		command = new StringBuilder("echo " + user + "=" + hash + " >> ");
		command.append(sshClient.getAsHome() + credFile);
		sshClient.runAndWait(command.toString());
		log.info("Created testing user:pass "+user+":"+pass);	
		if (mgmtClient!=null) {	
			mgmtClient.setUsername("rhqadmin");
			mgmtClient.setPassword("rhqadmin");
		}
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
		boolean running = sshClient.isRunning();
		if (!running) {
			// EAP failed to start, lets print its logs
			sshClient.runAndWait("cat "+sshClient.getAsHome()+"/standalone/log/boot.log");
			sshClient.runAndWait("cat "+sshClient.getAsHome()+"/standalone/log/server.log");
			sshClient.runAndWait("cat "+sshClient.getAsHome()+"/domain/log/host-controller.log");
		}
		Assert.assertTrue(running, "Server process is running");
		sshClient.runAndWait("netstat -pltn | grep java");
		runJSfile(null, "rhqadmin", "rhqadmin", jsFile, "--args-style=named agent="+agentName, start.getExpectedMessage()+",availability=UP", null,null,null,null);
	}
}
