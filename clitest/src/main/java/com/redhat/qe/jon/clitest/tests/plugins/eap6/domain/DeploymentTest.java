package com.redhat.qe.jon.clitest.tests.plugins.eap6.domain;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.AS7CliTest;
import com.redhat.qe.tools.checklog.CheckLog;
import com.redhat.qe.tools.checklog.LogFile;

@CheckLog(
	enabled=true,
	logs={
		@LogFile(refId="server"),
		@LogFile(refId="agent")
	}
)
public class DeploymentTest extends AS7CliTest {

	private final static String deploymentType = "DomainDeployment";
	@BeforeClass
	public void beforeClass() {
		sshClient = sshStandalone;
	}
	/* creating deployment child on DC/HC resource and promoting such deployment to a server group*/
	@Test
	public void deployWAR() throws IOException, CliTasksException {		
		createDeployment("hello1.war", "hello.war","Creating");
		// TODO validate deployment on EAP server
	}
	@Test(dependsOnMethods={"deployWAR"})
	public void retrieveBackingContentForDeployed() throws IOException, CliTasksException {
		retrieveBackingContent("hello1.war", "hello.war", null);
	}
	
	@Test(dependsOnMethods={"deployWAR"},priority=1) // giving higher priority value means that test runs later (priority is lower)
	public void redeployWAR() throws IOException, CliTasksException {		
		createDeployment("hello2.war", "hello.war","Updating backing content");
		// TODO validate deployment on EAP server
	}
	@Test(dependsOnMethods={"redeployWAR"})
	public void retrieveBackingContentForRedeployed() throws IOException, CliTasksException {
		retrieveBackingContent("hello2.war", "hello.war", null);
	}
	@Test(dependsOnMethods={"deployWAR"},priority=2)
	public void deployToServerGroup() throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/domain/deployToServerGroupTest.js", "--args-style=named target=main-server-group deployment=/tmp/hello.war", null, null,"eap6/domain/server.js",null,null);
	}
	
	
	@Test(alwaysRun=true,dependsOnMethods={"deployWAR","redeployWAR"},priority=100)
	public void undeployWAR() throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/undeploymentTest.js", "--args-style=named deployment=/tmp/hello.war", null, null,"eap6/domain/server.js",null,null);
	}


	/* creating a deployment child on server group resource */
	
	@Test(dependsOnMethods={"undeployWAR"})
	public void deployWARAsNewServerGroupChild() throws IOException, CliTasksException {		
		createServerGroupDeployment("hello1.war", "hello.war","Creating");
	}
	@Test(dependsOnMethods={"deployWARAsNewServerGroupChild"})
	public void retrieveBackingContentForWARAsNewServerGroupChild() throws IOException, CliTasksException {
		retrieveBackingContentForServerGroupDeployment("hello1.war", "hello.war", null);
	}
	
	@Test(dependsOnMethods={"deployWARAsNewServerGroupChild"},priority=1)
	public void redeployWARAsNewServerGroupChild() throws IOException, CliTasksException {		
		createServerGroupDeployment("hello2.war", "hello.war","Updating backing content");
	}
	@Test(dependsOnMethods={"redeployWARAsNewServerGroupChild"},priority=1)
	public void retrieveBackingContentForRedeployedWARAsNewServerGroupChild() throws IOException, CliTasksException {
		retrieveBackingContentForServerGroupDeployment("hello2.war", "hello.war", null);
	}
	
	@Test(alwaysRun=true,dependsOnMethods={"deployWARAsNewServerGroupChild","redeployWARAsNewServerGroupChild"},priority=100)
	public void undeployWARAsNewServerGroupChild() throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/undeploymentTest.js", "--args-style=named child=main-server-group deployment=/tmp/hello.war", null, null,"eap6/domain/server.js",null,null);
	}
	
	
	
	private void retrieveBackingContent(String srcWar, String destWar, String expected) throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/retrieveBackingContentTest.js", "--args-style=named type="+deploymentType+" deployment=/tmp/"+destWar, expected, null,"eap6/domain/server.js","/deployments/"+srcWar,"/tmp/"+destWar);
	}
	
	private void createDeployment(String srcWar, String destWar, String expected) throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/deploymentTest.js", "--args-style=named type="+deploymentType+" deployment=/tmp/"+destWar, expected, null,"eap6/domain/server.js","/deployments/"+srcWar,"/tmp/"+destWar);		
	}
	
	private void createServerGroupDeployment(String srcWar, String destWar, String expected) throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/deploymentTest.js", "--args-style=named type=Deployment child=main-server-group deployment=/tmp/"+destWar, expected, null,"eap6/domain/server.js","/deployments/"+srcWar,"/tmp/"+destWar);		
	}
	
	private void retrieveBackingContentForServerGroupDeployment(String srcWar, String destWar, String expected) throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/retrieveBackingContentTest.js", "--args-style=named child=main-server-group type=Deployment deployment=/tmp/"+destWar, expected, null,"eap6/domain/server.js","/deployments/"+srcWar,"/tmp/"+destWar);
	}
	
}
