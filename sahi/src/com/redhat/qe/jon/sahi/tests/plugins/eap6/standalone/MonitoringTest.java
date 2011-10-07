package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

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
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));        
    }
    
    @Test(groups={"monitoringTest"})
    public void disableMetricsTest()  {        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));               
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();        
        // step1: disable one of the metrics 
        sahiTasks.xy(sahiTasks.cell("Maximum request time"), 3, 3).click();
        try {
            Thread.sleep(2000);
        } catch(InterruptedException ex) {}
        sahiTasks.cell("Disable").click();      
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {}
        
        // step2: check that the metric is not present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {}
        org.testng.Assert.assertFalse(sahiTasks.cell("Maximum request time").exists());
                           
        // step3: enable the metrics back
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();          
        sahiTasks.xy(sahiTasks.cell("Maximum request time"), 3, 3).click();
        sahiTasks.cell("Enable").click();   
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {}
    }
    
    @Test(groups={"monitoringTest"}, dependsOnMethods={"disableMetricsTest"})
    public void enableMetricsTest()  {        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));                       
        // check that the metric is present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {}
        org.testng.Assert.assertTrue(sahiTasks.cell("Maximum request time").exists());                              
    }
    
    @Test(groups={"monitoringTest"})
    public void specifyMonitoringIntervalTest() {        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));               
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();                
        sahiTasks.xy(sahiTasks.cell("Maximum request time"), 3, 3).click();  
        
        ElementStub textbox = sahiTasks.textbox("textItem").in(sahiTasks.label("Collection Interval").parentNode("TR"));
        textbox.setValue("4");        
        sahiTasks.cell("Set").click();

        log.fine(sahiTasks.cell(3).in(sahiTasks.cell("Maximum request time").parentNode("TR")).fetch("innerHTML"));
        log.fine(sahiTasks.cell(4).in(sahiTasks.cell("Maximum request time").parentNode("TR")).fetch("innerHTML"));        
        org.testng.Assert.assertTrue(sahiTasks.cell(4).in(sahiTasks.cell("Maximum request time").parentNode("TR")).getText().indexOf("4") != -1);        
        
    }
    
    
}
