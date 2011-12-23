package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
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
    	try {
    		if (mgmtStandalone!=null)
    			mgmtStandalone.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
    		if (mgmtDomain!=null)
    			mgmtDomain.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if (sshDomain!=null)
    		sshDomain.disconnect();
    	if (sshStandalone!=null)
    		sshStandalone.disconnect();
    	
    }
}
