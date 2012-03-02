package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk & lzoubek
 */
public class ResourceConfigurationTest extends AS7DomainTest {
    
      
    
    @BeforeClass(groups="resourceConfiguration")
    protected void setupEapPlugin() {               
        as7SahiTasks.importResource(controller);
    }
     
    @Test(groups="resourceConfiguration")
    public void inventoryTest() {        
        controller.assertExists(true);
    }
    
    @Test(groups={"resourceConfiguration"})
    public void predefinedMetricsOfHostControllerTest() {
    	controller.monitoring();
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();
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
    
    @Test(groups={"resourceConfiguration"})
    public void predefinedMetricsOfManagedInstancesTest() {                         
        serverOne.monitoring();
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();
        String[] predefinedMetrics = {
            "Server state"
        };
        for(String s:predefinedMetrics) {
            log.finer("Check that predefined metrics exist: " + s);
            Assert.assertTrue(sahiTasks.cell(s).exists(), "Check that predefined metric exists: "+s);        
        }
    }
    
}
