package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * JMS Subsystem - adding/configuring/removing JMS Topic and JMS Queue
 * @author lzoubek
 *
 */
public class JMSDestinationsTest extends AS7StandaloneTest {
	
	private Resource hornetq; 
	private Resource queue;
	private Resource topic;
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        hornetq = server.child("messaging").child("default");
        queue = hornetq.child("test-queue");
        topic = hornetq.child("test-topic");
    }
	@Test()	
	public void addQueue() {
		as7SahiTasks.addJMSQueue(hornetq, queue,"JMS Queue");
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-queue", queue.getName(), true);		
		queue.assertExists(true);
	}
	@Test(dependsOnMethods="addQueue")
	public void removeQueue() 
	{
		queue.delete();
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-queue", queue.getName(), false);		
		queue.assertExists(false);
	}
	
	@Test()	
	public void addTopic() {
		as7SahiTasks.addJMSTopic(hornetq, topic,"JMS Topic");
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-topic", topic.getName(), true);		
		topic.assertExists(true);
	}
	@Test(dependsOnMethods="addTopic")
	public void removeTopic() 
	{
		topic.delete();
		mgmtClient.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-topic", topic.getName(), false);		
		topic.assertExists(false);
	}
}
