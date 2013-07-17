package com.redhat.qe.jon.clitest.tests.manager;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class ManagerCliTest extends CliEngine {
	
	@Test
	public void systemManagerTest() {
		createJSRunner("managers/systemmanager.js").
			run();
	}
	
	@Test
	public void resourceTypeManagerTest() {
		createJSRunner("managers/resourcetypemanager.js").
			run();
	}
	
	@Test
	public void dataAccessManagerTest() {
		createJSRunner("managers/dataaccessmanager.js").
			run();
	}
	
	@Test
	public void repoManagerTest() {
		createJSRunner("managers/repo.js").
			run();
	}
	
	@Test
	public void operationManagerTest() {
		createJSRunner("managers/viewProcessList.js").
			addDepends("rhqapi.js").
			run();
	}
	
	@Test
	public void measurementDefinitionManagerTest() {
		createJSRunner("managers/measurementdefinitions.js").
			run();
	}
}
