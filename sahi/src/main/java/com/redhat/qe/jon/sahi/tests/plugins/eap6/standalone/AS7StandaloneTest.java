package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;

import com.redhat.qe.jon.common.util.AS7DMRClient;
import com.redhat.qe.jon.common.util.AS7SSHClient;
import com.redhat.qe.jon.common.util.HTTPClient;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import com.redhat.qe.tools.checklog.CheckLog;
import com.redhat.qe.tools.checklog.LogFile;

/**
 * base class for all AS7 Standalone tests
 * @author lzoubek
 *
 */
@CheckLog(
	enabled=false,
	logs={
		@LogFile(refId="agent"),
		@LogFile(refId="server"),
		@LogFile(refId="agent",logFile="${as7.domain.home}/standalone/log/server.log")
	}
)
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
		 server = new Resource(sahiTasks, agentName, System.getProperty("as7.standalone.name","EAP (0.0.0.0:9990)"));
		 server2 = new Resource(sahiTasks, agentName, System.getProperty("as7.standalone2.name","EAP (0.0.0.0:19990)"));
		 as7SahiTasks.installRHQUser(server,sshClient,mgmtClient,"/standalone/configuration/mgmt-users.properties");		 
	 }
	 

}
