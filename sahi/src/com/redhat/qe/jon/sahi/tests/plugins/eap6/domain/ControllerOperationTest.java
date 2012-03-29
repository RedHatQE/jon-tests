package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
/**
*
* @author lzoubek
* @since 19.01.2012
* @see TCMS cases 100056 97588 
*/
public class ControllerOperationTest extends AS7DomainTest {

	@BeforeClass(groups = "operation")
	protected void setupAS7Plugin() {
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
		as7SahiTasks.importResource(controller);
    }
	@Test(groups={"operation","blockedByBug-807942"})
	public void shutdown() {
		Assert.assertTrue(httpDomainManager.isRunning(), "Server must be online before we try to stop it");
		Operations operations = controller.operations();
		Operation op = operations.newOperation("Shut down this host controller");
		op.schedule();
		operations.assertOperationResult(op, true);
		log.info("Waiting "+Timing.toString(Timing.TIME_30S)+" for server to stop");
		sahiTasks.waitFor(Timing.TIME_30S);
		Assert.assertFalse(sshClient.isRunning(), "Server process is running");
		Assert.assertFalse(httpDomainManager.isRunning(), "DomainManager is reachable via HTTP request");
		Assert.assertFalse(httpDomainOne.isRunning(), "server-one is reachable via HTTP request");
		Assert.assertFalse(httpDomainTwo.isRunning(), "server-two is reachable via HTTP request");
		Assert.assertFalse(httpDomainThree.isRunning(), "server-three is reachable via HTTP request");
		controller.assertAvailable(false, "EAP server is offline when server is stopped");
	}

	@Test(groups="operation",dependsOnMethods="shutdown")
	public void start() {
		Assert.assertFalse(httpDomainManager.isRunning(), "Server must be offline before we try to stop it");
		Operations operations = controller.operations();
		Operation op = operations.newOperation("Start this host controller");
		op.schedule();
		operations.assertOperationResult(op, true);		
		log.info("Waiting "+Timing.toString(Timing.TIME_30S)+" for server to start");
		sahiTasks.waitFor(Timing.TIME_30S);
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		Assert.assertTrue(httpDomainManager.isRunning(), "DomainManager is reachable via HTTP request");
		Assert.assertTrue(httpDomainOne.isRunning(), "server-one is reachable via HTTP request");
		controller.assertAvailable(true, "EAP server is online when server was started");
	}
	
	@Test(groups="operation")
	public void installRHQUser() {
		Operations operations = controller.operations();
		Operation op = operations.newOperation("Install RHQ user");	
		String user = "u"+new Date().getTime();
		op.getEditor().setText("user", user);
		op.schedule();
		operations.assertOperationResult(op,true);				
		String command = "grep '"+user+"' "+sshClient.getAsHome() + "/domain/configuration/mgmt-users.properties";
		Assert.assertTrue(sshClient.runAndWait(command).getStdout().contains(user), "New user was found on EAP machine in mgmt-users.properties");
	}
}
