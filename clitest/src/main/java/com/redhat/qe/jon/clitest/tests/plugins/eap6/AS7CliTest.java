package com.redhat.qe.jon.clitest.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.jboss.sasl.util.UsernamePasswordHashUtil;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.ServerStartConfig.ConfigFile;
import com.redhat.qe.jon.common.util.AS7DMRClient;
import com.redhat.qe.jon.common.util.AS7SSHClient;
import com.redhat.qe.jon.common.util.RestClient;
import com.redhat.qe.jon.common.util.SSHClient;
import com.redhat.qe.tools.checklog.CheckLog;
import com.redhat.qe.tools.checklog.LogFile;

@CheckLog(
	enabled=false,
	logs={
		@LogFile(id="agent",user="${jon.agent.user}",pass="${jon.agent.password}",host="${jon.agent.host}",logFile="rhq-agent/logs/agent.log"),
		@LogFile(id="server",user="${jon.server.user}",pass="${jon.server.password}",host="${jon.server.host}",logFile="${jon.server.home}/logs/${jon.server.logfile}")
	}	
)
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
	public void loadEap6Properties() {
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
		checkRequiredProperties(
			"jon.agent.name","jon.agent.user","jon.agent.host","jon.agent.password",
			"as7.standalone.home","as7.domain.home",
			"jon.server.host","jon.server.user","jon.server.password"
		);
		String agentPass = System.getProperty("jon.agent.password","hudson");
		String agentUser = System.getProperty("jon.agent.user","hudson");
		agentName = System.getProperty("jon.agent.name");
		standalone1Home = System.getProperty("as7.standalone.home");
		standalone1HostName = System.getProperty("jon.agent.host");
		domainHome = System.getProperty("as7.domain.home");
		domainHostName = System.getProperty("jon.agent.host");
		sshDomain = new AS7SSHClient(domainHome,agentUser,domainHostName,agentPass);
		sshStandalone = new AS7SSHClient(standalone1Home,agentUser,standalone1HostName,agentPass);
		installRHQUser(sshDomain,null,"/domain/configuration/mgmt-users.properties");
		installRHQUser(sshStandalone,null,"/standalone/configuration/mgmt-users.properties");
		if (System.getProperty("jon.server.home","").equals("")) {
		    log.fine("Auto-detecting [jon.server.home]");
		    String value = RestClient.detectServerInstallDir();
		    log.fine("[jon.server.home] detected : "+value);
		    System.setProperty("jon.server.home", value);
		}
		// detect server log file
		// default for RHQ < 4.6.0 or JON < 3.2.0 
		log.fine("Auto-detecting [jon.server.logfile]");
		System.setProperty("jon.server.logfile", "rhq-server-log4j.log");
		SSHClient client = new SSHClient(System.getProperty("jon.server.user"),System.getProperty("jon.server.host"),System.getProperty("jon.server.password"));
		
		// for  RHQ >= 4.6.0 and or >= 3.2.0 
		if( client.runAndWait("test -f "+System.getProperty("jon.server.home")+"/logs/server.log").getExitCode() == 0) {
		    System.setProperty("jon.server.logfile", "server.log");
		}
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
	

	@Override
	public void runJSfile(String rhqTarget, String cliUsername,
			String cliPassword, String jsFile, String cliArgs,
			String expectedResult, String makeFilure, String jsDepends,
			String resSrc, String resDst) throws IOException, CliTasksException {
		
		if (StringUtils.trimToNull(cliArgs)==null) {
			cliArgs = "--args-style=named agent="+agentName;
		}
		else {
			cliArgs+= " agent="+agentName;
		}
		// always add rhqapi.js as first dependency
		// we're using public version from rhq-project/samples
		//String rhqapi = "https://raw.github.com/rhq-project/samples/master/cli/rhqapi/rhqapi.js";
		String rhqapi = "/js-files/rhqapi.js";
		if (StringUtils.trimToNull(jsDepends)==null) {
		    jsDepends = rhqapi;
		}
		else {
		    jsDepends = rhqapi+","+jsDepends; 
		}		
		super.runJSfile(rhqTarget, cliUsername, cliPassword, jsFile, cliArgs,
				expectedResult, makeFilure, jsDepends, resSrc, resDst);
	}
	/**
	 * 
	 * @param start
	 * @param serverType either "standalone" or "domain" string
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void serverStartup(ServerStartConfig start, String serverType) throws IOException, CliTasksException {
		if (!("standalone".equals(serverType)||"domain".equals(serverType))) {
			throw new RuntimeException("serverType parameter must be either [standalone] or [domain] value!");
		}
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
		// do we run Domain or Standalone? we switch it by  by including eap6/{domain|standalone}/server.js
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/discoveryTest.js", null, start.getExpectedMessage()+",availability=UP", null,"eap6/"+serverType+"/server.js",null,null);
	}
}
