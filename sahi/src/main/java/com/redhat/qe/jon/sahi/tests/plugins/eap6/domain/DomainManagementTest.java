package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jmartisk
 */
public class DomainManagementTest extends AS7PluginSahiTestScript {
    
    @BeforeClass(groups="domainManagement")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
    }
    
  //  @Test(groups={"domainManagement"}) 
    public void shutdownDomainController() {
        //TODO
    }
    
  //  @Test(groups={"domainManagement"}) 
    public void startDomainController() {
        //TODO
    }
    
    // restart the domain controller without affecting the server groups it controls
 //   @Test(groups={"domainManagement"}, dependsOnMethods={"shutdownDomainController", "startDomainController"}) 
    public void restartDomainController() {
        //TODO
    }
    
 //   @Test(groups={"domainManagement"}) 
    public void addModuleToDomain() {
        // TODO
    }
    
  //  @Test(groups={"domainManagement"}) 
    public void removeModuleFromDomain() {
        // TODO
    }
    
  //  @Test(groups={"domainManagement"}) 
    public void createSocketBindingGroup() {
        
    }
    
 //   @Test(groups={"domainManagement"}) 
    public void removeSocketBindingGroup() {
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
