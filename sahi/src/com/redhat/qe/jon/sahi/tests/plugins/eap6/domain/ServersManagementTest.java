package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 *
 * @author lzoubek
 */
public class ServersManagementTest extends AS7DomainTest {

	private static final String managed_server="server-four";
	private static final String managed_server_name="EAP "+managed_server;
	private static final String managed_server_portoffset="300";
	
	private Resource managedServer;
    
	@BeforeClass(groups = "operation")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(controller);
		managedServer = controller.child(managed_server_name);
    }
	
	//@Test(groups = {"serversManagement"})
    public void stopServerGroup() {
        //TODO
    }

   // @Test(groups = {"serversManagement"})
    public void startServerGroup() {
        //TODO
    }

	@Test(groups="serversManagement")
	public void addManagedServer() {
		NewChildWizard newChild = controller.inventory().childResources().newChild("Managed Server");
		newChild.getEditor().setText("resourceName",managed_server);
		newChild.next();
		newChild.getEditor().checkRadio(hostController.getName());
		newChild.getEditor().checkRadio("main-server-group");
		newChild.getEditor().setText("socket-binding-port-offset", managed_server_portoffset);
		newChild.getEditor().assertRequiredInputs();
		newChild.finish();
		mgmtDomain.assertResourcePresence("/host="+hostController.getName(), "server-config", managed_server, true);
		controller.performManualAutodiscovery();
		managedServer.assertExists(true);		
	}
	
	@Test(groups="serversManagement",dependsOnMethods="addManagedServer")
	public void removeManagedServer() {
		managedServer.delete();	
		mgmtDomain.assertResourcePresence("/host="+hostController.getName(), "server-config", managed_server, false);
		controller.performManualAutodiscovery();
		controller.child(managed_server_name).assertExists(false);
	}
    
    
    @Test(groups = {"serversManagement"},dependsOnMethods="stopManagedServer")
    public void startManagedServer() {
    	Operations operations = serverOne.operations();
        Operation o = operations.newOperation("Start");
        o.schedule();
        operations.assertOperationResult(o, true);
        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+ " for managed server to start");
        sahiTasks.waitFor(Timing.TIME_30S);
        Assert.assertTrue(httpDomainOne.isRunning(),"Server One is reachable via HTTP");
    }

    @Test(groups = {"serversManagement","blockedByBug-800885"})
    public void stopManagedServer() {
    	Operations operations = serverOne.operations();
        Operation o = operations.newOperation("Stop");
        o.schedule();
        operations.assertOperationResult(o, true);
        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+ " for managed server to stop");
        sahiTasks.waitFor(Timing.TIME_30S);
        Assert.assertTrue(!httpDomainOne.isRunning(),"Server One is NOT reachable via HTTP");
    }
    @Test(groups = {"serversManagement","blockedByBug-800885"})
    public void restartManagedServer() {
    	Operations operations = serverOne.operations();
        Operation o = operations.newOperation("Restart");
        o.schedule();
        operations.assertOperationResult(o, true);
        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+ " for managed server to restart");
        sahiTasks.waitFor(Timing.TIME_30S);
        Assert.assertTrue(httpDomainOne.isRunning(),"Server One is reachable via HTTP");
    }
    
  //  @Test(groups={"serversManagement"}) 
    public void createSocketBindingGroup() {
        //TODO
    }
    
  //  @Test(groups={"serversManagement"}, dependsOnMethods={"createSocketBindingGroup"}) 
    public void applySocketBindingGroupToServerGroup() {
        //TODO
    }
    
  //  @Test(groups = {"serversManagement"})
    public void changeJvmParametersForServerGroup() {
        
    }
}
