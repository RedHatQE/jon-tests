package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.ChildResources;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.exceptions.NothingInDiscoveryQueueException;
import net.sf.sahi.client.ElementStub;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * @author Jan Martiska (jmartisk@redhat.com)
 * @since 7 September 2011
 *        How to add new testcases: Use this class instead of SahiTasks. Instantiating this class will also load eap6plugin.properties file
 */
public class AS7PluginSahiTasks {

    public enum Navigate {
        AUTODISCOVERY_QUEUE,
        AGENT_INVENTORY,
        AGENT_MONITORING,
        AS_INVENTORY,
        RESOURCE_MONITORING,
        AS_SUMMARY
    }

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTasks.class.getName());
    protected final SahiTasks tasks;

    /**
     * set that remembers what resources are in inventory, so we do not need to ask UI every time we need to
     * know whether resource is in
     */
    private static final Map<String,Boolean> inventoryState = new HashMap<String, Boolean>();
    public AS7PluginSahiTasks(SahiTasks tasks) {
        this.tasks = tasks;
    }


    public void uninventorizeAllDomainAS() {
    	// FIXME: update inventoryState
    }

    /**
     * imports given resource. First check internal state whether resource is already in inventory, 
     * secondly actually check inventory, whether resource is there, third, perform autodiscovery an finally import resource
     * @param res
     */
    public void importResource(Resource res) {
    	String resourceName = res.getName();
    	String agentName = res.getPlatform();
    	log.fine("Trying to inventorize resource \"" + resourceName + "\" of agent \"" + agentName + "\".");
        Boolean inInventory = inventoryState.get(agentName+resourceName);
        if (inInventory==null) {
        	inInventory = Boolean.FALSE;
        }
        if (inInventory) {
        	log.fine("[inventoryState] Resource \"" + resourceName + "\" of agent \"" + agentName + "\" have been already inventorized");
        	return;
        }
        if (!inInventory) {
        	if (res.exists()) {
        		log.fine("Resource \"" + resourceName + "\" of agent \"" + agentName + "\" have been already inventorized");
        		inventoryState.put(agentName+resourceName, Boolean.TRUE);
        		return;
        	}
        	log.fine("Will perform manual autodiscovery first.");
	        res.performManualAutodiscovery();
	        try {
	            tasks.link("Inventory").click();
                tasks.cell("Discovery Queue").click();
                tasks.waitFor(Timing.WAIT_TIME);
                ElementStub elm = tasks.cell(agentName);
                if (elm.exists()) {
                    elm.doubleClick();
                } else {
                    throw new NothingInDiscoveryQueueException();
                }
	            
	        } catch (NothingInDiscoveryQueueException ex) {
	            log.fine("Could not inventorize resource " + resourceName + ", nothing appeared in autodiscovery queue even after performing manual autodiscovery");
	            return;
	        }
	        ElementStub elm = tasks.image("unchecked.png").near(tasks.cell(resourceName));
	        if (elm.exists()) {
	            elm.check();
	            tasks.cell("Import").click();
	            log.fine("Waiting "+Timing.toString(2*Timing.TIME_1M)+" for resource to import...");
	            tasks.waitFor(2*Timing.TIME_1M);
	        } else {
	            log.fine("Resource \"" + resourceName + "\" of agent \"" + agentName + "\" not found in Autodiscovery queue, it might have been already inventorized");
	        }
	        inventoryState.put(agentName+resourceName, Boolean.TRUE);
        }
    }

    /**
     * adds a JMS queue 
     * @param hornetq resource representing hornetq
     * @param queue resource representing queue (should be child of hornetq)
     */
	public void addJMSQueue(Resource hornetq, Resource queue) {
		Inventory inventory = hornetq.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard child = childResources.newChild("JMS Queue");		
		child.getEditor().setText("resourceName", queue.getName());		
		child.next();
		ConfigEntry ce = child.getEditor().newEntry(0);
		ce.setField("entry", queue.getName());
		ce.OK();
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
	}
    /**
     * adds a JMS topic 
     * @param hornetq resource representing hornetq
     * @param topic resource representing topic (should be child of hornetq)
     */
	public void addJMSTopic(Resource hornetq, Resource topic) {
		Inventory inventory = hornetq.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard child = childResources.newChild("JMS Topic");		
		child.getEditor().setText("resourceName", topic.getName());		
		child.next();
		ConfigEntry ce = child.getEditor().newEntry(0);
		ce.setField("entry", topic.getName());
		ce.OK();
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
		
	}

}
