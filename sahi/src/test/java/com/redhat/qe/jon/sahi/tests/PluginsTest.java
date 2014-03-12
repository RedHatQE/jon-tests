package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.redhat.qe.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;





public class PluginsTest extends SahiTestScript {

	//exact list of req'd plugins being provided by development 

	//*************************************
	//* Agent Plugins validation
	//*************************************
	@Test (groups="PluginsTest", dataProvider="agentPluginsData")
	public void validateAgentPlugins(String agentPluginsName, boolean pageRedirection){
		Assert.assertTrue(sahiTasks.getAgentServerPluginsStaus(agentPluginsName, pageRedirection, true), "Agent Plugins '"+agentPluginsName+"' status");
	}

	//*************************************
	//* Server Plugins validation
	//*************************************
	@Test (groups="PluginsTest", dataProvider="serverPluginsData")
	public void validateServerPlugins(String serverPluginsName, boolean pageRedirection){
		Assert.assertTrue(sahiTasks.getAgentServerPluginsStaus(serverPluginsName, pageRedirection, false), "Server Plugins '"+serverPluginsName+"' status");
	}

	@DataProvider(name="agentPluginsData")
	public Object[][] getAgentPluginsData() {
		ArrayList<List<Object>> agentPluginsdata = new ArrayList<List<Object>>();
		agentPluginsdata.add(Arrays.asList(new Object[]{"Abstract Augeas Plugin", true}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Abstract Database", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Ant Bundle Plugin", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Apache HTTP Server", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Cassandra", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Generic JMX", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Hibernate Services", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"IIS", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"JBoss Application Server 3.x/4.x", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"JBoss Application Server 5.x/6.x", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"JBoss Application Server 7.x", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"JBossCache 2.x Services", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"JBossCache 3.x Services", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Operating System Platforms", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"RHQ Agent", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"RHQ Server", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Script", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"Tomcat Server", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"JBoss Application Server", false}));
		agentPluginsdata.add(Arrays.asList(new Object[]{"JBoss Application Server 5.x", false}));

		if(this.getTestNgDataProviderGroups().contains(TESTNG_DATA_PROVIDER_GROUPS.JON)){
			agentPluginsdata.add(Arrays.asList(new Object[]{"Apache ActiveMQ", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Apache Camel", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Apache CXF", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Apache Servicemix3", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Apache Servicemix4", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Drools RHQ JMX Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"FuseSource Fabric", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"GateIn AS7 Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"GateIn Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"HornetQPlugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Infinispan Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"JBoss Data Grid Server 6.2.x", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"JBoss Fuse 6.x", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"JBossESB", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"JBossESB5", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Kie RHQ JMX Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"ModeShape", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"ModeShape Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"SwitchYard", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Teiid Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Teiid Plugin[1]", false}));
		}

		if(this.getTestNgDataProviderGroups().contains(TESTNG_DATA_PROVIDER_GROUPS.RHQ)){
			agentPluginsdata.add(Arrays.asList(new Object[]{"Abstract NO-OP plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Aliases", false}));				
			agentPluginsdata.add(Arrays.asList(new Object[]{"Cobbler", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Cron", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"File Template Bundle Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"GRUB Boot Loader", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Hosts", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Hudson", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"mod_cluster", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"MySql Database", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Network Services", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"OpenSSH", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Oracle Database", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Performance Test Plugin", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Postfix", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"PostgreSQL Database", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Receiver for SNMP Traps", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Samba", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Sudo Access", false}));
			agentPluginsdata.add(Arrays.asList(new Object[]{"Twitter Plugin", false}));
		}
		return TestNGUtils.convertListOfListsTo2dArray(agentPluginsdata);
	}

	@DataProvider(name="serverPluginsData")
	public Object[][] getServerPluginsData() {
		ArrayList<List<Object>> serverPluginsdata = new ArrayList<List<Object>>();
		serverPluginsdata.add(Arrays.asList(new Object[]{"Alert Definition Injection Plugin", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:CLI", true}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:Email", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:Operations", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:Roles", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:SNMP", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:Subject", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Ant Bundle Processor", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Disk Content", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"Drift:JPA (RHQ default)", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"PackageType:CLI", false}));
		serverPluginsdata.add(Arrays.asList(new Object[]{"URL Content", false}));

		if(this.getTestNgDataProviderGroups().contains(TESTNG_DATA_PROVIDER_GROUPS.JON)){
			serverPluginsdata.add(Arrays.asList(new Object[]{"Fuse Fabric Groups Plugin", false}));
			serverPluginsdata.add(Arrays.asList(new Object[]{"JBoss CSP Content", false}));
		}

		if(this.getTestNgDataProviderGroups().contains(TESTNG_DATA_PROVIDER_GROUPS.RHQ)){
			serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:IRC", false}));
			serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:Microblog", false}));
			serverPluginsdata.add(Arrays.asList(new Object[]{"Alert:Mobicents", false}));
			serverPluginsdata.add(Arrays.asList(new Object[]{"File Template Bundle Processor", false}));
			serverPluginsdata.add(Arrays.asList(new Object[]{"Yum Content", false}));
		}
		return TestNGUtils.convertListOfListsTo2dArray(serverPluginsdata);
	}
}





