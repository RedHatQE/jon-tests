package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks.Navigate;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import net.sf.sahi.client.ElementStub;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk@redhat.com
 * @see TCMS testcase 96430
 */
public class ResourceCreation001Test extends AS7PluginSahiTestScript {

    // this address will be set in the connection settings, should be any random address that isn't running any AS instance :-)
    private static final String IP_ADDR = "239.12.33.74";

    @BeforeClass(groups = "resourceCreation001")
    protected void setupAS7Plugin() {
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
    
    @Test(groups = "resourceCreation001")
    public void checkPersistenceOfChanges() {
        
    }

    @Test(groups = "resourceCreation001")
    public void inputValidButIncorrectConnectionSettings() {
        as7SahiTasks.navigate(Navigate.AS_INVENTORY, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        sahiTasks.cell("Connection Settings").click();

        ElementStub hostname_element = sahiTasks.textbox(0).in(sahiTasks.row("Hostname"));
        ElementStub port_element = sahiTasks.textbox(0).in(sahiTasks.row("Port"));

        String old_hostname = hostname_element.getText();
        String old_port = port_element.getText();

        // set incorrect values
        log.info("old hostname = " + old_hostname + ", will try to set to " + IP_ADDR);
        log.info("old port = " + old_port + ", will try to set to " + Integer.toString(Integer.parseInt(old_port) + 1349));
        hostname_element.setValue(IP_ADDR);
        port_element.setValue(Integer.toString(Integer.parseInt(old_port) + 1349));
        sahiTasks.cell("Save").click();

        boolean ok = false;
        try {
            // the resource should go down after some time -- check for it

            for (int i = 0; i < 6; i++) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    log.severe(ex.getMessage());
                }
                log.finest("Checking that resource went offline: try #" + Integer.toString(i + 1) + " of 6");
                if (!as7SahiTasks.checkIfResourceIsOnline(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"))) {
                    log.fine("Success - Resource went offline! Now I will change connection settings back to normal.");
                    ok = true;
                    break;
                }
            }
        } finally {
            // return the old values back
            as7SahiTasks.navigate(Navigate.AS_INVENTORY, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
            sahiTasks.cell("Connection Settings").click();
            hostname_element.setValue(old_hostname);
            port_element.setValue(old_port);
            sahiTasks.cell("Save").click();
            log.fine("Connection settings restored back to correct state");

            if (!ok) {
                Assert.fail("AS7's connection settings were changed to incorrect, but the AS didn't appear offline even after more than 3 minutes");
            }
        }

    }
}
