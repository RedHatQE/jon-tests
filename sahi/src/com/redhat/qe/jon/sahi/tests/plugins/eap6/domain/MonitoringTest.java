package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks.Navigate;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import net.sf.sahi.client.ElementStub;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk
 */
public class MonitoringTest extends AS7PluginSahiTestScript {
    
    @BeforeClass(groups="monitoringTest")
    protected void setupAS7Plugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));        
    }
    
    private ElementStub getMetricsCell() {
    	return sahiTasks.cell("Maximum request time").in(sahiTasks.table("listTable[1]"));
    }  
    
    @Test(groups={"monitoringTest"})
    public void disableMetricsTest() {
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));
        sahiTasks.waitFor(5000);
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();
        // step1: disable one of the metrics
        sahiTasks.waitFor(5000);
        sahiTasks.xy(getMetricsCell(), 3, 3).click();
        sahiTasks.waitFor(2000);
        sahiTasks.cell("Disable").click();      
        sahiTasks.waitFor(2000);
        // step2: check that the metric is not present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 
        sahiTasks.waitFor(5000);
        org.testng.Assert.assertFalse(getMetricsCell().exists(),"Metrics 'Maximum request time' is not visible in results table");
                           
        // step3: enable the metrics back
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();

        sahiTasks.waitFor(5000);
        sahiTasks.xy(getMetricsCell(), 3, 3).click();
        sahiTasks.cell("Enable").click();  

        sahiTasks.waitFor(5000);
    }
    
    @Test(groups={"monitoringTest"})
    public void enableMetricsTest()  {        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));                       
        // check that the metric is present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 
        sahiTasks.waitFor(5000);
        org.testng.Assert.assertTrue(getMetricsCell().exists());                              
    }
    
    @Test(groups={"monitoringTest"})
    public void specifyMonitoringIntervalTest() {
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));
        sahiTasks.waitFor(5000);
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();
        sahiTasks.waitFor(5000);
        sahiTasks.xy(getMetricsCell(), 3, 3).click();  
        
        ElementStub textbox = sahiTasks.textbox("textItem").in(sahiTasks.label("Collection Interval").parentNode("TR"));
        textbox.setValue("4");        
        sahiTasks.cell("Set").click();

        org.testng.Assert.assertTrue(sahiTasks.cell(4).in(getMetricsCell().parentNode("TR")).getText().indexOf("4") != -1);        
    }
}
