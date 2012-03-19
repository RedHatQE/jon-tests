package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * tests adding/removing JMS destinations for EAP7 running in Domain mode, <b>full</b> profile is used, 
 * because it is by default configured to run messaging
 * @author lzoubek
 *
 */
public class JMSDestinationsTest extends AS7DomainTest {
	private Resource hornetq; 
	private Resource queue;
	private Resource topic;
	@BeforeClass(groups = "setup")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(controller);
        hornetq = controller.child("full").child("messaging").child("default");
        queue = hornetq.child("test-queue");
        topic = hornetq.child("test-topic");
    }
	@Test(groups="jms")	
	public void addQueue() {
		as7SahiTasks.addJMSQueue(hornetq, queue);
		mgmtClient.assertResourcePresence("/profile=full/subsystem=messaging/hornetq-server=default", "jms-queue", queue.getName(), true);		
		controller.performManualAutodiscovery();
		queue.assertExists(true);
	}
	@Test(groups="jms",dependsOnMethods="addQueue")
	public void removeQueue() 
	{
		queue.delete();
		mgmtClient.assertResourcePresence("/profile=full/subsystem=messaging/hornetq-server=default", "jms-queue", queue.getName(), false);		
		queue.assertExists(false);
	}
	
	@Test(groups="jms")	
	public void addTopic() {
		as7SahiTasks.addJMSTopic(hornetq, topic);
		mgmtClient.assertResourcePresence("/profile=full/subsystem=messaging/hornetq-server=default", "jms-topic", topic.getName(), true);		
		controller.performManualAutodiscovery();
		topic.assertExists(true);
	}
	@Test(groups="jms",dependsOnMethods="addTopic")
	public void removeTopic() 
	{
		topic.delete();
		mgmtClient.assertResourcePresence("/profile=full/subsystem=messaging/hornetq-server=default", "jms-topic", topic.getName(), false);		
		topic.assertExists(false);
	}
}
