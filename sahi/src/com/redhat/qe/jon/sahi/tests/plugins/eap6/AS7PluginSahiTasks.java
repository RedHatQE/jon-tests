package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.exceptions.NothingInDiscoveryQueueException;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
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
      AGENT_INVENTORY,
      AGENT_MONITORING,
      AS_INVENTORY
    };

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTasks.class.getName());
    protected final SahiTasks tasks;

    public AS7PluginSahiTasks(SahiTasks tasks) {
        this.tasks = tasks;
    }

    public void uninventorizeResourceByNameIfExists(String agentName, String resourceName) {
        log.fine("Uninventorizing resource \"" + resourceName + "\" from agent \"" + agentName + "\"");
        this.navigate(Navigate.AGENT_INVENTORY, agentName, null);
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
        tasks.div("selectItemText").setValue("m");
        tasks.waitFor(5000);
        tasks.div("selectItemText").setValue("m");
        /**
         * this.cell("Operations").click();
        this.cell("New").click();
        this.div("selectItemText").setValue("g");
        this.waitFor(5000);
        this.div("selectItemText").setValue("g");
        this.cell("Schedule").click();

         */
        
    /*    ElementStub elm = null;
        for(int i=0; i<6; i++) {
            elm = tasks.div("isc_SelectItem_"+i+"$xc");
            if(elm.exists())
                break;            
        }
        if(elm == null) {
            log.severe("Could not locate SelectItem box of operations. Thanks goes to GWT for unparseable, dynamically changed and untestable HTML code");
            Assert.fail("Could not locate SelectItem box of operations. Thanks goes to GWT for unparseable, dynamically changed and untestable HTML code");
        }          
        elm.click();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
        }
        elm.click();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
        }
        java.awt.Robot robot;
        try {
            robot = new java.awt.Robot();
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
            }
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
            robot = null;
            System.gc();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
            }         
            tasks.cell("Schedule").click();
        } catch (AWTException ex) {
            log.severe(ex.getMessage());
        }*/
        tasks.cell("Schedule").click();
    }

    public void inventorizeResourceByName(String agentName, String resourceName) {
        log.fine("Trying to inventorize resource \"" + resourceName + "\" of agent \"" + agentName + "\". Will perform manual autodiscovery first.");
        this.performManualAutodiscovery(agentName);       
        try {
            this.navigate(Navigate.AUTODISCOVERY_QUEUE, agentName, null);              
        } catch(NothingInDiscoveryQueueException ex) {
            log.fine("Could not inventorize resource " + resourceName + ", nothing appeared in autodiscovery queue even after performing manual autodiscovery");
            return;            
        }
        ElementStub elm =  tasks.image("unchecked.png").near(tasks.cell(resourceName));
        if(elm.exists()) {
            elm.check();
            tasks.cell("Import").click();
        } else {
            log.fine("Resource \"" + resourceName + "\" of agent \"" + agentName + "\" not found in Autodiscovery queue, it might have been already inventorized");
        }
        
    }
   
    public void assertResourceExistsInInventory(String agentName, String resourceName) {
        this.navigate(Navigate.AGENT_INVENTORY, agentName, null);
        Assert.assertTrue(tasks.cell(resourceName).exists());
    }
    
    public void navigate(Navigate destination, String agentName, String resourceName) {
        switch(destination) {
            case AUTODISCOVERY_QUEUE:
                tasks.link("Inventory").click();
                tasks.cell("Discovery Queue").click();
                ElementStub elm = tasks.cell(agentName);
                if(elm.exists()) {
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
                tasks.cell("Child Resources").click();
                tasks.cell(resourceName).click();
                tasks.image("Inventory_grey_16.png").click();       
                break;
            default:
                break;
        }
    }        
}
