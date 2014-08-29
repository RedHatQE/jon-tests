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
		checkRequiredProperties("jon.agent.name");

		platformName = System.getProperty("jon.agent.name");
		platform = new Resource(sahiTasks, platformName);
		platform.uninventory(true);

		log.info("platform name  " + platformName);
		
		// sleep 3 secs
		Thread.sleep(3000);
		
		if(!sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("Platforms"),
				platformName, Timing.WAIT_TIME))
		{
			throw new RuntimeException(platformName + " platform was not inventoried");
		}
	}

	@Test
	public void checkStorageNodeElementAutoInventoried() {

		log.info(platformName);
	
		// call platform manual auto-discovery
		platform.performManualAutodiscovery();
		// navigate to platform		
		platform.navigate();
		//assert Storage Node exist
		platform.assertChildExists("RHQ Storage Node", true);

	}


	
	@AfterClass()
	protected void inventoryAllResources() {

		platform.inventoryAll(platformName);
	}
}
