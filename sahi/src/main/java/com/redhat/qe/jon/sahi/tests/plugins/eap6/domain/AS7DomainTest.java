package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.annotations.BeforeClass;

import com.redhat.qe.jon.common.util.AS7DMRClient;
import com.redhat.qe.jon.common.util.AS7SSHClient;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import com.redhat.qe.tools.checklog.CheckLog;
import com.redhat.qe.tools.checklog.LogFile;

@CheckLog(
	enabled=false,
	logs={
		@LogFile(refId="agent"),
		@LogFile(refId="server"),
		@LogFile(refId="agent",id="domain",logFile="${as7.domain.home}/domain/log/host-controller.log")
	}
)
public class AS7DomainTest extends AS7PluginSahiTestScript {
	/**
	 * SSH Client for domain server
	 */
	 protected AS7SSHClient sshClient;
	 /**
	  * DMR Client for DomainController
	  */
	 protected AS7DMRClient mgmtClient;
	 /**
	  * AS7 Domain controller resource
	  */
	 protected Resource controller;
	 /**
	  * AS7 Host controller resource
	  */
	 protected Resource hostController;
	 /**
	  * managed server ONE resource
	  */
	 protected Resource serverOne;
	 /**
	  * managed server TWO resource
	  */
	 protected Resource serverTwo;
	 /**
	  * managed server THREE resource
	  */
	 protected Resource serverThree;
	 @BeforeClass(groups = "setup")
	 public void setup2() {
		 as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
		 sshClient = sshDomain;
		 mgmtClient = mgmtDomain;
		 controller = new Resource(sahiTasks, agentName, System.getProperty("as7.domain.controller.name", "EAP Domain Controller (0.0.0.0:8990)"));
		 hostController = controller.child(System.getProperty("as7.domain.host.name", "master"));
		 serverOne = controller.child(System.getProperty("as7.domain.host.server-one.name", "EAP server-one"));
		 serverTwo = controller.child(System.getProperty("as7.domain.host.server-two.name", "EAP server-two"));
		 serverThree = controller.child(System.getProperty("as7.domain.host.server-three.name", "EAP server-three"));
		 as7SahiTasks.installRHQUser(controller,sshClient,mgmtClient,"/domain/configuration/mgmt-users.properties");		 
	 }
}
