package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;

/**
 *
 * @author lzoubek
 */
public class ServerGroupsManagementTest extends AS7DomainTest {
    
	private Resource serverGroup;
	
    @BeforeClass(groups="serverGroupsManagement")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        serverGroup = controller.child("testing-server-group");
    }
    @Test(groups={"serverGroupsManagement"}) 
    public void addServerGroup() {
        NewChildWizard newChild = controller.inventory().childResources().newChild("ServerGroup");
        newChild.getEditor().setText("resourceName", serverGroup.getName());
        newChild.next();
        newChild.getEditor().checkRadio("default"); // profile
        newChild.getEditor().checkRadio("standard-sockets"); //socket group
        newChild.getEditor().assertRequiredInputs();
        newChild.finish();
        mgmtClient.assertResourcePresence("", "server-group", serverGroup.getName(), true);
		serverGroup.assertExists(true);        
    }
    
    @Test(groups={"serverGroupsManagement","blockedByBug-802561"},dependsOnMethods="addServerGroup") 
    public void assignSocketBindingGroupToServerGroup() {
        Configuration configuration = serverGroup.configuration();
        CurrentConfig current = configuration.current();
        current.getEditor().checkRadio("ha-sockets");
        current.save();
        configuration.history().failOnPending();
        configuration.history().failOnFailure();
        Assert.assertTrue(mgmtClient.readAttribute("/server-group="+serverGroup.getName(), "socket-binding-group").get("result").asString().equals("ha-sockets"),"Configuration changed");
    }
    
    @Test(groups={"serverGroupsManagement","blockedByBug-802561"},dependsOnMethods="addServerGroup") 
    public void changeProfileToServerGroup() {
        Configuration configuration = serverGroup.configuration();
        CurrentConfig current = configuration.current();
        current.getEditor().checkRadio("full-ha");
        current.save();
        configuration.history().failOnPending();
        configuration.history().failOnFailure();
        Assert.assertTrue(mgmtClient.readAttribute("/server-group="+serverGroup.getName(), "profile").get("result").asString().equals("full-ha"),"Configuration changed");
    }
    
    @Test(groups={"serverGroupsManagement","blockedByBug-802561","blockedByBug-801849"},dependsOnMethods="addServerGroup")     
    public void changeJvmParametersForServerGroup() {
    	Configuration configuration = serverGroup.configuration();
        CurrentConfig current = configuration.current();
        current.getEditor().checkBox(1, false);
        current.getEditor().setText("jvm", "default");
        current.save();
    	configuration.history().failOnPending();
        configuration.history().failOnFailure();
        mgmtClient.assertResourcePresence("/server-group="+serverGroup.getName(), "jvm", "default", true);
    }
    
    
    @Test(alwaysRun=true,groups={"serverGroupsManagement"},dependsOnMethods={"assignSocketBindingGroupToServerGroup","changeJvmParametersForServerGroup"})
    public void removeServerGroup() {
    	serverGroup.delete();
    	mgmtClient.assertResourcePresence("", "server-group", serverGroup.getName(), false);
		serverGroup.assertExists(false);
    }
    
    
}
