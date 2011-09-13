package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.sahi.client.ElementStub;

/**
 *
 * @author Jan Martiska (jmartisk@redhat.com)
 * @since 7 September 2011
 * How to add new testcases: Use this class instead of SahiTasks. Instantiating this class will also load eap6plugin.properties file
 * 
 */
public class AS7PluginSahiTasks {   
    
    public enum Navigate {
      AUTODISCOVERY_QUEUE,  
      AGENT_INVENTORY
    };

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTasks.class.getName());
    protected final SahiTasks tasks;

    public AS7PluginSahiTasks(SahiTasks tasks) {
        this.tasks = tasks;
    }

    public void uninventorizeResourceByNameIfExists(String agentName, String resourceName) {
        log.fine("Uninventorizing resource \"" + resourceName + "\" from agent \"" + agentName + "\"");
        this.navigate(Navigate.AGENT_INVENTORY, agentName);
        tasks.image("Inventory_grey_16.png").click();//near(tasks.cell("Alerts")).click();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
        }     
        ElementStub elm = tasks.div(0).in(tasks.cell(resourceName));
        if(!elm.exists()) {
            log.finer("Resource \"" + resourceName + "\" was not found in the inventory for agent \"" + agentName + "\". Skipping.");
            return;
        }
        elm.click();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
        }
        tasks.cell("Uninventory").click();
        tasks.cell("Yes").click();
    }

    public void uninventorizeAllDomainAS() {
        
    }    

    /**
     * Performs 'manual autodiscovery' operation on the agent of the specified name. The agent has to be already inventorized!
     * @param agentName 
     */
    public void performManualAutodiscovery(String agentName) {
        tasks.link("Inventory").click();
        tasks.cell("Platforms").click();
        tasks.link(agentName).click();
        tasks.cell("Operations").click();
        tasks.cell("New").click();
        tasks.div("isc_SelectItem_1$xc").click();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
        }
        tasks.div("isc_SelectItem_1$xc").click();
        java.awt.Robot robot;
        try {
            robot = new java.awt.Robot();
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ex) {
            }
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ex) {
            }         
            tasks.cell("Schedule").click();
        } catch (AWTException ex) {
            log.severe(ex.getMessage());
        }
    }

    public void inventorizeResourceByName(String agentName, String resourceName) {
        this.navigate(Navigate.AUTODISCOVERY_QUEUE, agentName);        
        tasks.image("unchecked.png").near(tasks.cell(resourceName)).check();
        tasks.cell("Import").click();
    }
   
    public void assertResourceExistsInInventory(String agentName, String resourceName) {
        this.navigate(Navigate.AGENT_INVENTORY, agentName);
        Assert.assertTrue(tasks.cell(resourceName).exists());
    }
    
    public void navigate(Navigate destination, String agentName) {
        switch(destination) {
            case AUTODISCOVERY_QUEUE:
                tasks.link("Inventory").click();
                log.finer("1");
                tasks.cell("Discovery Queue").click();
                log.finer("2");
                tasks.cell(agentName).doubleClick();
                log.finer("3");
                break;
            case AGENT_INVENTORY:
                tasks.link("Inventory").click();
                tasks.cell("Platforms").click();
                tasks.link(agentName).click();
                tasks.image("Inventory_grey_16.png").click();
                break;
            default:
                break;
        }
    }        
}
