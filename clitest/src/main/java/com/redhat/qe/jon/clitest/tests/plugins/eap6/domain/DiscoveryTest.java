package com.redhat.qe.jon.clitest.tests.plugins.eap6.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.AS7CliTest;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.ServerStartConfig;

/**
 * this test starts up AS7 in standalone mode with different configurations (standalone.sh parameters)
 * and checks whether AS7 was detected correctly
 * @author lzoubek@redhat.com
 *
 */
public class DiscoveryTest extends AS7CliTest {


	@BeforeClass()
	public void beforeClass() {
		sshClient = sshDomain;
	}
		
	@DataProvider
	public Object[][] createStartupConfigurations() {
		List<ServerStartConfig> configs = new ArrayList<ServerStartConfig>();

		configs.add(new ServerStartConfig("domain.sh", "hostname:'0.0.0.0'"));

		configs.add(new ServerStartConfig(
				"domain.sh -Djboss.host.default.config=host2.xml",
				"hostXmlFileName:'host2.xml'",
				"cp domain/configuration/host.xml domain/configuration/host2.xml")
		);
		
		configs.add(new ServerStartConfig("domain.sh -bmanagement localhost", "hostname:'localhost'"));
		configs.add(new ServerStartConfig("domain.sh -Djboss.management.http.port=19998", "port:'19998'"));
		
		Object[][] output = new Object[configs.size()][];
		for (int i=0;i<configs.size();i++) {
			output[i] = new Object[] {configs.get(i)};
		}		
		return output;
	}

	@Test(
		dataProvider="createStartupConfigurations",
		description="This test starts up AS7 in Domain mode with particular configuration and runs eap6/discoveryTest.js to detect and import it"
	)
	public void serverStartupTest(ServerStartConfig start) throws IOException, CliTasksException {
		serverStartup(start,"domain");
	}
	@AfterClass
	public void teardown() {
		// we start AS with all defaults (as it was before)
		log.info("Starting server with default (none) parameters");
		sshClient.restart("domain.sh");
	}
}
