package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

public class JMSDestinationsTest extends AS7StandaloneTest {
	
	private Resource hornetq; 
	private Resource queue;
	private Resource topic;
	@BeforeClass(groups = "setup")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        hornetq = server.child("messaging").child("default");
        queue = hornetq.child("test-queue");
        topic = hornetq.child("test-topic");
    }
	@Test(groups="jms")	
	public void addQueue() {
		Operations operations = hornetq.operations();
		Operation op = operations.newOperation("Add destination");
		op.getEditor().setText("name", queue.getName());
		ConfigEntry ce = op.getEditor().newEntry(0);
		ce.setField("entry", queue.getName());
		ce.OK();
		op.getEditor().checkRadio("type[0]");
		op.assertRequiredInputs();
		op.schedule();
		operations.assertOperationResult(op, true);
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-queue", queue.getName(), true);		
		server.performManualAutodiscovery();
		queue.assertExists(true);
	}
	@Test(groups="jms",dependsOnMethods="addQueue")
	public void removeQueue() 
	{
		queue.delete();
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-queue", queue.getName(), false);		
		queue.assertExists(false);
	}
	
	@Test(groups="jms")	
	public void addTopic() {
		Operations operations = hornetq.operations();
		Operation op = operations.newOperation("Add destination");
		op.getEditor().setText("name", topic.getName());
		ConfigEntry ce = op.getEditor().newEntry(0);
		ce.setField("entry", topic.getName());
		ce.OK();
		op.getEditor().checkRadio("type[1]");
		op.assertRequiredInputs();
		op.schedule();
		operations.assertOperationResult(op, true);
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-topic", topic.getName(), true);		
		server.performManualAutodiscovery();
		topic.assertExists(true);
	}
	@Test(groups="jms",dependsOnMethods="addTopic")
	public void removeTopic() 
	{
		topic.delete();
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-topic", topic.getName(), false);		
		topic.assertExists(false);
	}
}
