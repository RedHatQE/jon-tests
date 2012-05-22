package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;

import com.redhat.qe.jon.common.util.AS7DMRClient;
import com.redhat.qe.jon.common.util.AS7SSHClient;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.HTTPClient;

/**
 * base class for all AS7 Standalone tests
 * @author lzoubek
 *
 */
public class AS7StandaloneTest extends AS7PluginSahiTestScript {

	 protected AS7SSHClient sshClient;
	 protected AS7DMRClient mgmtClient;
	 protected HTTPClient httpClient;
	 /**
	  * AS7 Standalone server resource
	  */
	 protected Resource server;
	 /**
	  * AS7 Standalone server resource (2nd instance)
	  */
	 protected Resource server2;
	 @BeforeClass(groups = "setup")
	 public void setup2() {
		 as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
		 sshClient = sshStandalone;
		 mgmtClient = mgmtStandalone;
		 httpClient = httpStandalone;
		 server = new Resource(sahiTasks, System.getProperty("agent.name"), System.getProperty("as7.standalone1.name"));
		 server2 = new Resource(sahiTasks, System.getProperty("agent.name"), System.getProperty("as7.standalone2.name"));
		 as7SahiTasks.installRHQUser(server,sshClient,mgmtClient,"/standalone/configuration/mgmt-users.properties");
	 }
	 

}
