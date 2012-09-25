package com.redhat.qe.jon.clitest.tests.plugins.eap6.standalone;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.AS7CliTest;

public class DeploymentTest extends AS7CliTest {

	private final static String deploymentType = "Deployment";
	@BeforeClass
	public void beforeClass() {
		sshClient = sshStandalone;
	}
	
	
	@Test
	public void deployWAR() throws IOException, CliTasksException {		
		createDeployment("hello1.war", "hello.war","Creating Deployment");
		// TODO validate deployment on EAP server
	}
	@Test(dependsOnMethods={"deployWAR"})
	public void retrieveBackingContentForDeployed() throws IOException, CliTasksException {
		retrieveBackingContent("hello1.war", "hello.war", null);
	}
	
	@Test(dependsOnMethods={"deployWAR"},priority=1) // giving higher priority value means that test runs later (priority is lower)
	public void redeployWAR() throws IOException, CliTasksException {		
		createDeployment("hello2.war", "hello.war","Updating backing content");
	}
	@Test(dependsOnMethods={"redeployWAR"})
	public void retrieveBackingContentForRedeployed() throws IOException, CliTasksException {
		retrieveBackingContent("hello2.war", "hello.war", null);
	}
	
	@Test(alwaysRun=true,dependsOnMethods={"deployWAR","redeployWAR"},priority=100)
	public void undeployWAR() throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/undeploymentTest.js", "--args-style=named deployment=/tmp/hello.war", null, null,"rhqapi.js,eap6/standalone/server.js",null,null);
	}
	
	private void retrieveBackingContent(String srcWar, String destWar, String expected) throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/retrieveBackingContentTest.js", "--args-style=named type="+deploymentType+" deployment=/tmp/"+destWar, expected, null,"rhqapi.js,eap6/standalone/server.js","/deployments/"+srcWar,"/tmp/"+destWar);
	}
	
	private void createDeployment(String srcWar, String destWar, String expected) throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/deploymentTest.js", "--args-style=named type="+deploymentType+" deployment=/tmp/"+destWar, expected, null,"rhqapi.js,eap6/standalone/server.js","/deployments/"+srcWar,"/tmp/"+destWar);
		
	}
}
