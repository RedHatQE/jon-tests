package com.redhat.qe.jon.clitest.base;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.auto.testng.TestScript;
import com.redhat.qe.jon.clitest.tasks.CliTasks;
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
	public void loadBeforeSuite() throws IOException{
		_logger.log(Level.INFO, "Loading before Suite");
		CliTasks.getCliTasks().getConnection(System.getenv().get("HOST_NAME"), System.getenv().get("HOST_USER"), System.getenv().get("HOST_PASSWORD"));
		CliTest.cliShLocation = System.getenv().get("CLI_AGENT_BIN_SH");
		CliTest.javaHome = System.getenv().get("JAVA_HOME").trim();
		CliTest.rhqTarget = System.getenv().get("RHQ_TARGET").trim();
		String jsFileLoc = System.getProperty("js.files.dir");
		if(jsFileLoc == null){
			jsFileLoc = "resources/js-files/"; //taking default location
			_logger.info("JS file location is not specified.. Taking defailt location..."+jsFileLoc);
		}
		CliTest.jsFileLocation = System.getProperty("user.dir")+"/"+jsFileLoc;
		if(!CliTest.jsFileLocation.endsWith("/")){
			CliTest.jsFileLocation += "/";
		}
		_logger.log(Level.INFO, "Loaded before Suite");
	}

	@AfterSuite
	public void closeBrowser() {
		_logger.log(Level.INFO, "Executing after Suite");
		CliTasks.getCliTasks().closeConnection();
		_logger.log(Level.INFO, "Completed after Suite");
		
	}
}
