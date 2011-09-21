package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks.Navigate;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk
 */
public class ResourceConfigurationTest extends AS7PluginSahiTestScript {
    
    @BeforeClass(groups="resourceConfiguration")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
     
    @Test(groups="resourceConfiguration")
    public void inventoryTest() {
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));        
        as7SahiTasks.assertResourceExistsInInventory(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name")); 
    }
    
    @Test(groups={"resourceConfiguration"}, timeOut=180000)
    public void predefinedMetricsOfHostControllerTest() {
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));
        
        
        do {
            sahiTasks.cell("Schedules").mouseDown();
            sahiTasks.cell("Schedules").click();
            sahiTasks.cell("Schedules").doubleClick();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }                        
        } while (!((sahiTasks.cell("Maximum request time")).exists())); 

        String[] predefinedMetrics = {
            "Maximum request time",
            "Number of management requests",
            "Number of management requests per Minute",
            "Time used for management requests",
            "Time used for management requests per Minute"
        };
        for(String s:predefinedMetrics) {
            log.finer("Check that predefined metrics exist: " + s);
            Assert.assertTrue(sahiTasks.cell(s).exists(), "Check that predefined metric exists: "+s);        
        }
    }
    
    @Test(groups={"resourceConfiguration"}, timeOut=180000)
    public void predefinedMetricsOfManagedInstancesTest() {
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-one.name"));        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-one.name"));
        
        
        do {
            sahiTasks.cell("Schedules").mouseDown();
            sahiTasks.cell("Schedules").click();
            sahiTasks.cell("Schedules").doubleClick();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }                        
        } while (!((sahiTasks.cell("Server state")).exists())); 

        String[] predefinedMetrics = {
            "Server state"
        };
        for(String s:predefinedMetrics) {
            log.finer("Check that predefined metrics exist: " + s);
            Assert.assertTrue(sahiTasks.cell(s).exists(), "Check that predefined metric exists: "+s);        
        }
    }
    
}
