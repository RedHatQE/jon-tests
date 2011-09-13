package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks.Navigate;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk@redhat.com
 * @see TCMS testcase 96430
 */
public class ResourceCreation001Test extends AS7PluginSahiTestScript {

    @BeforeClass(groups="resourceCreation001")
    protected void setupAS7Plugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
    
    @Test(groups="resourceCreation001")
    public void modifyConnectionProperties() {
        as7SahiTasks.navigate(Navigate.AS_INVENTORY, System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        sahiTasks.cell("Connection Settings").click();
    }
    
}
