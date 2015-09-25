package com.redhat.qe.jon.sahi.tests.alerts;

import org.testng.annotations.Test;

import java.util.Iterator;

import java.util.logging.Logger;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

import com.redhat.qe.jon.sahi.base.inventory.Resource;

import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinition;


import org.testng.Assert;



public class CheckAlertDefTempCreatedTest extends SahiTestScript {
        
        
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
                
                checkRequiredProperties("jon.server.host");

                platformName = System.getProperty("jon.server.host");
                platform = new Resource(sahiTasks, platformName);
                storageNodeName = System.getProperty("jon.server.host");
                
                
        
                platform.navigate();
                // Retrieve the Storage node Resource
                storageNode = platform.child("RHQ Storage Node("+storageNodeName+")");
                
                
                // Assert that the storage node was found
                Assert.assertTrue(storageNode.exists(), "RHQ Storage Node not found");
                
                // Navigate to the Storage node Resource
                storageNode.navigate();
                                
                
                log.info("storageNode id  "+storageNode.getId());
                log.info("storageNode name  "+storageNode.getName());
                                
                // Retrieve the Cassandra Server JVM resource
                cassandraServerJVM = storageNode.child("JVM");
                
                
                //Assert that the Cassandra Server JVM resource was found
                Assert.assertTrue(cassandraServerJVM.exists(),"Cassandra Server JVM not found");
                
                // Navigate to Cassandra Server JVM
                cassandraServerJVM.navigate();
                                
                
                log.info("cassandraServerJVM id  "+cassandraServerJVM.getId());
                log.info("cassandraServerJVM name  "+cassandraServerJVM.getName());
                
                //Retrieve the Memory Subsystem Resource
                memorySubsystem = cassandraServerJVM.child("Memory Subsystem");
                
                
                //Assert that Memory Subsystem Resource was found
                Assert.assertTrue(memorySubsystem.exists(),"Memory Subsystem not found");
                
                //Navigate to Memory Subsystem Resource
                memorySubsystem.navigate();
                                
                
                
                log.info("memorySubsystem id  "+memorySubsystem.getId());
                log.info("memorySubsystem name  "+memorySubsystem.getName());
                
                
        
                Iterator<AlertDefinition> alertDefintionIterator = memorySubsystem.alerts().alertDefinitionsPage().getAlertDefinitions("StorageNodeHighHeap").iterator();
                
                //Check if Alert Definition is found
                Assert.assertTrue(alertDefintionIterator.hasNext(),"Alert Definition Template not found");
                
                AlertDefinition alertDefinition = alertDefintionIterator.next();
                
                //Check if Alert Definition is enabled
                Assert.assertTrue(alertDefinition.isEnabled(),"Alert Definition not enabled");
                
                log.info("CheckAlertDefTempCreated class finished");
        }

        
                
}



