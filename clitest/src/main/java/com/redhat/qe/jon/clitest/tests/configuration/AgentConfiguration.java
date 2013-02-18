package com.redhat.qe.jon.clitest.tests.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.*;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import org.testng.annotations.Test;;


public class AgentConfiguration  extends ConfigurationCliTest {


		@BeforeClass()
		public void beforeClass() {
			// do nothing
		}
			
		@DataProvider
		public Object[][] createStartupConfigurations() {
			List<String> configs = new ArrayList<String>();
			
			configs.add("--args-style=named  prop=rhq.agent.agent-update.enabled  propType=bool propValue=true");
			configs.add("--args-style=named  prop=rhq.agent.client.max-concurrent propType=string propValue=7");
			configs.add("--args-style=named  prop=rhq.agent.client.max-retries  propType=string propValue=7"); 
			configs.add("--args-style=named  prop=rhq.communications.multicast-detector.enabled propType=bool propValue=true");
			configs.add("--args-style=named  prop=rhq.communications.multicast-detector.default-time-delay propType=string propValue=4888");
			configs.add("--args-style=named  prop=rhq.communications.multicast-detector.heartbeat-time-delay propType=string propValue=11111");
//			configs.add("--args-style=named  prop=rhq.communications.multicast-detector.multicast-address propType=string propValue=10.10.10.10");
			configs.add("--args-style=named  prop=rhq.communications.multicast-detector.port propType=string propValue=11111");
			configs.add("--args-style=named  prop=rhq.agent.client.queue-size propType=string propValue=25000");
			configs.add("--args-style=named  prop=rhq.agent.client.queue-throttling propType=string propValue=102:1002");
			configs.add("--args-style=named  prop=rhq.agent.client.retry-interval-msecs propType=string propValue=7777777");
			configs.add("--args-style=named  prop=rhq.agent.client.send-throttling propType=string propValue=102:1002");
			configs.add("--args-style=named  prop=rhq.agent.client.server-polling-interval-msecs propType=string propValue=48888");
			configs.add("--args-style=named  prop=rhq.agent.test-failover-list-at-startup propType=bool propValue=true");
			configs.add("--args-style=named  prop=rhq.agent.client.command-timeout-msecs propType=string propValue=7");
			configs.add("--args-style=named  prop=rhq.agent.client.command-spool-file.name propType=string propValue=command-spool-test.dat");
			configs.add("--args-style=named  prop=rhq.agent.client.command-spool-file.params propType=string propValue=10000099:75");
			configs.add("--args-style=named  prop=rhq.agent.client.command-spool-file.compressed propType=bool propValue=true");
			configs.add("--args-style=named  prop=rhq.agent.primary-server-switchover-check-interval-msecs propType=string propValue=3598765");
			configs.add("--args-style=named  prop=rhq.agent.register-with-server-at-startup propType=bool propValue=true");
			configs.add("--args-style=named  prop=rhq.communications.remote-stream-max-idle-time-msecs propType=string propValue=299988");
//			configs.add("--args-style=named  prop=rhq.agent.server.bind-address propType=string propValue=10.10.10.10");
			configs.add("--args-style=named  prop=rhq.agent.server.bind-port propType=string propValue=7080");
			configs.add("--args-style=named  prop=rhq.agent.server.transport-params propType=string propValue=/jboss-remoting-servlet-invoker/ServerInvokerServlet");
			configs.add("--args-style=named  prop=rhq.agent.server.transport  propType=string propValue=socket");
			configs.add("--args-style=named  prop=rhq.communications.connector.bind-port propType=string propValue=1234");
			configs.add("--args-style=named  prop=rhq.communications.connector.transport propType=string propValue=sslsocket");
			configs.add("--args-style=named  prop=rhq.agent.update-plugins-at-startup propType=bool propValue=true");
			configs.add("--args-style=named  prop=rhq.agent.vm-health-check.interval-msecs propType=string propValue=4888");
			configs.add("--args-style=named  prop=rhq.agent.vm-health-check.low-heap-mem-threshold propType=string propValue=0.5");
			configs.add("--args-style=named  prop=rhq.agent.vm-health-check.low-nonheap-mem-threshold propType=string propValue=0.5");
			configs.add("--args-style=named  prop=rhq.agent.wait-for-server-at-startup-msecs propType=string propValue=59876");
				
				
			Object[][] output = new Object[configs.size()][];
			for (int i=0;i<configs.size();i++) {
				output[i] = new Object[] {configs.get(i)};
			}		
			return output;
		}

		@Test(
			dataProvider="createStartupConfigurations",
			description="This test puts configuration prperty names and values into admin configuration"
		)
		public void updateAgentConfigurationTest(String config) throws IOException, CliTasksException {
		updateAgentConfiguration(config);
		}
		@AfterClass
		public void teardown() {
			// we start AS with all defaults (as it was before)
			log.info("Starting server with default (none) parameters");
		//	sshClient.restart("domain.sh");
		}
	}


