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
 * @author jmartisk, lzoubek
 */
public class MonitoringTest extends AS7PluginSahiTestScript {
    
    @BeforeClass(groups="monitoringTest")
    protected void setupAS7Plugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));        
    }
    
    
    private ElementStub getMetricsCell() {
    	int tables = sahiTasks.table("listTable").countSimilar();
    	log.info("listTable count = "+tables);
    	return sahiTasks.cell("Maximum request time").in(sahiTasks.table("listTable["+(tables-1)+"]"));
    }    
    
    @Test(groups={"monitoringTest"})
    public void disableMetricsTest()  {        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));               
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();        
        // step1: disable one of the metrics         
        sahiTasks.waitFor(5000);
        sahiTasks.xy(getMetricsCell(),3,3).click();
        sahiTasks.waitFor(2000);
        sahiTasks.cell("Disable").click();      

        sahiTasks.waitFor(10000);
        // step2: check that the metric is not present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 

        sahiTasks.waitFor(5000);
        Assert.assertFalse(getMetricsCell().isVisible(),"Metrics 'Maximum request time' is not visible in results table");
    }

    
    @Test(groups={"monitoringTest"})
    public void enableMetricsTest()  {        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));                       
        // check that the metric is present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 
        sahiTasks.waitFor(5000);
        Assert.assertTrue(sahiTasks.cell("Maximum request time").exists());                              
    }

    @Test(groups={"monitoringTest"})
    public void specifyMonitoringIntervalTest() {        
        as7SahiTasks.navigate(Navigate.RESOURCE_MONITORING, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));               
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click(); 
        sahiTasks.waitFor(5000);
        sahiTasks.xy(getMetricsCell(),3,3).click();
        ElementStub textbox = sahiTasks.textbox("interval");
        textbox.setValue("4");
        sahiTasks.waitFor(5000);
        log.info(sahiTasks.cell("Set").parentNode().fetch("innerHTML"));

        sahiTasks.cell("Set").focus();
        sahiTasks.cell("Set").click();
        sahiTasks.xy(sahiTasks.cell("Set"),3,3).click();
        sahiTasks.waitFor(5000); 
        log.fine(sahiTasks.cell(4).in(getMetricsCell().parentNode("tr")).getText());
        Assert.assertTrue(sahiTasks.cell(4).in(getMetricsCell().parentNode("tr")).getText().indexOf("4") != -1, "Metric cell with changed time exists");
        
        // change time back to default
        //textbox = sahiTasks.textbox("interval");
        //textbox.setValue("2");        
        //sahiTasks.cell("Set").near(sahiTasks.label("Collection Interval")).click();
        //sahiTasks.waitFor(5000);
        //sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();
    }
    
    
}
