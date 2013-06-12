package com.redhat.qe.jon.sahi.tests.autoinventory;

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class StorageAutoInventory extends SahiTestScript {

	protected Resource platform;
	String platformName;
	Logger log = Logger.getLogger(this.getClass().getName());

	@BeforeClass()
	protected void uninventoryAllResources() {
		checkRequiredProperties("jon.agent.name");

		platformName = System.getProperty("jon.agent.name");
		platform = new Resource(sahiTasks, platformName);
		platform.uninventory(true);

		log.info("platform name  " + platformName);
	}

	@Test
	public void checkStorageNodeElementInventoried()
			throws InterruptedException {

		log.info(platformName);
		// sleep for 2 sec
		Thread.sleep(2000);
		// wait for platform auto-inventory
		sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("Platforms"),
				platformName, Timing.WAIT_TIME);
		// call platform manual auto-discovery
		platform.operations().newOperation("Manual Autodiscovery");
		// navigate to inventory
		platform.inventory().navigateFull();
		// wait for storage node auto-inventory
		sahiTasks.waitForElementVisible(sahiTasks,
				sahiTasks.cell("RHQ Storage Node"),
				sahiTasks.cell("RHQ Storage Node").text(), Timing.WAIT_TIME);
		Assert.assertTrue(platform.inventory().hasChildren());

		platform.inventoryAll(platformName);
	}

	@AfterClass()
	protected void inventoryAllResources() {

		platform.inventoryAll(platformName);
	}
}
