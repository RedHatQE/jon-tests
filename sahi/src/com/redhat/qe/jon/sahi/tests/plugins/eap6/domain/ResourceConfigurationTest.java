package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import com.redhat.qe.jon.sahi.tasks.Navigator.InventoryNavigation;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks.Navigate;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk & lzoubek
 */
public class ResourceConfigurationTest extends AS7PluginSahiTestScript {
    
    private InventoryNavigation navController;       
    
    @BeforeClass(groups="resourceConfiguration")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        navController = new InventoryNavigation(System.getProperty("agent.name"),"Inventory",System.getProperty("as7.domain.controller.name"));
        
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name")); 
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.host.server-one.name"));      
    }
     
    @Test(groups="resourceConfiguration")
    public void inventoryTest() {        
        sahiTasks.assertResourceExists(true, navController);
    }
    
    @Test(groups={"resourceConfiguration"})
    public void predefinedMetricsOfHostControllerTest() {
        sahiTasks.getNavigator().inventoryGoToResource(navController);
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
        sahiTasks.getNavigator().inventoryGoToResource(navController.pathPush("as7.domain.host.server-one.name"));
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
