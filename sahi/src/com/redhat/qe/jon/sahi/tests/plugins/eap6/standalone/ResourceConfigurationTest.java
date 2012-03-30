package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 *
 * @author Jan Martiska (jmartisk@redhat.com)
 * @see TCMS test case 96428, 96429
 * @since 7 September 2011
 * 
 */
public class ResourceConfigurationTest extends AS7StandaloneTest {
    
    @BeforeClass(groups={"configuration","inventoryTest"})
    protected void setupAS7Plugin() {        
        as7SahiTasks.importResource(server);        
        log.finer("Waiting "+Timing.toString(Timing.TIME_30S)+" till server is properly inventorized");
        sahiTasks.waitFor(Timing.TIME_30S);
        Assert.assertTrue(server.isAvailable(), 
                "Resource " + server.toString() + " should be ONLINE, but I could not verify this!");
    }
    
    /**
     * Tries to inventorize a standalone AS7 instance and then verifies that it appears in the inventory of the agent.
     * @see TCMS test case 96428
     */
    @Test(groups={"inventoryTest"})
    public void inventoryTest() {
        server.assertExists(true);
    }
    
    /**
     * @see TCMS testcase 96429
     */
    @Test(groups={"inventoryTest"}, dependsOnMethods={"inventoryTest"})
    public void predefinedMetricsTest() {               
        server.monitoring();
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();

        String[] predefinedMetrics = {
            "Maximum request time",
            //"Number of management requests",
            "Number of management requests per Minute",
            //"Time used for management requests",
            "Time used for management requests per Minute"
        };
        for(String s:predefinedMetrics) {
            log.finer("Check that predefined metrics exist: " + s);
            Assert.assertTrue(sahiTasks.cell(s).exists(), "Check that predefined metric exists: "+s);        
        }
    }   
}