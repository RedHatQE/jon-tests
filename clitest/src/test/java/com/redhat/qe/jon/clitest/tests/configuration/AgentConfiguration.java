package com.redhat.qe.jon.clitest.tests.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import org.testng.annotations.Test;;


public class AgentConfiguration  extends ConfigurationCliTest {

			
		@DataProvider
		public Object[][] createStartupConfigurations() {
			List<ConfigValue> configs = new ArrayList<ConfigValue>();
			
			configs.add(new ConfigValue("rhq.agent.agent-update.enabled","bool","true"));
			configs.add(new ConfigValue("rhq.agent.client.max-concurrent","string","7"));
			configs.add(new ConfigValue("rhq.agent.client.max-retries","string","7")); 
			configs.add(new ConfigValue("rhq.communications.multicast-detector.enabled","bool","true"));
			configs.add(new ConfigValue("rhq.communications.multicast-detector.default-time-delay","string","4888"));
			configs.add(new ConfigValue("rhq.communications.multicast-detector.heartbeat-time-delay","string","11111"));
//			configs.add(new ConfigValue("rhq.communications.multicast-detector.multicast-address","string","10.10.10.10"));
			configs.add(new ConfigValue("rhq.communications.multicast-detector.port","string","11111"));
			configs.add(new ConfigValue("rhq.agent.client.queue-size","string","25000"));
			configs.add(new ConfigValue("rhq.agent.client.queue-throttling","string","102:1002"));
			configs.add(new ConfigValue("rhq.agent.client.retry-interval-msecs","string","7777777"));
			configs.add(new ConfigValue("rhq.agent.client.send-throttling","string","102:1002"));
			configs.add(new ConfigValue("rhq.agent.client.server-polling-interval-msecs","string","48888"));
			configs.add(new ConfigValue("rhq.agent.test-failover-list-at-startup","bool","true"));
			configs.add(new ConfigValue("rhq.agent.client.command-timeout-msecs","string","7"));
			configs.add(new ConfigValue("rhq.agent.client.command-spool-file.name","string","command-spool-test.dat"));
			configs.add(new ConfigValue("rhq.agent.client.command-spool-file.params","string","10000099:75"));
			configs.add(new ConfigValue("rhq.agent.client.command-spool-file.compressed","bool","true"));
			configs.add(new ConfigValue("rhq.agent.primary-server-switchover-check-interval-msecs","string","3598765"));
			configs.add(new ConfigValue("rhq.agent.register-with-server-at-startup","bool","true"));
			configs.add(new ConfigValue("rhq.communications.remote-stream-max-idle-time-msecs","string","299988"));
//			configs.add(new ConfigValue("rhq.agent.server.bind-address","string","10.10.10.10");
			configs.add(new ConfigValue("rhq.agent.server.bind-port","string","7080"));
			configs.add(new ConfigValue("rhq.agent.server.transport-params","string","/jboss-remoting-servlet-invoker/ServerInvokerServlet"));
			configs.add(new ConfigValue("rhq.agent.server.transport ","string","socket"));
			configs.add(new ConfigValue("rhq.communications.connector.bind-port","string","1234"));
			configs.add(new ConfigValue("rhq.communications.connector.transport","string","sslsocket"));
			configs.add(new ConfigValue("rhq.agent.update-plugins-at-startup","bool","true"));
			configs.add(new ConfigValue("rhq.agent.vm-health-check.interval-msecs","string","4888"));
			configs.add(new ConfigValue("rhq.agent.vm-health-check.low-heap-mem-threshold","string","0.5"));
			configs.add(new ConfigValue("rhq.agent.vm-health-check.low-nonheap-mem-threshold","string","0.5"));
			configs.add(new ConfigValue("rhq.agent.wait-for-server-at-startup-msecs","string","59876"));				
			return getDataProviderArray(configs);
		}

		@Test(
			dataProvider="createStartupConfigurations",
			description="This test puts configuration prperty names and values into admin configuration"
		)
		public void updateAgentConfigurationTest(ConfigValue config) throws IOException, CliTasksException {
		    updateAgentConfiguration(config);
		}
	}


