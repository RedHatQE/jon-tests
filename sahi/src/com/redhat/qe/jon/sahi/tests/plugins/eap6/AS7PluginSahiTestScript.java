package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.tasks.Navigator.InventoryNavigation;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.HTTPClient;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.ManagementClient;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.SSHClient;

/**
 * @author jmartisk, lzoubek
 */
public class AS7PluginSahiTestScript extends SahiTestScript {

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTestScript.class.getName());

    protected AS7PluginSahiTasks as7SahiTasks;
    private static int MGMT_PORT_STANDALONE;
    private static String MGMT_HOST_STANDALONE;
    private static int MGMT_PORT_DOMAIN;
    private static String MGMT_HOST_DOMAIN;

    /**
     * AS7 management API client for standalone instance
     */
    protected static ManagementClient mgmtStandalone;
    /**
     * AS7 management API client for domain instance
     */
    protected static ManagementClient mgmtDomain;

    /**
     * SSH Client to to some stuff on machine where runs standalone AS7
     */
    protected static SSHClient sshStandalone;
    
    /**
     * SSH Client to to some stuff on machine where runs domain AS7
     */
    protected static SSHClient sshDomain;
    /**
     * HTTP Client for standalone instance 
     */
    protected static HTTPClient httpStandalone;
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
        MGMT_PORT_STANDALONE = Integer.parseInt(System.getProperty("as7.standalone.port", "9999"));
        MGMT_HOST_STANDALONE = System.getProperty("as7.standalone.hostname", "localhost");
        MGMT_PORT_DOMAIN = Integer.parseInt(System.getProperty("as7.domain.port", "9999"));
        MGMT_HOST_DOMAIN = System.getProperty("as7.domain.hostname", "localhost");

        mgmtStandalone = new ManagementClient(MGMT_HOST_STANDALONE, MGMT_PORT_STANDALONE);
        mgmtDomain = new ManagementClient(MGMT_HOST_DOMAIN, MGMT_PORT_DOMAIN);

        String user = System.getProperty("as7.runs.as.user");
        String host = System.getProperty("as7.standalone.hostname");
        String key = System.getProperty("user.home")+"/"+System.getProperty("as7.key");
        sshStandalone = new SSHClient(
        		System.getProperty("as7.runs.as.user"),
        		System.getProperty("as7.standalone.hostname"),
        		System.getProperty("user.home")+"/"+System.getProperty("as7.key"),
        		System.getProperty("as7.standalone.home"));
        sshDomain = new SSHClient(
        		System.getProperty("as7.runs.as.user"),
        		System.getProperty("as7.domain.hostname"),
        		System.getProperty("user.home")+"/"+System.getProperty("as7.key"),
        		System.getProperty("as7.domain.home"));
        httpStandalone = new HTTPClient(System.getProperty("as7.standalone.hostname"), Integer.parseInt(System.getProperty("as7.standalone.http.port")));
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
    /**
     * asserts whether last operation on current resource (you have to be on resource page) 
     * defined by 'opName' was successful
     * @param opName
     */
    public void assertOperationSuccess(InventoryNavigation nav,String opName) {
    	assertOperationResult(nav, opName, true);
    }

    /**
     * asserts whether last operation on current resource (you have to be on resource page) 
     * defined by 'opName' resulted.
     * @param opName
     * @param success - true if you expect operation to succeed, false if you expect it to fail
     */
    public void assertOperationResult(InventoryNavigation nav,String opName, boolean success) {
    	log.fine("Asserting operation '"+opName+"' result...");
    	nav = nav.setInventoryTab("Summary");
    	sahiTasks.getNavigator().inventoryGoToResource(nav);
    	int timeout = 600*1000;
    	int time = 0;
    	while (time<timeout && sahiTasks.image("Operation_inprogress_16.png").in(sahiTasks.div(opName+"[0]").parentNode("tr")).exists()) {
    		time+=10*1000;
    		log.fine("Operation '"+opName+"' in progress, waiting 10s");
    		sahiTasks.waitFor(10*1000);
    		sahiTasks.getNavigator().inventoryGoToResource(nav);
    	}
    	String resultImage = "Operation_failed_16.png";
    	String succ="fail";
    	if (success) {
    		resultImage = "Operation_ok_16.png";
    		succ="success";
    	}
    	
    	Assert.assertTrue(sahiTasks.image(resultImage).in(sahiTasks.div(opName+"[0]").parentNode("tr")).exists(),opName+" operation result:"+succ);
    }
    /**
     * asserts whether last operation on current resource (you have to be on resource page) 
     * defined by 'opName' failed
     * @param opName
     */
    public void assertOperationFailure(InventoryNavigation nav,String opName) {
    	assertOperationResult(nav, opName, false);
    }
    
    
}
