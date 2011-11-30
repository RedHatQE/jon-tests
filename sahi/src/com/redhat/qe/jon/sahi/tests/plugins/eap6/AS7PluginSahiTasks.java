package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.exceptions.NothingInDiscoveryQueueException;
import net.sf.sahi.client.ElementStub;

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

    public void uninventorizeResourceByNameIfExists(String agentName, String resourceName) {
        log.fine("Uninventorizing resource \"" + resourceName + "\" from agent \"" + agentName + "\"");
        Boolean inInventory = inventoryState.get(agentName+resourceName);
        if (inInventory == null) {
        	inInventory = Boolean.TRUE;
        }
        if (!inInventory.booleanValue()) {
        	log.finer("[inventoryState] Resource \"" + resourceName + "\" for agent \"" + agentName + "\" is NOT in inventory. Skipping.");
            return;
        }
        if (inInventory.booleanValue()) {
        	inventoryState.put(agentName+resourceName, Boolean.FALSE);
        	this.navigate(Navigate.AGENT_INVENTORY, agentName, null);
	        tasks.image("Inventory_grey_16.png").click();//near(tasks.cell("Alerts")).click();
	        try {
	            Thread.sleep(2500);
	        } catch (InterruptedException ex) {
	        }
	        ElementStub elm = tasks.div(0).in(tasks.cell(resourceName));
	        if (!elm.exists()) {
	            log.finer("Resource \"" + resourceName + "\" was not found in the inventory for agent \"" + agentName + "\". Skipping.");
	            return;
	        }
	        elm.mouseDown();
	        try {
	            Thread.sleep(2500);
	        } catch (InterruptedException ex) {
	        }
	        tasks.cell("Uninventory").click();
	        tasks.cell("Yes").click();
	        log.fine("Successfully uninventorized resource \"" + resourceName + "\" from agent \"" + agentName + "\"");
        }
    }

    public void uninventorizeAllDomainAS() {
    	// FIXME: update inventoryState
    }

    /**
     * Performs 'manual autodiscovery' operation on the agent of the specified name. The agent has to be already inventorized!
     *
     * @param agentName
     */
    public void performManualAutodiscovery(String agentName) {
        tasks.link("Inventory").click();
        tasks.cell("Platforms").click();
        tasks.link(agentName).click();
        tasks.cell("Operations").click();
        tasks.newInventoryOperation("Manual Autodiscovery");
        tasks.cell("Schedule").click();
    }

    public void inventorizeResourceByName(String agentName, String resourceName) {
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
        	log.fine("Will perform manual autodiscovery first.");
	        this.performManualAutodiscovery(agentName);
	        try {
	            this.navigate(Navigate.AUTODISCOVERY_QUEUE, agentName, null);
	        } catch (NothingInDiscoveryQueueException ex) {
	            log.fine("Could not inventorize resource " + resourceName + ", nothing appeared in autodiscovery queue even after performing manual autodiscovery");
	            return;
	        }
	        ElementStub elm = tasks.image("unchecked.png").near(tasks.cell(resourceName));
	        if (elm.exists()) {
	            elm.check();
	            tasks.cell("Import").click();
	        } else {
	            log.fine("Resource \"" + resourceName + "\" of agent \"" + agentName + "\" not found in Autodiscovery queue, it might have been already inventorized");
	        }
	        inventoryState.put(agentName+resourceName, Boolean.TRUE);
        }

    }

    public void assertResourceExistsInInventory(String agentName, String resourceName) {
        this.navigate(Navigate.AGENT_INVENTORY, agentName, null);
        Assert.assertTrue(tasks.cell(resourceName).exists());
    }

    public boolean checkIfResourceIsOnline(String agentName, String resourceName) {
        this.navigate(Navigate.AS_SUMMARY, agentName, resourceName);
        if (tasks.image("Server_down_24.png").exists()) {
            log.finer("Resource " + resourceName + " is offline!");
            return false;
        }
        if (tasks.image("Server_up_24.png").exists()) {
            log.finer("Resource " + resourceName + " is online!");
            return true;
        }
        Assert.fail("Could not verify whether a resource is online or offline -- neither Server_down_16.png nor Server_up_16.png was found");
        return false;
    }

    public void navigate(Navigate destination, String agentName, String resourceName) {
        switch (destination) {
            case AUTODISCOVERY_QUEUE:
                tasks.link("Inventory").click();
                tasks.cell("Discovery Queue").click();
                ElementStub elm = tasks.cell(agentName);
                if (elm.exists()) {
                    elm.doubleClick();
                } else {
                    throw new NothingInDiscoveryQueueException();
                }
                break;
            case AGENT_INVENTORY:
                tasks.link("Inventory").click();
                tasks.cell("Platforms").click();
                tasks.link(agentName).click();
                tasks.image("Inventory_grey_16.png").click();
                break;
            case AGENT_MONITORING:
                tasks.link("Inventory").click();
                tasks.cell("Platforms").click();
                tasks.link(agentName).click();
                tasks.image("Monitor_grey_16.png").click();
                break;
            case AS_INVENTORY:
                tasks.link("Inventory").click();
                tasks.cell("Platforms").click();
                tasks.link(agentName).click();
                tasks.image("Inventory_grey_16.png").click();
                tasks.xy(tasks.cell("Child Resources"), 3, 3).click();
                tasks.link(resourceName).click();
                tasks.image("Inventory_grey_16.png").click();
                break;
            case RESOURCE_MONITORING:
                tasks.link("Inventory").click();
                tasks.cell("Platforms").click();
                tasks.link(agentName).click();
                tasks.image("Inventory_grey_16.png").click();
                tasks.xy(tasks.cell("Child Resources"), 3, 3).click();
                tasks.link(resourceName).click();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                tasks.image("Monitor_grey_16.png").click();
                break;
            case AS_SUMMARY:
                tasks.link("Inventory").click();
                tasks.cell("Platforms").click();
                tasks.link(agentName).click();
                tasks.image("Inventory_grey_16.png").click();
                tasks.xy(tasks.cell("Child Resources"), 3, 3).click();
                tasks.link(resourceName).click();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                break;
            default:
                break;
        }
    }

}
