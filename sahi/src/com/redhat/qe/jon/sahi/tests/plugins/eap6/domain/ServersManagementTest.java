package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
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
    private Resource managedServerJVM;
	@BeforeClass(groups = "serversManagement")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(controller);
		managedServer = controller.child(managed_server_name);
		managedServerJVM = managedServer.child("jvm");
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
		newChild.getEditor().checkRadio("auto-start[0]");
		newChild.getEditor().assertRequiredInputs();
		newChild.finish();
		mgmtDomain.assertResourcePresence("/host="+hostController.getName(), "server-config", managed_server, true);
		controller.performManualAutodiscovery();
		managedServer.assertExists(true);
		// we start the server
		managedServerOperation(managedServer, "Start");
		managedServer.assertAvailable(true, "Managed server MUST be available, because it was just started");
	}
	
    @Test(groups={"serversManagement"},dependsOnMethods="addManagedServer") 
    public void addManagedServerJVM() {
    	Inventory inventory = managedServer.inventory();
        NewChildWizard newChild = inventory.childResources().newChild("JVM Definition");
        newChild.getEditor().setText("resourceName", managedServerJVM.getName());
        newChild.next();
        newChild.getEditor().checkRadio("baseDefinition"); 
        newChild.finish();
        inventory.childHistory().assertLastResourceChange(true);
    	mgmtClient.assertResourcePresence("/host="+hostController.getName()+"/server-config="+managed_server, "jvm", managedServerJVM.getName(), true);
		managedServerJVM.assertExists(true);      
    }
    
    @Test(groups="serversManagement",dependsOnMethods={"addManagedServerJVM"})
    public void configureManagedServer() {
    	Configuration configuration = managedServer.configuration();
    	CurrentConfig current = configuration.current();
    	current.getEditor().checkRadio("other-server-group");
    	current.getEditor().checkRadio("standard-sockets");
    	current.save();
    	configuration.history().failOnFailure();
    	String group = mgmtDomain.readAttribute("/host="+hostController.getName()+"/server-config="+managed_server, "group").get("result").asString();
    	Assert.assertTrue("other-server-group".equals(group), "Managed server configuration (server-group) has been updated");
    }
	
	@Test(groups="serversManagement",dependsOnMethods={"configureManagedServer"})
	public void removeManagedServer() {
		managedServerOperation(managedServer, "Stop");
		managedServer.delete();	
		mgmtDomain.assertResourcePresence("/host="+hostController.getName(), "server-config", managed_server, false);
		controller.child(managed_server_name).assertExists(false);
	}
    
	private void managedServerOperation(Resource server, String op) {
		Operations operations = server.operations();
        Operation o = operations.newOperation(op);
        o.getEditor().checkRadio("blocking[0]");
        o.schedule();
        operations.assertOperationResult(o, true);
        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+ " for managed server to start/stop");
        sahiTasks.waitFor(Timing.TIME_30S);
	}
    
    @Test(groups = {"serversManagement"},dependsOnMethods="stopManagedServer")
    public void startManagedServer() {
        managedServerOperation(serverOne, "Start");
        Assert.assertTrue(httpDomainOne.isRunning(),"Server One is reachable via HTTP");
    }

    @Test(groups = {"serversManagement","blockedByBug-800885"})
    public void stopManagedServer() {
    	managedServerOperation(serverOne, "Stop");
        Assert.assertFalse(httpDomainOne.isRunning(),"Server One is reachable via HTTP");
    }
    @Test(groups = {"serversManagement","blockedByBug-800885"})
    public void restartManagedServer() {
        managedServerOperation(serverOne, "Restart");
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
