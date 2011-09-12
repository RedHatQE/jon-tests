package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.Eap6PluginSahiTasks.Navigate;
import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.Eap6PluginSahiTestScript;
import org.testng.annotations.Test;

/**
 *
 * @author Jan Martiska (jmartisk@redhat.com)
 * @see TCMS test case 96426
 * @since 7 September 2011
 * 
 */
public class AutodetectConfigurationTest extends Eap6PluginSahiTestScript {        
    
    @Test(groups={"eap6-standalone-test"})
    public void autodetectConfiguration() throws InterruptedException {          
        eapSahiTasks.navigate(Navigate.AUTODISCOVERY_QUEUE, System.getProperty("agent.name"));                
        String resourceTypeHTML = (sahiTasks.cell(System.getProperty("as7.standalone.name")).parentNode("TABLE")).parentNode("TR").fetch("innerHTML");
        
        boolean found = false;
        if(resourceTypeHTML.indexOf("JBossAS7-Standalone") != -1) {
            found = true;
        }
        if(resourceTypeHTML.indexOf("EAP6") != -1) {
            found = true;
        }        
        Assert.assertTrue(found, "The resource type of a standalone instance should be \"JBossAS7-Standalone\" or \"EAP6\"");
    }    
    
}
