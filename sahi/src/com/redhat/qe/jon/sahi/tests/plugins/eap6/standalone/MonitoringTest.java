package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;

/**
 *
 * @author jmartisk, lzoubek
 */
public class MonitoringTest extends AS7StandaloneTest {
    
    @BeforeClass(groups="monitoringTest")
    protected void setupAS7Plugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.importResource(server);        
    }
    
    private ElementStub getMetricsCell() {
    	int tables = sahiTasks.table("listTable").countSimilar();
    	log.fine("listTable count = "+tables);
    	return sahiTasks.cell("Maximum request time").in(sahiTasks.table("listTable["+(tables-1)+"]"));
    }
    private boolean metricsCellVisible() {
    	int tables = sahiTasks.table("listTable").countSimilar();
    	log.fine("listTable count = "+tables);
    	for (ElementStub es : sahiTasks.cell("Maximum request time").in(sahiTasks.table("listTable["+(tables-1)+"]")).collectSimilar()) {
    		if (es.isVisible()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    @Test(groups={"monitoringTest"},dependsOnMethods="enableMetricsTest")
    public void disableMetricsTest()  {
    	server.monitoring();
                       
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();        
        // step1: disable one of the metrics         
        sahiTasks.waitFor(5000);
        sahiTasks.xy(getMetricsCell(),3,3).click();
        sahiTasks.waitFor(2000);
        for (ElementStub disable : sahiTasks.cell("Disable").collectSimilar()) {
        	disable.click();
        }

        sahiTasks.waitFor(10000);
        // step2: check that the metric is not present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 

        sahiTasks.waitFor(5000);
        Assert.assertFalse(metricsCellVisible(),"Metrics 'Maximum request time' is not visible in results table");
    }

    
    @Test(groups={"monitoringTest"})
    public void enableMetricsTest()  {                               
        server.monitoring();
        // check that the metric is present in "Tables" tab                
        sahiTasks.xy(sahiTasks.cell("Tables"), 3, 3).click(); 
        sahiTasks.waitFor(5000);
        Assert.assertTrue(metricsCellVisible());                              
    }

    @Test(groups={"monitoringTest"})
    public void specifyMonitoringIntervalTest() {                       
        server.monitoring();
        sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click(); 
        sahiTasks.waitFor(5000);
        sahiTasks.xy(getMetricsCell(),3,3).click();
        ElementStub textbox = sahiTasks.textbox("interval");
        textbox.setValue("4");
        sahiTasks.waitFor(2000);
        for (ElementStub e : sahiTasks.cell("Set").collectSimilar()) {
        	sahiTasks.xy(e,3,3).click();
        }
        sahiTasks.waitFor(2000); 
        Assert.assertTrue(sahiTasks.cell(4).in(getMetricsCell().parentNode("tr")).getText().indexOf("4") != -1, "Metric cell with changed time exists");
        
        // change time back to default
        //textbox = sahiTasks.textbox("interval");
        //textbox.setValue("2");        
        //sahiTasks.cell("Set").near(sahiTasks.label("Collection Interval")).click();
        //sahiTasks.waitFor(5000);
        //sahiTasks.xy(sahiTasks.cell("Schedules"), 3, 3).click();
    }
    
    
}
