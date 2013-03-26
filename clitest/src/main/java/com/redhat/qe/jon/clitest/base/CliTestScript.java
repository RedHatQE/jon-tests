package com.redhat.qe.jon.clitest.base;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.texen.util.FileUtil;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;


import com.redhat.qe.jon.clitest.base.Configuration.PARAM;
import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;
import com.redhat.qe.jon.common.TestScript;
import com.redhat.qe.jon.common.util.LocalCommandRunner;
import com.redhat.qe.jon.common.util.SSHClient;


/**
 * @author jkandasa@redhat.com (Jeeva Kandasamy)
 * Feb 15, 2012
 */
public abstract class CliTestScript extends TestScript{
	
	private static Logger _logger = Logger.getLogger(CliTestScript .class.getName());
	private static Configuration config = null;
	
	public CliTestScript(){
		super();
	}

	@BeforeSuite
	public void loadBeforeSuite() throws IOException, CliTasksException{
		_logger.log(Level.INFO, "Loading before Suite");
		config = Configuration.load();
		CliTasks.getCliTasks().initialize(	config.get(PARAM.HOST_NAME), 
												config.get(PARAM.HOST_USER),
												config.get(PARAM.HOST_PASSWORD));
		
		CliTest.rhqTarget = config.get(PARAM.RHQ_TARGET);
		CliTest.cliShLocation = config.get(PARAM.CLI_AGENT_BIN_SH);
		if (StringUtils.trimToNull(CliTest.cliShLocation)==null) {
			_logger.info("Property "+PARAM.CLI_AGENT_BIN_SH+" was not defined, auto-installing CLI and auto-detecting");
			// auto-install and setup cli executable
			// download from target server
			CliTasks.getCliTasks().runCommand("wget -nv http://"+CliTest.rhqTarget+":7080/client/download -O rhq-cli.zip  2>&1");
			// detect CLI_HOME from zip content
			String cliHome = CliTasks.getCliTasks().runCommand("unzip -l rhq-cli.zip | head -n4 | tail -1 | grep cli | awk '{print $4}'").trim();
			CliTest.cliShLocation = cliHome+"bin/rhq-cli.sh";
			// unzip CLI
			CliTasks.getCliTasks().runCommand("rm -rf "+cliHome+" && unzip rhq-cli.zip; rm -f rhq-cli.zip");
			_logger.info("Property "+PARAM.CLI_AGENT_BIN_SH+" was autodetected to "+CliTest.cliShLocation);
		}
		CliTest.rhqCliJavaHome = config.get(PARAM.RHQ_CLI_JAVA_HOME);
		
		_logger.log(Level.INFO, "Loaded before Suite");
	}

	@AfterSuite
	public void cleanUp() {
		_logger.log(Level.INFO, "Executing after Suite");
		CliTasks.getCliTasks().closeConnection();
		
		gatherServerLog(config.get(PARAM.RHQ_TARGET), System.getProperty("jon.server.log.path"));
		
		String agents = System.getProperty("jon.agent.hosts");
		if(agents == null){
			_logger.log(Level.INFO, "IPs of RHQ/JON agents not defined, skipping gathering.");
		}else{
			String[] ips = agents.split(",");
			for(String ip : ips){
				gatherAgentLog(ip.trim());
			}
		}
		_logger.log(Level.INFO, "Completed after Suite");
		
	}
	/**
	 * Gets the server log from remote server to 'logs' directory in actual directory
	 * @param serverHost ip or hostname of RHQ/JON server's host
	 * @param serverLogPath path to the RHQ/JON server log (relative or absolute)
	 */
	private void gatherServerLog(String serverHost, String serverLogPath){
		SSHClient rhqServer = new SSHClient("hudson",serverHost,"hudson");
		LocalCommandRunner localServer = new LocalCommandRunner(".");
		createLogDir();
		
		try{
			rhqServer.connect();
			if(serverLogPath == null){
				_logger.log(Level.INFO, "Location of RHQ/JON server log not defined, skipping gathering.");
				// TODO try to find the server log
			}else{
				_logger.log(Level.INFO, "Gathering the RHQ/JON server log from " + rhqServer.getHost());
				// get it from remote server to local /tmp
				rhqServer.getFile(serverLogPath, "/tmp");
				// copy it to logs directory
				localServer.copyFile("/tmp/server.log", "logs", "server"+rhqServer.getHost()+".log");
			}
		}catch(Exception ex){
			_logger.log(Level.WARNING, "Failed to gather the RHQ server log." ,ex);
		}finally{
			rhqServer.disconnect();
		}
	}
	
	/**
	 * Gets the agent log from remote server to 'logs' directory in actual directory
	 * @param agentHost ip or hostname of RHQ/JON agent's host
	 */
	private void gatherAgentLog(String agentHost){
		SSHClient agentHostClient = new SSHClient("hudson",agentHost,"hudson");
		LocalCommandRunner localServer = new LocalCommandRunner(".");
		createLogDir();
		
		try{
			agentHostClient.connect();
			_logger.log(Level.INFO, "Gathering the RHQ/JON agent log from host: " + agentHost);
			// get it from remote server to local /tmp
			agentHostClient.getFile("rhq-agent/logs/agent.log", "/tmp");
			// copy it to logs directory
			localServer.copyFile("/tmp/agent.log", "logs", "agent"+agentHostClient.getHost()+".log");
		}catch(Exception ex){
			_logger.log(Level.WARNING, "Failed to gather the agent log." ,ex);
		}finally{
			agentHostClient.disconnect();
		}
	}
	
	/**
	 * Creates 'logs' directory in actual directory if it doesn't already exist
	 * @return true if it is successfully created
	 */
	private boolean createLogDir(){
		File destLogDir = new File("logs");
		// if the directory does not exist, create it
		if (!destLogDir.exists())
		{
			return destLogDir.mkdir();  
		}
		return true;
	}
}
