package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.HTTPClient;

/**
 *
 * @author lzoubek
 */
public class ServerGroupsManagementTest extends AS7DomainTest {
    /**
     * server group being added/configured/deleted
     */
	private Resource myServerGroup;
	/**
	 * server group that is present on EAP by default, being started/stopped/restarted
	 */
	private Resource mainServerGroup;
	/**
	 * list of managed server resources that belong to mainServerGroup
	 */
	private Resource[] managedServers;
	/**
	 * list of HTTPClients for checking managedServers availability via HTTP
	 */
	private HTTPClient[] managedServerClients;
	
    @BeforeClass(groups="serverGroupsManagement")
    protected void setupEapPlugin() {        
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        myServerGroup = controller.child("testing-server-group");
        mainServerGroup = controller.child("main-server-group");
        managedServers = new Resource[] {serverOne,serverTwo};
        managedServerClients = new HTTPClient[] {httpDomainOne,httpDomainTwo};
    }
    @Test(groups={"serverGroupsManagement"}) 
    public void addServerGroup() {
        NewChildWizard newChild = controller.inventory().childResources().newChild("ServerGroup");
        newChild.getEditor().setText("resourceName", myServerGroup.getName());
        newChild.next();
        newChild.getEditor().checkRadio("default"); // profile
        newChild.getEditor().checkRadio("standard-sockets"); //socket group
        newChild.getEditor().assertRequiredInputs();
        newChild.finish();
        mgmtClient.assertResourcePresence("", "server-group", myServerGroup.getName(), true);
		myServerGroup.assertExists(true);        
    }
    
    @Test(groups={"serverGroupsManagement","blockedByBug-802561"},dependsOnMethods="addServerGroup") 
    public void assignSocketBindingGroupToServerGroup() {
        Configuration configuration = myServerGroup.configuration();
        CurrentConfig current = configuration.current();
        current.getEditor().checkRadio("ha-sockets");
        current.save();
        configuration.history().failOnFailure();
        Assert.assertTrue(mgmtClient.readAttribute("/server-group="+myServerGroup.getName(), "socket-binding-group").get("result").asString().equals("ha-sockets"),"Configuration changed");
    }
    
    @Test(groups={"serverGroupsManagement","blockedByBug-802561"},dependsOnMethods="addServerGroup") 
    public void changeProfileToServerGroup() {
        Configuration configuration = myServerGroup.configuration();
        CurrentConfig current = configuration.current();
        current.getEditor().checkRadio("full-ha");
        current.save();
        configuration.history().failOnFailure();
        Assert.assertTrue(mgmtClient.readAttribute("/server-group="+myServerGroup.getName(), "profile").get("result").asString().equals("full-ha"),"Configuration changed");
    }
    
    @Test(groups={"serverGroupsManagement","blockedByBug-802561","blockedByBug-801849"},dependsOnMethods="addServerGroup")     
    public void changeJvmParametersForServerGroup() {
    	Configuration configuration = myServerGroup.configuration();
        CurrentConfig current = configuration.current();
        current.getEditor().checkBox(1, false);
        current.getEditor().setText("jvm", "default");
        current.save();
        configuration.history().failOnFailure();
        mgmtClient.assertResourcePresence("/server-group="+myServerGroup.getName(), "jvm", "default", true);
    }
     
    @Test(alwaysRun=true,groups={"serverGroupsManagement"},dependsOnMethods={"assignSocketBindingGroupToServerGroup","changeJvmParametersForServerGroup"})
    public void removeServerGroup() {
    	myServerGroup.delete();
    	mgmtClient.assertResourcePresence("", "server-group", myServerGroup.getName(), false);
		myServerGroup.assertExists(false);
    }
    @Test(groups={"serverGroupsManagement"})
    public void stopServers() {
    	for (HTTPClient c : managedServerClients) {
    		Assert.assertTrue(c.isRunning(), "Managed server is rachable via HTTP");
    	}		
		Operations operations = mainServerGroup.operations();
		Operation op = operations.newOperation("Stop Servers");
		op.schedule();
		operations.assertOperationResult(op,true);
		log.fine("Waiting "+Timing.toString(Timing.TIME_30S)+" for servers to stop");
		sahiTasks.waitFor(Timing.TIME_30S);
    	
    	for (HTTPClient c : managedServerClients) {
    		Assert.assertFalse(c.isRunning(), "Managed server is rachable via HTTP");
    	}
		for (Resource ms : managedServers) {
			ms.assertAvailable(false, "Managed server "+ms.toString()+" is available");
		}
    }
    @Test(groups={"serverGroupsManagement"},dependsOnMethods="stopServers")
    public void startServers() {
    	Operations operations = mainServerGroup.operations();
		Operation op = operations.newOperation("Start Servers");
		op.schedule();
		operations.assertOperationResult(op,true);
		log.fine("Waiting "+Timing.toString(Timing.TIME_30S)+" for servers to start");
		sahiTasks.waitFor(Timing.TIME_30S);
    	
    	for (HTTPClient c : managedServerClients) {
    		Assert.assertTrue(c.isRunning(), "Managed server is rachable via HTTP");
    	}
		for (Resource ms : managedServers) {
			ms.assertAvailable(true, "Managed server "+ms.toString()+" is available");
		}
    }
    @Test(groups={"serverGroupsManagement"})
    public void restartServerGroup() {
    	for (HTTPClient c : managedServerClients) {
    		Assert.assertTrue(c.isRunning(), "Managed server is rachable via HTTP");
    	}
    	// we'll check startup time only for one of servers in group
    	Date startupDate = sshClient.getStartupTime("domain/servers/server-one/log/boot.log");
		Operations operations = mainServerGroup.operations();
		Operation op = operations.newOperation("Restart Servers");
		op.schedule();
		operations.assertOperationResult(op,true);
		log.fine("Waiting "+Timing.toString(Timing.TIME_30S)+" for servers to restart");
		sahiTasks.waitFor(Timing.TIME_30S);
		Date restartDate = sshClient.getStartupTime("domain/servers/server-one/log/boot.log");
		Assert.assertTrue(restartDate.getTime()>startupDate.getTime(), "Managed server server-one/boot.log first message timestamp check: Server has been restarted");
    	for (HTTPClient c : managedServerClients) {
    		Assert.assertTrue(c.isRunning(), "Managed server is rachable via HTTP");
    	}
		for (Resource ms : managedServers) {
			ms.assertAvailable(true, "Managed server "+ms.toString()+" is available");
		}
    }
    
    
}
