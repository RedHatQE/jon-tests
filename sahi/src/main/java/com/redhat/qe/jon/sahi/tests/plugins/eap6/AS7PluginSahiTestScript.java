package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.common.util.AS7DMRClient;
import com.redhat.qe.jon.common.util.AS7SSHClient;
import com.redhat.qe.jon.common.util.HTTPClient;
import com.redhat.qe.jon.common.util.RestClient;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.tools.checklog.CheckLog;
import com.redhat.qe.tools.checklog.LogFile;

/**
 * @author jmartisk, lzoubek
 */

@CheckLog(enabled=false,
	logs={
		@LogFile(id="agent",user="${jon.agent.user}",pass="${jon.agent.password}",host="${jon.agent.host}",logFile="rhq-agent/logs/agent.log"),
		@LogFile(id="server",user="${jon.server.user}",pass="${jon.server.password}",host="${jon.server.host}",logFile="${jon.server.home}/logs/rhq-server-log4j.log"),
	}	
)
public class AS7PluginSahiTestScript extends SahiTestScript {

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTestScript.class.getName());

    protected static String agentName;
    protected AS7PluginSahiTasks as7SahiTasks;
    private static int MGMT_PORT_STANDALONE;
    private static String MGMT_HOST_STANDALONE;
    private static int MGMT_PORT_STANDALONE2;
    private static String MGMT_HOST_STANDALONE2;
    private static int MGMT_PORT_DOMAIN;
    private static String MGMT_HOST_DOMAIN;

    /**
     * AS7 management API client for standalone instance
     */
    protected static AS7DMRClient mgmtStandalone;
    /**
     * AS7 management API client for standalone instance  (2nd instance)
     */
    protected static AS7DMRClient mgmtStandalone2;
    /**
     * AS7 management API client for domain instance
     */
    protected static AS7DMRClient mgmtDomain;

    /**
     * SSH Client to to some stuff on machine where runs standalone AS7
     */
    protected static AS7SSHClient sshStandalone;
    
    /**
     * SSH Client to to some stuff on machine where runs standalone AS7 (2nd instance)
     */
    protected static AS7SSHClient sshStandalone2;
    
    
    /**
     * SSH Client to to some stuff on machine where runs domain AS7
     */
    protected static AS7SSHClient sshDomain;
    /**
     * HTTP Client for standalone instance 
     */
    protected static HTTPClient httpStandalone;
    /**
     * HTTP Client for standalone instance (2nd instance)
     */
    protected static HTTPClient httpStandalone2;
    protected static HTTPClient httpDomainManager;
    protected static HTTPClient httpDomainOne;
    protected static HTTPClient httpDomainTwo;
    protected static HTTPClient httpDomainThree;

 //   private RemoteLogAccess remoteLogAccess;
    public AS7PluginSahiTestScript() {
        super();
    }

    @BeforeSuite(groups = "setup", dependsOnMethods = {"openBrowser"})
    public void setup1() {
        try {
            System.getProperties().load(new FileInputStream(new File(System.getProperty("eap6plugin.configfile"))));
        } catch (Exception e) {
            try {
                System.getProperties().load(new FileInputStream(new File("config/eap6plugin.properties")));
            } catch (Exception ex) {
                try {
                    System.getProperties().load(new FileInputStream(new File("automatjon/jon/sahi/config/eap6plugin.properties")));
                } catch (Exception exc) {
                    log.severe("Could not load properties file for EAP6plugin testing: " + exc.getMessage() + " please provide the full path in system property \"eap6plugin.configfile\".");
                }
            }

        }

        checkRequiredProperties(
		"jon.agent.name","jon.agent.user","jon.agent.host","jon.agent.password",
		"as7.standalone.home","as7.domain.home",
		"jon.server.host","jon.server.user","jon.server.password"
	);
        agentName = System.getProperty("jon.agent.name");
        
        String agentHost = System.getProperty("jon.agent.host", "localhost");
        String agentUser = System.getProperty("jon.agent.user", "hudson");
        String agentPass = System.getProperty("jon.agent.password", "hudson");
        
        String agent2Host = System.getProperty("jon.agent2.host", "localhost");
        String agent2User = System.getProperty("jon.agent2.user", "hudson");
        String agent2Pass = System.getProperty("jon.agent2.password", "hudson");
        
        MGMT_PORT_STANDALONE = 9999;
        MGMT_HOST_STANDALONE = agentHost;
        MGMT_PORT_STANDALONE2 = 9999;
        MGMT_HOST_STANDALONE2 = agent2Host;
        MGMT_PORT_DOMAIN = 8999;
        MGMT_HOST_DOMAIN = agentHost;

        
        
        mgmtStandalone = new AS7DMRClient(MGMT_HOST_STANDALONE, MGMT_PORT_STANDALONE);
        mgmtStandalone2 = new AS7DMRClient(MGMT_HOST_STANDALONE2, MGMT_PORT_STANDALONE2);
        mgmtDomain = new AS7DMRClient(MGMT_HOST_DOMAIN, MGMT_PORT_DOMAIN);

        sshStandalone = new AS7SSHClient(System.getProperty("as7.standalone.home"),agentUser,agentHost,agentPass);
        sshStandalone2 = new AS7SSHClient(System.getProperty("as7.standalone2.home"),agent2User,agent2Host,agent2Pass);
        sshDomain = new AS7SSHClient(System.getProperty("as7.domain.home"));
        
        httpStandalone = new HTTPClient(agentHost, 8080);
        httpStandalone2 = new HTTPClient(agent2Host, 8080);
        httpDomainManager = new HTTPClient(agentHost, 8990);
        httpDomainOne = new HTTPClient(agentHost, 8130);
        httpDomainTwo = new HTTPClient(agentHost, 8230);
        httpDomainThree = new HTTPClient(agentHost, 8330);
        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
	if (System.getProperty("jon.server.home","").equals("")) {
	    log.fine("Auto-detecting [jon.server.home]");
	    String value = RestClient.detectServerInstallDir();
	    log.fine("[jon.server.home] detected : "+value);
	    System.setProperty("jon.server.home", value);
	}
	new Resource(sahiTasks,agentName).importFromDiscoQueue(5000);
    }

    @AfterSuite(groups="teardown")
    public void clientsCleanup() {
		if (mgmtDomain!=null) {
			mgmtDomain.close();
		}
		if (mgmtStandalone!=null) {
			mgmtStandalone.close();
		}
    	
    	if (sshDomain!=null)
    		sshDomain.disconnect();
    	if (sshStandalone!=null)
    		sshStandalone.disconnect();
    } 
}
