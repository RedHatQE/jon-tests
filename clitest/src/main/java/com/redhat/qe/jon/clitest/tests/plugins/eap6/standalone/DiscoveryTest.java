package com.redhat.qe.jon.clitest.tests.plugins.eap6.standalone;

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
		sshClient = sshStandalone;
	}
		
	@DataProvider
	public Object[][] createStartupConfigurations() {
		List<ServerStartConfig> configs = new ArrayList<ServerStartConfig>();
		// listen on all intefraces (currently default .. it is safe to keep this test as first)
		configs.add(new ServerStartConfig("standalone.sh", "hostname:'0.0.0.0'"));
		// listen on localhost
		configs.add(new ServerStartConfig("standalone.sh -bmanagement localhost", "hostname:'localhost'"));
		configs.add(new ServerStartConfig(
				"standalone.sh -c=standalone-full.xml",
				"hostXmlFileName:'standalone-full.xml'")
		);
		// listen on IPv6 all localhost
		configs.add(new ServerStartConfig("standalone.sh -Djava.net.preferIPv4Stack=false -bmanagement ::1", "hostname:'::1'"));
		// BZ 820570
		configs.add(new ServerStartConfig(
			"standalone.sh -P file://${HOME}/"+sshClient.getAsHome()+"/bin/props.properties",
			"port:'29990'",
			"echo \"jboss.management.http.port=29990\" > bin/props.properties")
		);		
		// listen on IPv6 all interfaces
		configs.add(new ServerStartConfig("standalone.sh -Djava.net.preferIPv4Stack=false -bmanagement ::", "hostname:'::'"));
		// listen on particular port
		configs.add(new ServerStartConfig("standalone.sh -Djboss.management.http.port=29990","port:'29990'"));
		// start with port offset we assume default http management port is 9990
		configs.add(new ServerStartConfig("standalone.sh -Djboss.socket.binding.port-offset=1000","port:'10990'"));
		configs.add(new ServerStartConfig(
				"standalone.sh --server-config=standalone-full.xml",
				"hostXmlFileName:'standalone-full.xml'")
		);
		// BZ 820570
		configs.add(new ServerStartConfig(
				"standalone.sh --properties file://${HOME}/"+sshClient.getAsHome()+"/props.properties",
			"port:'10990'",
			"echo \"jboss.socket.binding.port-offset=1000\" > props.properties")
		);		
		// override config dir
		configs.add(new ServerStartConfig(
				"standalone.sh -Djboss.server.config.dir=${HOME}/"+sshClient.getAsHome()+"/standalone/configuration2",
				"configuration2",
				"cp -a standalone/configuration standalone/configuration2")
		);
		// BZ 820570 using relative path
		configs.add(new ServerStartConfig(
			"standalone.sh -P=props.properties",
			"port:'10990'",
			"echo \"jboss.socket.binding.port-offset=1000\" > bin/props.properties")
		);
		configs.add(new ServerStartConfig(
				"standalone.sh -Djboss.server.default.config=standalone-full.xml",
				"hostXmlFileName:'standalone-full.xml'")
		);
		// override basedir
		configs.add(new ServerStartConfig(
				"standalone.sh -Djboss.server.base.dir=${HOME}/"+sshClient.getAsHome()+"/standalone2",
				"standalone2",
				"cp -a standalone standalone2")
		);
		// start in full profile - more ways doing it
		configs.add(new ServerStartConfig(
				"standalone.sh -c=standalone-full.xml",
				"hostXmlFileName:'standalone-full.xml'")
		);

		
		Object[][] output = new Object[configs.size()][];
		for (int i=0;i<configs.size();i++) {
			output[i] = new Object[] {configs.get(i)};
		}		
		return output;
	}

	@Test(
		dataProvider="createStartupConfigurations",
		description="This test starts up AS7 in standalone mode with particular configuration and runs eap6/discoveryTest.js to detect and import it"
	)
	public void serverStartupTest(ServerStartConfig start) throws IOException, CliTasksException {
		serverStartup(start,"standalone");
	}
	
	@AfterClass
	public void teardown() {
		// we start AS with all defaults (as it was before)
		log.info("Starting server with default (none) parameters");
		sshClient.restart("standalone.sh");
	}
}
