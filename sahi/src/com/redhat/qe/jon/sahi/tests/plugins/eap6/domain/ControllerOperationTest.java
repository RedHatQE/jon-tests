package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.HTTPClient;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.SSHClient;
/**
*
* @author lzoubek
* @since 19.01.2012
* @see TCMS cases 100056 97588 
*/
public class ControllerOperationTest extends AS7PluginSahiTestScript {

	private static final String managed_server="server-four";
	private static final String managed_server_portoffset="300";
	private static final int waitTime = 5000;
	

	private SSHClient sshClient;
	@BeforeClass(groups = "operation")
	protected void setupAS7Plugin() {
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));
        sshClient = sshDomain;
        sshClient.connect();
    }
	@Test(groups="operation")
	public void shutdown() {
		Assert.assertTrue(httpDomainManager.isRunning(), "Server must be online before we try to stop it");
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.domain.controller.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Shut down this host controller");
		sahiTasks.waitFor(waitTime);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		assertOperationSuccess("Shut down this host controller");
		log.fine("Waiting 30s for server to stop");
		Assert.assertFalse(sshClient.isRunning(), "Server process is running");
		Assert.assertFalse(httpDomainManager.isRunning(), "DomainManager is reachable via HTTP request");
		Assert.assertFalse(httpDomainOne.isRunning(), "server-one is reachable via HTTP request");
		Assert.assertFalse(httpDomainTwo.isRunning(), "server-two is reachable via HTTP request");
		Assert.assertFalse(httpDomainThree.isRunning(), "server-three is reachable via HTTP request");
		boolean ok = false;
		for (int i = 0; i < 10; i++) {
			sahiTasks.waitFor(30000);
			log.fine("Checking that resource is offline: try #"
					+ Integer.toString(i + 1) + " of 10");
			if (!as7SahiTasks.checkIfResourceIsOnline(
					System.getProperty("agent.name"),
					System.getProperty("as7.domain.controller.name"))) {
				log.fine("Success - resource is offline!");
				ok = true;
				break;
			}
		}
		Assert.assertTrue(ok,"EAP server is offline when server is stopped");
	}

	@Test(groups="operation",dependsOnMethods="shutdown")
	public void start() {
		Assert.assertFalse(httpDomainManager.isRunning(), "Server must be offline before we try to stop it");
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.domain.controller.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Start this host controller");
		sahiTasks.waitFor(waitTime);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		assertOperationSuccess("Start this host controller");
		log.fine("Waiting 30s for server to stop");
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		Assert.assertTrue(httpDomainManager.isRunning(), "DomainManager is reachable via HTTP request");
		Assert.assertTrue(httpDomainOne.isRunning(), "server-one is reachable via HTTP request");
		boolean ok = false;
		for (int i = 0; i < 10; i++) {
			sahiTasks.waitFor(30000);
			log.fine("Checking that resource is online: try #"
					+ Integer.toString(i + 1) + " of 10");
			if (as7SahiTasks.checkIfResourceIsOnline(
					System.getProperty("agent.name"),
					System.getProperty("as7.domain.controller.name"))) {
				log.fine("Success - resource is online!");
				ok = true;
				break;
			}
		}
		Assert.assertTrue(ok,"EAP server is online when server was started again");
	}
	@Test(groups="operation")
	public void addManagedServer() {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.domain.controller.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Add managed server");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("servername").setValue(managed_server);
		sahiTasks.waitFor(waitTime);
		sahiTasks.radio(System.getProperty("as7.domain.host.name")).check();
		sahiTasks.waitFor(waitTime);
		sahiTasks.radio("main-server-group").check();
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("port-offset").setValue(managed_server_portoffset);
		sahiTasks.waitFor(waitTime);
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		assertOperationSuccess("Add managed server");
		mgmtDomain.assertResourcePresence("/host="+System.getProperty("as7.domain.host.name"), "server-config", managed_server, true);
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		sahiTasks.getNavigator().inventoryDiscoveryQueue();
		sahiTasks.cell(System.getProperty("agent.name")).doubleClick();
        Assert.assertTrue(sahiTasks.cell("EAP "+managed_server).exists(), "Resource "+managed_server+" is detected by agent");
	}
	
	@Test(groups="operation",dependsOnMethods="addManagedServer")
	public void removeManagedServer() {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.domain.controller.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Remove managed server");
		sahiTasks.waitFor(waitTime);
		sahiTasks.textbox("servername").setValue(managed_server);
		sahiTasks.waitFor(waitTime);
		sahiTasks.radio(System.getProperty("as7.domain.host.name")).check();
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		assertOperationSuccess("Remove managed server");
		mgmtDomain.assertResourcePresence("/host="+System.getProperty("as7.domain.host.name"), "server-config", managed_server, false);
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		sahiTasks.getNavigator().inventoryDiscoveryQueue();
		sahiTasks.cell(System.getProperty("agent.name")).doubleClick();
		Assert.assertFalse(sahiTasks.cell("EAP "+managed_server).exists(), "Resource "+managed_server+" is detected by agent");
	}
	
	@Test(groups="operation")
	public void installRHQUser() {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.domain.controller.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Install RHQ user");
		sahiTasks.waitFor(waitTime);
		String user = "u"+new Date().getTime();
		sahiTasks.textbox("user").setValue(user);
		sahiTasks.waitFor(waitTime);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(waitTime);
		assertOperationSuccess("Install RHQ user");
		String command = "grep '"+user+"' "+System.getProperty("as7.domain.home") + "/domain/configuration/mgmt-users.properties";
		Assert.assertTrue(sshClient.runAndWait(command).getStdout().contains(user), "New user was found on EAP machine in mgmt-users.properties");
	}
}
