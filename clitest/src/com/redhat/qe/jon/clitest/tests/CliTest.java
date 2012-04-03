package com.redhat.qe.jon.clitest.tests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliTestScript;
import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;

public class CliTest extends CliTestScript{
	private static Logger _logger = Logger.getLogger(CliTest.class.getName());
	public static String cliShLocation;
	public static String jsFileLocation;
	public static String javaHome;
	public static String rhqTarget;
	private String cliUsername;
	private String cliPassword;
	private CliTasks cliTasks;
		
	
	
	
	
	//@Parameters({"rhq.target","cli.username","cli.password","js.file","cli.args","expected.result","make.failure"})
	public void loadSetup(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, @Optional String makeFailure) throws IOException{
		if(rhqTarget != null)
			CliTest.rhqTarget = rhqTarget;
		if(cliUsername != null)
			this.cliUsername = cliUsername;
		if(cliPassword != null)
			this.cliPassword = cliPassword;
	}
	
	@Parameters({"rhq.target","cli.username","cli.password","js.file","cli.args","expected.result","make.failure"})
	@Test
	public void runJSfile(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, String jsFile, @Optional String cliArgs, @Optional String expectedResult, @Optional String makeFilure) throws IOException, CliTasksException{
		loadSetup(rhqTarget, cliUsername, cliPassword, makeFilure);
		cliTasks = CliTasks.getCliTasks();
		String consoleOutput = cliTasks.runCommnad("export RHQ_CLI_JAVA_HOME="+javaHome+";"+CliTest.cliShLocation+" -s "+CliTest.rhqTarget+" -u "+this.cliUsername+" -p "+this.cliPassword+" -f "+jsFileLocation+jsFile);
		_logger.log(Level.INFO, consoleOutput);
		if(makeFilure != null){
			cliTasks.validateErrorString(consoleOutput , makeFilure);
		}
		if(expectedResult != null){
			cliTasks.validateExpectedResultString(consoleOutput , expectedResult);
		}
	}	
	
	@Parameters({"rhq.target","cli.username","cli.password","js.file","cli.args","expected.result","make.failure"})
	@Test
	public void getCliVersion(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, String jsFile, @Optional String cliArgs, @Optional String expectedResult, @Optional String makeFilure) throws IOException, CliTasksException{
		loadSetup(rhqTarget, cliUsername, cliPassword, makeFilure);
		cliTasks = CliTasks.getCliTasks();
		String consoleOutput = cliTasks.runCommnad("export RHQ_CLI_JAVA_HOME="+javaHome+";"+CliTest.cliShLocation+" -s "+CliTest.rhqTarget+" -u "+this.cliUsername+" -p "+this.cliPassword+" -f "+jsFileLocation+jsFile);
		_logger.log(Level.INFO, consoleOutput);
		if(makeFilure != null){
			cliTasks.validateErrorString(consoleOutput , makeFilure);
		}
		if(expectedResult != null){
			cliTasks.validateExpectedResultString(consoleOutput , expectedResult);
		}
	}	
	
}
