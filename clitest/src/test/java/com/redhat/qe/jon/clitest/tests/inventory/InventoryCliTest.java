package com.redhat.qe.jon.clitest.tests.inventory;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class InventoryCliTest extends CliEngine {

	@Test
	public void discoveryScanTest() {
		createJSRunner("inventory/discoveryScan.js").
			addDepends("rhqapi.js").
			run();
	}
	
	@Test
	public void importAllResourcesTest() {
		createJSRunner("inventory/ImportResources.js").
			addExpect("Resources are imported successfully").
			run();
	}
	
	@Test(groups={"blockedByBug-983210"})
	public void uninventoryAllResourcesTest() {
		createJSRunner("inventory/UninventoryResources.js").
			addDepends("rhqapi.js").
			addExpect("Platforms successfully removed from server").
			run();
	}
}
