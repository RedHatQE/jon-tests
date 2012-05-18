package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * tests whether some predefined metrics exist on AS7 resource, also adds/confiures/removes deployment scanner
 * @author Jan Martiska (jmartisk@redhat.com)
 * @see TCMS test case 96428, 96429
 * @since 7 September 2011
 * 
 */
public class ResourceConfigurationTest extends AS7StandaloneTest {
    
	private Resource deploymentScanner;
	private String deployDir="deployDir2";
	
    @BeforeClass()
    protected void setupAS7Plugin() {        
        as7SahiTasks.importResource(server);        
        log.finer("Waiting "+Timing.toString(Timing.TIME_30S)+" till server is properly inventorized");
        sahiTasks.waitFor(Timing.TIME_30S);
        deploymentScanner = server.child("deployment-scanner");
        Assert.assertTrue(server.isAvailable(), 
                "Resource " + server.toString() + " should be ONLINE, but I could not verify this!");
    }
    
    /**
     * Tries to inventorize a standalone AS7 instance and then verifies that it appears in the inventory of the agent.
     * @see TCMS test case 96428
     */
    @Test()
    public void inventoryTest() {
        server.assertExists(true);
    }
    @Test()
    public void addDeploymentScanner() {
    	sshClient.run("mkdir -p "+sshClient.getAsHome()+"/standalone/"+deployDir);
    	Configuration configuration = deploymentScanner.configuration();
    	CurrentConfig config = configuration.current();
    	ConfigEntry ce = config.newEntry(0);
    	ce.getEditor().setText("name", deployDir);
    	ce.getEditor().setText("path", deployDir);
    	ce.getEditor().checkBox(0, false);
    	ce.getEditor().setText("relative-to", "jboss.server.base.dir");
    	ce.OK();
    	config.save();
    	configuration.history().failOnFailure();
    	mgmtClient.assertResourcePresence("/subsystem=deployment-scanner", "scanner", deployDir, true);
    	Assert.assertTrue(mgmtClient.readAttribute("/subsystem=deployment-scanner/scanner="+deployDir, "path").get("result").asString().equals(deployDir),"Deployment scanner has correctly set PATH");
    }
    @Test(dependsOnMethods="addDeploymentScanner")
    public void configureDeploymentScanner() {
    	Configuration configuration = deploymentScanner.configuration();
    	CurrentConfig config = configuration.current();
    	ConfigEntry ce = config.getEntry(deployDir);
    	ce.getEditor().checkRadio("scan-enabled[1]");
    	ce.OK();
    	config.save();
    	configuration.history().failOnFailure();
    	Assert.assertFalse(mgmtClient.readAttribute("/subsystem=deployment-scanner/scanner="+deployDir, "scan-enabled").get("result").asBoolean(),"Deployment scanner configuratio updated - scanner is disabled");
    }
    @Test(dependsOnMethods="configureDeploymentScanner")
    public void removeDeploymentScanner() {
    	Configuration configuration = deploymentScanner.configuration();
    	CurrentConfig config = configuration.current();
    	config.removeEntry(deployDir);
    	config.save();
    	configuration.history().failOnFailure();
    	mgmtClient.assertResourcePresence("/subsystem=deployment-scanner", "scanner", deployDir, false);
    }
    
    /**
     * @see TCMS testcase 96429
     */
    @Test(dependsOnMethods={"inventoryTest"})
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