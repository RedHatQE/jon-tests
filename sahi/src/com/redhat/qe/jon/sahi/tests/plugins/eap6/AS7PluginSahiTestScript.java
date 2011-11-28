package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.testng.annotations.BeforeSuite;
import com.redhat.qe.auto.testng.Assert;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * @author jmartisk
 */
public class AS7PluginSahiTestScript extends SahiTestScript {

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTestScript.class.getName());

    protected AS7PluginSahiTasks as7SahiTasks;
    private static int MGMT_PORT_STANDALONE;
    private static String MGMT_HOST_STANDALONE;
    private static int MGMT_PORT_DOMAIN;
    private static String MGMT_HOST_DOMAIN;

    protected static ModelControllerClient managementStandalone;
    protected static ModelControllerClient managementDomain;
    protected static ModelControllerClient managementCurrent;

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

        try {
            managementStandalone = ModelControllerClient.Factory.create(InetAddress.getByName(MGMT_HOST_STANDALONE), MGMT_PORT_STANDALONE);
            managementCurrent = managementStandalone;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create model controller client for host: " + MGMT_HOST_STANDALONE + " and port " + MGMT_PORT_STANDALONE, e);
        }
        try {
            managementDomain = ModelControllerClient.Factory.create(InetAddress.getByName(MGMT_HOST_DOMAIN), MGMT_PORT_DOMAIN);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create model controller client for host: " + MGMT_HOST_DOMAIN + " and port " + MGMT_PORT_DOMAIN, e);
        }


        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        // should we include this or not? it uninventorizes all EAP-instance resources from the agent before the testing starts..
    /*    as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-one.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-two.name"));
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-three.name"));
    */}

    /**
     * sets Domain as management client. Methods executeOperation* will contact domain EAP instance since this method is called
     */
    protected void setManagementControllerDomain() {
    	managementCurrent = managementDomain;
    }
    /**
     * sets STANDALONE as management client. Methods executeOperation* will contact standalone EAP instance since this method is called
     */
    protected void setManagementControllerStandalone() {
    	managementCurrent = managementStandalone;
    }
    /***********************************************************************/
    /******************** AUX FUNCTIONS FOR MANAGEMENT**********************/
    /***********************************************************************/

   protected ModelNode createOperation(String address, String operation, String... params) {
        ModelNode op = new ModelNode();
        String[] pathSegments = address.split("/");
        ModelNode list = op.get("address").setEmptyList();
        for (String segment : pathSegments) {
            String[] elements = segment.split("=");
            list.add(elements[0], elements[1]);
        }
        op.get("operation").set(operation);
        for (String param : params) {
            String[] elements = param.split("=");
            op.get(elements[0]).set(elements[1]);
        }
        return op;
    }

    protected ModelNode executeOperation(final ModelNode op) throws IOException {
        ModelNode ret = managementCurrent.execute(op);
        return ret;
    }

    protected ModelNode executeOperationAndAssertSuccess(String msg, final ModelNode op) throws IOException {
        ModelNode ret = managementCurrent.execute(op);
        Assert.assertTrue("success".equals(ret.get("outcome").asString()),
                msg + ret.get("failure-description").asString()
                );
        return ret;
    }

    protected ModelNode executeOperationAndAssertFailure(String msg, final ModelNode op) throws IOException {
        ModelNode ret = managementCurrent.execute(op);
        Assert.assertTrue("failed".equals(ret.get("outcome").asString()), msg);
        return ret;
    }



}
