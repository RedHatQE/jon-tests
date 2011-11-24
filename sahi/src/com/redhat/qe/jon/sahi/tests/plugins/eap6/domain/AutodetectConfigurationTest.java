package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks.Navigate;
import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Jan Martiska (jmartisk@redhat.com)
 * @see TCMS test case 97550
 * @since 16 September 2011
 * 
 */
public class AutodetectConfigurationTest extends AS7PluginSahiTestScript {                                
    
    @BeforeClass(groups="autodetectConfiguration")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
    
    @Test(groups={"autodetectConfiguration"})
    public void autodetectConfiguration() {
        as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
        as7SahiTasks.navigate(Navigate.AUTODISCOVERY_QUEUE, System.getProperty("agent.name"), null);
        
        // check servers under the domain controller (or at least one of them)
        String resourceTypeHTML = (sahiTasks.cell(System.getProperty("as7.domain.host.server-one.name")).parentNode("TABLE")).parentNode("TR").fetch("innerHTML");
        if(resourceTypeHTML.indexOf("JBossAS7 Managed") == -1) {
            Assert.fail("Could not verify that server \"" + System.getProperty("as7.domain.host.server-one.name") + "\" in the domain was detected as of type JBossAS-Managed. HTML snippet: " + resourceTypeHTML);                        
        }        
    }    
    
    
    
}
