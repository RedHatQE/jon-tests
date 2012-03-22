package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;

public class OperationTest extends AS7StandaloneTest {
	

	@BeforeClass(groups = "operation")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        sshClient.connect();
    }
	
	@Test(groups="operation")
	public void shutdown() {
		Assert.assertTrue(httpClient.isRunning(), "Server must be online before we try to stop it");		
		Operations operations = server.operations();
		Operation op = operations.newOperation("Shutdown");
		op.schedule();
		operations.assertOperationResult(op,true);
		log.fine("Waiting "+Timing.toString(Timing.TIME_30S)+" for server to stop");
		sahiTasks.waitFor(Timing.TIME_30S);
		Assert.assertFalse(sshClient.isRunning(), "Server process is running");
		Assert.assertFalse(httpClient.isRunning(), "Server is reachable via HTTP request");		
		server.assertAvailable(false,"EAP server is offline when server is stopped");
	}

	@Test(groups="operation",dependsOnMethods="shutdown")
	public void start() {
		Assert.assertFalse(httpClient.isRunning(), "Server must be offline before we try to stop it");
		Operations operations = server.operations();
		Operation op = operations.newOperation("Start");
		op.schedule();
		operations.assertOperationResult(op,true);
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		Assert.assertTrue(httpClient.isRunning(), "Server is reachable via HTTP request");
		server.assertAvailable(true,"EAP server is online when server was started again");
	}

	@Test(groups="operation")
	public void restart() {
		Date startupDate = sshClient.getStartupTime("standalone/log/boot.log");
		
		Operations operations = server.operations();
		Operation op = operations.newOperation("Restart");
		op.schedule();
		operations.assertOperationResult(op,true);

		log.fine("Waiting "+Timing.toString(Timing.TIME_30S)+" for server to restart");
		sahiTasks.waitFor(Timing.TIME_30S);
		Assert.assertTrue(sshClient.isRunning(), "Server process is running");
		Date restartDate = sshClient.getStartupTime("standalone/log/boot.log");
		Assert.assertTrue(restartDate.getTime()>startupDate.getTime(), "Server boot.log first message timestamp check: Server has been restarted");
		Assert.assertTrue(httpClient.isRunning(), "Server is reachable via HTTP request");
		log.info("Now, we'll ensure that EAP did not go down after restart");
		boolean ok = true;
		for (int i = 0; i < Timing.REPEAT; i++) {
			sahiTasks.waitFor(Timing.TIME_30S);
			log.fine("Checking that resource is online: try #"
					+ Integer.toString(i + 1) + " of 10");
			if (!server.isAvailable()) {
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
	
	@Test(groups="operation")
	public void installRHQUser() {
		Operations operations = server.operations();
		Operation op = operations.newOperation("Install RHQ user");	
		String user = "u"+new Date().getTime();
		sahiTasks.textbox("user").setValue(user);
		sahiTasks.waitFor(Timing.WAIT_TIME);
		op.schedule();
		operations.assertOperationResult(op,true);				
		String command = "grep '"+user+"' "+sshClient.getAsHome() + "/standalone/configuration/mgmt-users.properties";
		Assert.assertTrue(sshClient.runAndWait(command).getStdout().contains(user), "New user was found on EAP machine in mgmt-users.properties");
	}
}
