package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.AS7SSHClient;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.HTTPClient;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.ManagementClient;
import com.redhat.qe.tools.remotelog.CheckRemoteLog;
import com.redhat.qe.tools.remotelog.RemoteLog;

/**
 * @author jmartisk, lzoubek
 */
//temporary disabled until https://bugzilla.redhat.com/show_bug.cgi?id=814243 is fixed
//@CheckRemoteLog(logs=@RemoteLog(logFile="rhq-agent/logs/agent.log",host="${env:HOST_NAME}",user="${env:HOST_USER}",pass="${env:HOST_PASSWORD}"))
public class AS7PluginSahiTestScript extends SahiTestScript {

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTestScript.class.getName());

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
    protected static ManagementClient mgmtStandalone;
    /**
     * AS7 management API client for standalone instance  (2nd instance)
     */
    protected static ManagementClient mgmtStandalone2;
    /**
     * AS7 management API client for domain instance
     */
    protected static ManagementClient mgmtDomain;

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

        // ********************************************
        // MANAGEMENT INTERFACE INITIALIZATION ********
        // ********************************************
        MGMT_PORT_STANDALONE = Integer.parseInt(System.getProperty("as7.standalone1.port", "9999"));
        MGMT_HOST_STANDALONE = System.getProperty("as7.standalone1.hostname", "localhost");
        MGMT_PORT_STANDALONE2 = Integer.parseInt(System.getProperty("as7.standalone2.port", "9999"));
        MGMT_HOST_STANDALONE2 = System.getProperty("as7.standalone2.hostname", "localhost");
        MGMT_PORT_DOMAIN = Integer.parseInt(System.getProperty("as7.domain.port", "9999"));
        MGMT_HOST_DOMAIN = System.getProperty("as7.domain.hostname", "localhost");

        mgmtStandalone = new ManagementClient(MGMT_HOST_STANDALONE, MGMT_PORT_STANDALONE);
        mgmtStandalone2 = new ManagementClient(MGMT_HOST_STANDALONE2, MGMT_PORT_STANDALONE2);
        mgmtDomain = new ManagementClient(MGMT_HOST_DOMAIN, MGMT_PORT_DOMAIN);

        sshStandalone = new AS7SSHClient(
        		System.getProperty("as7.standalone1.home"));
        sshStandalone2 = new AS7SSHClient(
        		System.getProperty("as7.standalone2.home"));
        sshDomain = new AS7SSHClient(
        		System.getProperty("as7.domain.home"));
        httpStandalone = new HTTPClient(System.getProperty("as7.standalone1.hostname"), Integer.parseInt(System.getProperty("as7.standalone1.http.port")));
        httpStandalone2 = new HTTPClient(System.getProperty("as7.standalone2.hostname"), Integer.parseInt(System.getProperty("as7.standalone2.http.port")));
        httpDomainManager = new HTTPClient(System.getProperty("as7.domain.hostname"), Integer.parseInt(System.getProperty("as7.domain.http.port")));
        httpDomainOne = new HTTPClient(System.getProperty("as7.domain.hostname"), Integer.parseInt(System.getProperty("as7.domain.host.server-one.http.port")));
        httpDomainTwo = new HTTPClient(System.getProperty("as7.domain.hostname"), Integer.parseInt(System.getProperty("as7.domain.host.server-two.http.port")));
        httpDomainThree = new HTTPClient(System.getProperty("as7.domain.hostname"), Integer.parseInt(System.getProperty("as7.domain.host.server-three.http.port")));
        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        // should we include this or not? it uninventorizes all EAP-instance resources from the agent before the testing starts..
    /*    as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-one.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-two.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-three.name"));
    */}

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
