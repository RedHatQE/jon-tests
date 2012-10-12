package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.util.Arrays;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Resource;

public class CompatibleGroupsTest extends AS7StandaloneTest {
	
//	Resource compatibleGroup;
//	Resource jmsDestination;
//	Resource hornetq;
//	@BeforeClass(groups = "groups")
//    protected void setupAS7Plugin() {
//		as7SahiTasks.importResource(server);
//		as7SahiTasks.importResource(server2);
//		compatibleGroup = new Resource("Compatible Groups", sahiTasks, "standalone-group");
//		hornetq = compatibleGroup.child("messaging").child("default");
//		jmsDestination = hornetq.child("doubledtopic");
//		sahiTasks.createGroup("Compatible Groups", compatibleGroup.getName(), "", true, Arrays.asList(new String[] {hornetq.getName()}));
//    }
//	@Test(groups="groups",description="This test deploys new JMS Topic as composite operation to 2 standalone EAP6 instances")
//	public void addJMSDestination() {
//		as7SahiTasks.addJMSTopic(hornetq, jmsDestination);
//		mgmtStandalone.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-queue", jmsDestination.getName(), true);
//		mgmtStandalone2.assertResourcePresence("/subsystem=messaging/hornetq-server=default", "jms-queue", jmsDestination.getName(), true);
//		server.performManualAutodiscovery();
//		jmsDestination.assertExists(true);
//	}
//	@Test(groups="groups",dependsOnMethods="addJMSDestination")
//	public void removeJMSDestination() {
//		
//	}
}
