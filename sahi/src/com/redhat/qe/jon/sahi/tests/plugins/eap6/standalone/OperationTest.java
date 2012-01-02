package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.SSHClient;

public class OperationTest extends AS7PluginSahiTestScript {

	private SSHClient sshClient;
	@BeforeClass(groups = "operation")
	protected void setupAS7Plugin() {
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
        sshClient = sshStandalone;
        sshClient.connect();
    }
	@Test(groups="operation")
	public void shutdown() {
		Assert.assertTrue(httpStandalone.isRunning(), "Server must be online before we try to stop it");
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.standalone.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Shutdown");
		sahiTasks.waitFor(5000);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(5000);
		sahiTasks.getNavigator().inventorySelectTab("Summary");
		Assert.assertTrue(sahiTasks.image("Operation_ok_16.png").in(sahiTasks.div("Shutdown[0]").parentNode("tr")).exists(),"Shutdown operation successfull");
		log.fine("Waiting 30s for server to stop");
		Assert.assertFalse(sshClient.isRunning(), "Server process is running");
		Assert.assertFalse(httpStandalone.isRunning(), "Server is reachable via HTTP request");
		boolean ok = false;
		for (int i = 0; i < 10; i++) {
			sahiTasks.waitFor(30000);
			log.fine("Checking that resource is offline: try #"
					+ Integer.toString(i + 1) + " of 10");
			if (!as7SahiTasks.checkIfResourceIsOnline(
					System.getProperty("agent.name"),
					System.getProperty("as7.standalone.name"))) {
				log.fine("Success - resource is offline!");
				ok = true;
				break;
			}
		}
		Assert.assertTrue(ok,"EAP server is offline when server is stopped");
	}

	@Test(groups="operation",dependsOnMethods="shutdown")
	public void start() {
		Assert.assertFalse(httpStandalone.isRunning(), "Server must be offline before we try to stop it");
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.standalone.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Start");
		sahiTasks.waitFor(5000);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(5000);
		sahiTasks.getNavigator().inventorySelectTab("Summary");
		Assert.assertTrue(sahiTasks.image("Operation_ok_16.png").in(sahiTasks.div("Start[0]").parentNode("tr")).exists(),"Start operation successfull");
		log.fine("Waiting 30s for server to stop");
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		Assert.assertTrue(httpStandalone.isRunning(), "Server is reachable via HTTP request");
		boolean ok = false;
		for (int i = 0; i < 10; i++) {
			sahiTasks.waitFor(30000);
			log.fine("Checking that resource is online: try #"
					+ Integer.toString(i + 1) + " of 10");
			if (as7SahiTasks.checkIfResourceIsOnline(
					System.getProperty("agent.name"),
					System.getProperty("as7.standalone.name"))) {
				log.fine("Success - resource is online!");
				ok = true;
				break;
			}
		}
		Assert.assertTrue(ok,"EAP server is online when server was started again");
	}

	@Test(groups="operation")
	public void restart() {
		Date startupDate = sshClient.getStartupTime("standalone/log/boot.log");
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Operations", System.getProperty("as7.standalone.name"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Restart");
		sahiTasks.waitFor(5000);
		sahiTasks.cell("Schedule").click();
		sahiTasks.waitFor(5000);
		sahiTasks.getNavigator().inventorySelectTab("Summary");
		Assert.assertTrue(sahiTasks.image("Operation_ok_16.png").in(sahiTasks.div("Restart[0]").parentNode("tr")).exists(),"Restart operation successfull");
		log.fine("Waiting 30s for server to stand up");
		sahiTasks.waitFor(30*1000);
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		Date restartDate = sshClient.getStartupTime("standalone/log/boot.log");
		Assert.assertTrue(restartDate.getTime()>startupDate.getTime(), "Server boot.log first message timestamp check: Server has been restarted");
		Assert.assertTrue(httpStandalone.isRunning(), "Server is reachable via HTTP request");
		log.info("Now, we'll ensure that EAP did not go down after restart");
		boolean ok = true;
		for (int i = 0; i < 10; i++) {
			sahiTasks.waitFor(30000);
			log.fine("Checking that resource is online: try #"
					+ Integer.toString(i + 1) + " of 10");
			if (!as7SahiTasks.checkIfResourceWithChildrenIsOnline(
					System.getProperty("agent.name"),
					System.getProperty("as7.standalone.name"))) {
				log.fine("Resource is offline!");
				ok = false;
			}
			else {
				ok = true;
				log.fine("Resource is online!");
			}
		}
		Assert.assertTrue(ok,"EAP server is online after server was restarted");
	}
}
