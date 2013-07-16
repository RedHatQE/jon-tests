package com.redhat.qe.jon.clitest.tests.inventory;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class InventoryCliTest extends CliTest {

	@Test
	public void discoveryScanTest() throws IOException, CliTasksException{
		runJSfile("inventory/discoveryScan.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
	
	@Test
	public void importAllResourcesTest() throws IOException, CliTasksException{
		runJSfile("inventory/ImportResources.js",
				"Login successful,Resources are imported successfully");
	}
	
	@Test
	public void uninventoryAllResourcesTest() throws IOException, CliTasksException{
		runJSfile("inventory/UninventoryResources.js",
				null,
				"Login successful,Platforms successfully removed from server",
				"rhqapi.js");
	}
}
