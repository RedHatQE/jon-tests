package com.redhat.qe.jon.clitest.tests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;

public class CliTest{
	private static Logger _logger = Logger.getLogger(CliTest.class.getName());
	private String cliShLocation;
	private String jsFileLocation;
	private String javaHome;
	private String rhqTarget;
	private String cliUsername;
	private String cliPassword;
	private String makeFailure;
	private CliTasks cliTasks;
		
	
	
	@BeforeClass
	public void loadBeforeClass() throws IOException{
		cliTasks = CliTasks.getCliTasks();
		cliTasks.getConnection(System.getenv().get("HOST_NAME"), System.getenv().get("HOST_USER"), System.getenv().get("HOST_PASSWORD"));
		this.cliShLocation = System.getenv().get("CLI_AGENT_BIN_SH");
		this.javaHome = System.getenv().get("JAVA_HOME");
		
		this.jsFileLocation = System.getProperty("user.dir")+"/"+System.getProperty("js.files.dir");
		if(!jsFileLocation.endsWith("/")){
			jsFileLocation += "/";
		}
	}
	
	//@Parameters({"rhq.target","cli.username","cli.password","js.file","cli.args","expected.result","make.failure"})
	public void loadSetup(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, @Optional String makeFailure) throws IOException{
		if(rhqTarget != null)
			this.rhqTarget = rhqTarget;
		if(cliUsername != null)
			this.cliUsername = cliUsername;
		if(cliPassword != null)
			this.cliPassword = cliPassword;
		if(makeFailure != null)
			this.makeFailure = makeFailure;
	}
	
	@Parameters({"rhq.target","cli.username","cli.password","js.file","cli.args","expected.result","make.failure"})
	@Test
	public void runJSfile(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, String jsFile, @Optional String cliArgs, @Optional String expectedResult, @Optional String makeFilure) throws IOException, CliTasksException{
		loadSetup(rhqTarget, cliUsername, cliPassword, makeFilure);
		String output = cliTasks.runCommnad("export RHQ_CLI_JAVA_HOME="+javaHome+";"+this.cliShLocation+" -s "+this.rhqTarget+" -u "+this.cliUsername+" -p "+this.cliPassword+" -f "+jsFileLocation+jsFile);
		_logger.log(Level.INFO, output);
	}	
}
