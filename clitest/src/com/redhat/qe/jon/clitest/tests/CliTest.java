package com.redhat.qe.jon.clitest.tests;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterTest;
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
	public static String rhqCliJavaHome;
	public static String rhqTarget;
	private String cliUsername;
	private String cliPassword;
	protected CliTasks cliTasks;
	
	private String jsFileName;
	private static String remoteFileLocation = "/tmp/";
	
	public static boolean isVersionSet = false;
		
	
	
	
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
		// upload JS file to remote host first
		cliTasks.copyFile(jsFileLocation+jsFile, remoteFileLocation);
		jsFileName = new File(jsFile).getName();
		// autodetect RHQ_CLI_JAVA_HOME if not defined

		if (StringUtils.trimToNull(rhqCliJavaHome)==null) {
			rhqCliJavaHome = cliTasks.runCommnad("echo $JAVA_HOME").trim();
			_logger.log(Level.INFO,"Environment variable RHQ_CLI_JAVA_HOME was autodetected using JAVA_HOME variable");
		}
		String consoleOutput = null;
		if(cliArgs != null){
			consoleOutput = cliTasks.runCommnad("export RHQ_CLI_JAVA_HOME="+rhqCliJavaHome+";"+CliTest.cliShLocation+" -s "+CliTest.rhqTarget+" -u "+this.cliUsername+" -p "+this.cliPassword+" -f "+remoteFileLocation+jsFileName+" "+cliArgs);
		}else{
			consoleOutput = cliTasks.runCommnad("export RHQ_CLI_JAVA_HOME="+rhqCliJavaHome+";"+CliTest.cliShLocation+" -s "+CliTest.rhqTarget+" -u "+this.cliUsername+" -p "+this.cliPassword+" -f "+remoteFileLocation+jsFileName);
		}
		
		if(!isVersionSet){
			System.setProperty("rhq.build.version", consoleOutput.substring(consoleOutput.indexOf("Remote server version is:")+25, consoleOutput.indexOf("Login successful")).trim());
			isVersionSet = true;
			_logger.log(Level.INFO, "RHQ/JON Version: "+System.getProperty("rhq.build.version"));
		}
		
		_logger.log(Level.INFO, consoleOutput);
		if(makeFilure != null){
			cliTasks.validateErrorString(consoleOutput , makeFilure);
		}
		if(expectedResult != null){
			cliTasks.validateExpectedResultString(consoleOutput , expectedResult);
		}
		
	}	
	
	@AfterTest
	public void deleteJSFile(){
		try {
			CliTasks.getCliTasks().runCommnad("rm -rf '"+remoteFileLocation+jsFileName+"'", 1000*60*3);
		} catch (CliTasksException ex) {
			_logger.log(Level.WARNING, "Exception on remote File deletion!, ", ex);
		}
	}
	
}
