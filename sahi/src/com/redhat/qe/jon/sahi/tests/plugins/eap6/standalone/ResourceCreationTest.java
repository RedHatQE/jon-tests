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
public class ResourceCreationTest extends AS7PluginSahiTestScript {

    // this address will be set in the connection settings, should be any random address that isn't running any AS instance :)
    private static final String IP_ADDR = "239.12.33.74";

    @BeforeClass(groups = "resourceCreation001")
    protected void setupAS7Plugin() {
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }

    @Test(groups = "resourceCreation001", alwaysRun=true)
    public void checkPersistenceOfChanges() {
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        as7SahiTasks.navigate(Navigate.AS_INVENTORY, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));

        sahiTasks.xy(sahiTasks.cell("Connection Settings"), 3, 3).click();


        ElementStub configuration_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Running configuration").parentNode("TR"));
        ElementStub startScript_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Start Script").parentNode("TR"));

        String old_configuration = configuration_element.getText();
        String old_startScript = startScript_element.getText();

        // set new values
        log.info("old configuration = " + old_configuration + ", will try to set to \"adfadjfadsf.xml\"");
        log.info("old start script = " + old_startScript + ", will try to set to \"abccbcblsd.sh\"");
        try {
            configuration_element.setValue("adfadjfadsf.xml");
            startScript_element.setValue("abccbcblsd.sh");
            sahiTasks.cell("Save").click();
        } finally {
            as7SahiTasks.navigate(Navigate.AS_INVENTORY, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));

            
            sahiTasks.xy(sahiTasks.cell("Connection Settings"), 3, 3).click();
            /*
            do {
                sahiTasks.cell("Connection Settings").mouseDown();
                sahiTasks.cell("Connection Settings").click();
                sahiTasks.cell("Connection Settings").doubleClick();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            } while (!((sahiTasks.textbox("textItem").in(sahiTasks.div("Running configuration").parentNode("TR"))).exists()));*/


            // check that the changes are persistent
            configuration_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Running configuration").parentNode("TR"));
            startScript_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Start Script").parentNode("TR"));
            String celm = configuration_element.getValue();
            String selm = startScript_element.getValue();

            // return the values back
            ElementStub configuration_element2 = sahiTasks.textbox("textItem").in(sahiTasks.div("Running configuration").parentNode("TR"));
            ElementStub startScript_element2 = sahiTasks.textbox("textItem").in(sahiTasks.div("Start Script").parentNode("TR"));
            configuration_element2.setValue(old_configuration);
            startScript_element2.setValue(old_startScript);
            sahiTasks.cell("Save").click();

            Assert.assertEquals(celm, "adfadjfadsf.xml", "Testing if changes to connection settings are persistent");
            Assert.assertEquals(selm, "abccbcblsd.sh", "Testing if changes to connection settings are persistent");
        }
    }

    @Test(groups = "resourceCreation001", alwaysRun=true)
    public void inputValidButIncorrectConnectionSettings() {
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        as7SahiTasks.navigate(Navigate.AS_INVENTORY, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));

        
        sahiTasks.xy(sahiTasks.cell("Connection Settings"), 3, 3).click();
        /*do {
            sahiTasks.cell("Connection Settings").mouseDown();
            sahiTasks.cell("Connection Settings").click();
            sahiTasks.cell("Connection Settings").doubleClick();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }
        } while (!((sahiTasks.textbox("textItem").in(sahiTasks.div("Running configuration").parentNode("TR"))).exists()));*/

        ElementStub hostname_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Hostname").parentNode("TR"));
        ElementStub port_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Port").parentNode("TR"));

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

            for (int i = 0; i < 12; i++) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    log.severe(ex.getMessage());
                }
                log.finer("Checking that resource went offline: try #" + Integer.toString(i + 1) + " of 12");
                if (!as7SahiTasks.checkIfResourceIsOnline(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"))) {
                    log.fine("Success - Resource went offline! Now I will change connection settings back to normal.");
                    ok = true;
                    break;
                }
            }
        } finally {
            // return the old values back
            as7SahiTasks.navigate(Navigate.AS_INVENTORY, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
            sahiTasks.xy(sahiTasks.cell("Connection Settings"), 3, 3).click();

            hostname_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Hostname").parentNode("TR"));
            port_element = sahiTasks.textbox("textItem").in(sahiTasks.div("Port").parentNode("TR"));

            hostname_element.setValue(old_hostname);
            port_element.setValue(old_port);
            sahiTasks.cell("Save").click();
            log.fine("Connection settings restored back to correct state");

            if (!ok) {
                Assert.fail("AS7's connection settings were changed to incorrect, but the AS didn't appear offline even after more than 6 minutes");
            }
        }

    }
}
