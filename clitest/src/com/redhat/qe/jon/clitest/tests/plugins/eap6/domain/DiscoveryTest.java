package com.redhat.qe.jon.clitest.tests.plugins.eap6.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.AS7CliTest;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.ServerStartConfig;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.ServerStartConfig.ConfigFile;
import com.redhat.qe.jon.common.util.AS7SSHClient;

/**
 * this test starts up AS7 in standalone mode with different configurations (standalone.sh parameters)
 * and checks whether AS7 was detected correctly
 * @author lzoubek@redhat.com
 *
 */
public class DiscoveryTest extends AS7CliTest {

	
	AS7SSHClient sshClient;

	@BeforeClass()
	public void beforeClass() {
		sshClient = new AS7SSHClient(domainHome,"hudson",domainHostName,"hudson");
	}
		
	@DataProvider
	public Object[][] createStartupConfigurations() {
		List<ServerStartConfig> configs = new ArrayList<ServerStartConfig>();

		configs.add(new ServerStartConfig("domain.sh", "hostname=0.0.0.0"));

		configs.add(new ServerStartConfig(
				"domain.sh -Djboss.host.default.config=host2.xml",
				"hostXmlFileName=host2.xml",
				"cp domain/configuration/host.xml domain/configuration/host2.xml")
		);
		
		Object[][] output = new Object[configs.size()][];
		for (int i=0;i<configs.size();i++) {
			output[i] = new Object[] {configs.get(i)};
		}		
		return output;
	}

	@Test(
		dataProvider="createStartupConfigurations",
		description="This test starts up AS7 in Domain mode with particular configuration and runs eap6/domain/discoveryTest.js to detect and import it"
	)
	public void serverStartupTest(ServerStartConfig start) throws IOException, CliTasksException {
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
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		sshClient.runAndWait("netstat -pltn | grep java");
		runJSfile(null, "rhqadmin", "rhqadmin", "eap6/domain/discoveryTest.js", "--args-style=named agent="+agentName, start.getExpectedMessage(), null);
	}
	@AfterClass
	public void teardown() {
		// we start AS with all defaults (as it was before)
		log.info("Starting server with default (none) parameters");
		sshClient.restart("domain.sh");
	}
}
