package com.redhat.qe.jon.sahi.tests.autoinventory;

import java.util.logging.Logger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class StorageAutoInventoryTest extends SahiTestScript {

	protected Resource platform;
	
	String platformName;
	Logger log = Logger.getLogger(this.getClass().getName());

	@BeforeClass()
	protected void uninventoryAllResources() throws InterruptedException {
		checkRequiredProperties("jon.server.host");

		platformName = System.getProperty("jon.server.host");
		platform = new Resource(sahiTasks, platformName);
		platform.uninventory(true);
	}

	@Test
	public void checkStorageNodeElementAutoInventoried() {
        log.info("platform name  " + platformName);
        sahiTasks.cell("Refresh").click();
        if (!sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell(platformName),
                platformName, Timing.TIME_5S)) {
            sahiTasks.cell("Refresh").click();
        }
        if (!sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell(platformName),
                platformName, Timing.TIME_5S)) {
            throw new RuntimeException(platformName + " platform was not inventoried");
        }
		// call platform manual auto-discovery
		platform.performManualAutodiscovery();
		// navigate to platform		
		platform.navigate();
		//assert Storage Node exist
		platform.assertChildExists("RHQ Storage Node(" + platformName + ")", true);

	}


	
	@AfterClass(alwaysRun=true)
	protected void inventoryAllResources() {

		platform.inventoryAll(platformName);
	}
}
