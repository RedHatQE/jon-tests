package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;

/**
 *
 * @author Jan Martiska (jmartisk@redhat.com)
 * @see TCMS test case 97550
 * @since 16 September 2011
 * 
 */
public class AutodetectConfigurationTest extends AS7DomainTest {                                
    
    @BeforeClass(groups="autodetectConfiguration")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
    
    @Test(groups={"autodetectConfiguration"})
    public void autodetectConfiguration() {
    	controller.uninventory(false);
    	controller.performManualAutodiscovery();
    	as7SahiTasks.importResource(controller);
    	serverOne.assertExists(true);
    	controller.inventory();
        String resourceTypeHTML = (sahiTasks.cell(serverOne.getName()).parentNode("TR")).fetch("innerHTML");
        if(resourceTypeHTML.indexOf("Managed") == -1) {
            Assert.fail("Could not verify that server \"" + serverOne.getName() + "\" in the domain was detected as of type Managed. HTML snippet: " + resourceTypeHTML);                        
        } 
    }    
    
    
    
}
