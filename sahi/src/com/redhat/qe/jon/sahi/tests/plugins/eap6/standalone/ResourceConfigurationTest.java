package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Jan Martiska (jmartisk@redhat.com)
 * @see TCMS test case 96428, 96429
 * @since 7 September 2011
 * 
 */
public class ResourceConfigurationTest extends AS7PluginSahiTestScript {
    
    @BeforeClass(groups="inventoryTest")
    protected void setupAS7Plugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
    
    /**
     * Tries to inventorize a standalone AS7 instance and then verifies that it appears in the inventory of the agent.
     * @see TCMS test case 96428
     */
    @Test(groups={"inventoryTest"})
    public void inventoryTest() {
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));        
        as7SahiTasks.assertResourceExistsInInventory(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));        
    }
    
    /**
     * @see TCMS testcase 96429
     */
    @Test(groups={"inventoryTest"})
    public void predefinedMetricsTest() {
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));        
        as7SahiTasks.navigate(AS7PluginSahiTasks.Navigate.AGENT_MONITORING, System.getProperty("agent.name"), null);
        sahiTasks.cell("Schedules").click();
        String[] predefinedMetrics = {
            "Architecture", 
            "Distribution Name",
            "Distribution Version",
            "Free Memory",
            "Free Swap Space",
            "Hostname",
            "Idle",
            "OS Name",
            "OS Version",
            "System Load",
            "Total Memory",
            "Total Swap Space",
            "Used Memory",
            "Used Swap Space",
            "User Load",
            "Wait Load"
        };
        for(String s:predefinedMetrics) {
            log.finest("Check that predefined metrics exist: " + s);
            Assert.assertTrue(sahiTasks.cell(s).exists(), "Check that predefined metric exists: "+s);        
        }
    }
    
    
    
    
}