package com.redhat.qe.jon.clitest.base;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.auto.testng.TestScript;
import com.redhat.qe.jon.clitest.base.Configuration.PARAM;
import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;


/**
 * @author jkandasa@redhat.com (Jeeva Kandasamy)
 * Feb 15, 2012
 */
public abstract class CliTestScript extends TestScript{
	
	private static Logger _logger = Logger.getLogger(CliTestScript .class.getName());
	
	public CliTestScript(){
		super();
	}

	@BeforeSuite
	public void loadBeforeSuite() throws IOException, CliTasksException{
		_logger.log(Level.INFO, "Loading before Suite");
		Configuration config = Configuration.load();
		CliTasks.getCliTasks().getConnection(	config.get(PARAM.HOST_NAME), 
												config.get(PARAM.HOST_USER),
												config.get(PARAM.HOST_PASSWORD));
		
		CliTest.rhqTarget = config.get(PARAM.RHQ_TARGET);
		CliTest.cliShLocation = config.get(PARAM.CLI_AGENT_BIN_SH);
		if (StringUtils.trimToNull(CliTest.cliShLocation)==null) {
			_logger.info("Property "+PARAM.CLI_AGENT_BIN_SH+" was not defined, auto-installing CLI and auto-detecting");
			// auto-install and setup cli executable
			// download from target server
			CliTasks.getCliTasks().runCommnad("wget -nv http://"+CliTest.rhqTarget+":7080/client/download -O rhq-cli.zip  2>&1");
			// detect CLI_HOME from zip content
			String cliHome = CliTasks.getCliTasks().runCommnad("zip -sf rhq-cli.zip | head -n2 | grep cli").trim();
			CliTest.cliShLocation = cliHome+"bin/rhq-cli.sh";
			// unzip CLI
			CliTasks.getCliTasks().runCommnad("rm -rf "+cliHome+" && unzip rhq-cli.zip; rm -f rhq-cli.zip");
			_logger.info("Property "+PARAM.CLI_AGENT_BIN_SH+" was autodetected to "+CliTest.cliShLocation);
		}
		CliTest.rhqCliJavaHome = config.get(PARAM.RHQ_CLI_JAVA_HOME);
		
		_logger.log(Level.INFO, "Loaded before Suite");
	}

	@AfterSuite
	public void closeBrowser() {
		_logger.log(Level.INFO, "Executing after Suite");
		CliTasks.getCliTasks().closeConnection();
		_logger.log(Level.INFO, "Completed after Suite");
		
	}
}
