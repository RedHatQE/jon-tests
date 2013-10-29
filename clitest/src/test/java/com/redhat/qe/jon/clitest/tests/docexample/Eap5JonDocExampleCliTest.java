package com.redhat.qe.jon.clitest.tests.docexample;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;


public class Eap5JonDocExampleCliTest extends CliEngine {

	
	
	@Test
	public void creatingContentBackedResTest(){
		createJSRunner("docExample/jon/short/creatingContentBackedRes.js").
			dependsOn("rhqapi.js").
			withResource("/deployments/MiscBeans-3.2.2.ear","deployment").
			run();
	}
	@Test(dependsOnMethods = { "creatingContentBackedResTest" },
			priority=1)
	public void startingArrayTest(){
		createJSRunner("docExample/jon/short/startingArray.js").
			dependsOn("rhqapi.js").
			run();
	}
	@Test(dependsOnMethods = { "creatingContentBackedResTest" },
			priority=2)
	public void gettingDataForSingleAndMultipleResTest(){
		createJSRunner("docExample/jon/short/gettingDataForSingleAndMultipleRes.js").
			dependsOn("rhqapi.js").
			run();
	}
	
	@Test(groups={"blockedByBug-851145"},
			dependsOnMethods = { "creatingContentBackedResTest" },
			priority=3)
	public void updatingContentBackedResTest(){
		createJSRunner("docExample/jon/short/updatingContentBackedRes.js").
			dependsOn("rhqapi.js").
			withResource("/deployments/MiscBeans-3.2.5.ear","deployment").
			run();
	}
	
	@Test(dependsOnMethods = { "creatingContentBackedResTest" },
			priority=4)
	public void deletingContentBackedResTest(){
		createJSRunner("docExample/jon/short/deletingContentBackedRes.js").
			dependsOn("rhqapi.js").
			run();
	}
	
	
}
