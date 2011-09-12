package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan Martiska (jmartisk@redhat.com)
 * @since 7 September 2011
 * How to add new testcases: Use this class instead of SahiTasks. Instantiating this class will also load eap6plugin.properties file
 * 
 */
public class Eap6PluginSahiTasks {
    
    public enum Navigate {
      AUTODISCOVERY_QUEUE  
    };

    protected static final Logger log = Logger.getLogger(Eap6PluginSahiTasks.class.getName());
    private SahiTasks tasks;

    public Eap6PluginSahiTasks(SahiTasks tasks) {
        this.tasks = tasks;
    }

    public void uninventorizeResourceByName(String agentName, String resourceName) {
        tasks.link("Inventory").click();
        tasks.cell("Platforms").click();
        tasks.link(agentName).click();
        tasks.image("Inventory_grey_16.png").click();//near(tasks.cell("Alerts")).click();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
        }     
        tasks.div(0).in(tasks.cell(resourceName)).click();
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
        }
        tasks.cell("Uninventory").click();
        tasks.cell("Yes").click();
    }

    public void uninventorizeAllDomainEAP() {
        // TODO
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
   
    public void navigate(Navigate destination, String agentName) {
        switch(destination) {
            case AUTODISCOVERY_QUEUE:
                tasks.link("Inventory").click();
                tasks.cell("Discovery Queue").click();
                tasks.cell(agentName).doubleClick();
                break;
            default:
                break;
        }
    }
}
