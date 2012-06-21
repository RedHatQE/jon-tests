package com.redhat.qe.jon.clitest.tests.plugins.eap6.standalone;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.AS7CliTest;

public class DeploymentTest extends AS7CliTest {

	@BeforeClass
	public void beforeClass() {
		sshClient = sshStandalone;
	}
	
	@Test
	public void deployWAR() throws IOException, CliTasksException {		
		createDeployment("hello1.war", "hello.war","Creating new deployment resource");
		// TODO validate deployment on EAP server
	}
	@Test(dependsOnMethods={"deployWAR"})
	public void redeployWAR() throws IOException, CliTasksException {		
		createDeployment("hello2.war", "hello.war","Updating backing content");
		// TODO validate deployment on EAP server
	}
	
	@Test(alwaysRun=true,dependsOnMethods={"deployWAR","redeployWAR"})
	public void undeployWAR() {
		
	}
	
	private void createDeployment(String srcWar, String destWar, String expected) throws IOException, CliTasksException {
		String warFilePath = AS7CliTest.class.getResource("/resources/deployments/"+srcWar).getPath();
		CliTasks.getCliTasks().copyFile(warFilePath, "/tmp/",destWar);

		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/deploymentTest.js", "--args-style=named agent="+agentName+" serverType=\"JBossAS7 Standalone Server\" pluginName=JBossAS7 depFile=/tmp/"+destWar+" depType=Deployment", expected, null,null,null,null);
		
	}
}
