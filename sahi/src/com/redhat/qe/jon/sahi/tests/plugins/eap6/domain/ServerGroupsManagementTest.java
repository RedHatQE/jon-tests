package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import org.junit.Ignore;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk
 */
public class ServerGroupsManagementTest extends AS7PluginSahiTestScript {
    
    @BeforeClass(groups="serverGroupsManagement")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
    
    //@Test(groups={"serverGroupsManagement"}) 
    public void assignSocketBindingGroupToServerGroup() {
        // TODO
    }
    
    //@Test(groups={"serverGroupsManagement"})     
    public void changeJvmParametersForServerGroup() {
        // TODO
    }
    
}
