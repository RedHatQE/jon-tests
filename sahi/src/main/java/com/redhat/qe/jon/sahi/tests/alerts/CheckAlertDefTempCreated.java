package com.redhat.qe.jon.sahi.tests.alerts;

import org.testng.annotations.Test;

import java.util.Iterator;

import java.util.logging.Logger;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

import com.redhat.qe.jon.sahi.base.inventory.Resource;

import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinition;


import org.testng.Assert;



public class CheckAlertDefTempCreated extends SahiTestScript {
	
	
	protected Resource platform;
	protected Resource storageNode;
	protected Resource cassandraServerJVM;
	protected Resource memorySubsystem;
	protected Resource nextResource;
	String platformName;
	String storageNodeName;
	Logger log = Logger.getLogger(this.getClass().getName());
	
	
	
	/* 
	This method checks if Alert Definition was found for Storage Node -> Cassandra Server JVM -> Memory Subsystem
	and checks if it is enabled or not
	
	*/
	@Test
	public void checkTemplateInjection(){
		
		checkRequiredProperties("jon.agent.name");

		platformName = System.getProperty("jon.agent.name");
		platform = new Resource(sahiTasks, platformName);
		storageNodeName = System.getProperty("jon.server.host");
		
		
	
		platform.navigate();
		// Retrieve the Storage node Resource
		storageNode = platform.child("RHQ Storage Node("+storageNodeName+")");
		
		// Navigate to the Storage node Resource
		storageNode.navigate();
		
		// Assert that the storage node was found
		Assert.assertNotNull(storageNode, "RHQ Storage Node not found");
		
		log.info("storageNode id  "+storageNode.getId());
		log.info("storageNode name  "+storageNode.getName());
				
		// Retrieve the Cassandra Server JVM resource
		cassandraServerJVM = storageNode.child("Cassandra Server JVM");
		
		// Navigate to Cassandra Server JVM
		cassandraServerJVM.navigate();
		
		//Assert that the Cassandra Server JVM resource was found
		Assert.assertNotNull(cassandraServerJVM, "Cassandra Server JVM not found");
		
		
		log.info("cassandraServerJVM id  "+cassandraServerJVM.getId());
		log.info("cassandraServerJVM name  "+cassandraServerJVM.getName());
		
		//Retrieve the Memory Subsystem Resource
		memorySubsystem = cassandraServerJVM.child("Memory Subsystem");
		
		//Navigate to Memory Subsystem Resource
		memorySubsystem.navigate();
		
		//Assert that Memory Subsystem Resource was found
		Assert.assertNotNull(memorySubsystem, "Memory Subsystem not found");
		
		
		log.info("memorySubsystem id  "+memorySubsystem.getId());
		log.info("memorySubsystem name  "+memorySubsystem.getName());
		
		
	
		Iterator<AlertDefinition> alertDefintionIterator = memorySubsystem.alerts().alertDefinitionsPage().getAlertDefinitions("StorageNodeHighHeapTemplate").iterator();
		
		//Check if Alert Definition is found
		Assert.assertTrue(alertDefintionIterator.hasNext(),"Alert Definition Template not found");
		
		AlertDefinition alertDefinition = alertDefintionIterator.next();
		
		//Check if Alert Definition is enabled
		Assert.assertTrue(alertDefinition.isEnabled(),"Alert Definition not enabled");
		
		log.info("CheckAlertDefTempCreated class finished");
		sahiTasks.logout();
	}

	
		
}
