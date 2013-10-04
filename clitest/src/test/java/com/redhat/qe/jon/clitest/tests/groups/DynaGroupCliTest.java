package com.redhat.qe.jon.clitest.tests.groups;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class DynaGroupCliTest extends CliEngine {
	
	// this blocking bz is still not fixed, it would block tests from running so the test is 
	// changed temporarily 
	@Test//(groups={"blockedByBug-990466"})
	public void createDynaGroupDefinition(){
		createJSRunner("groups/createDynaGroupDef.js").
			addDepends("/rhqapi.js").
			addDepends("/groups/utils.js").
			run();
	}
	
	@Test(groups={"blockedByBug-974501","blockedByBug-974540"})
	public void createDynaGroupDefinitionNegativeTest(){
		createJSRunner("groups/createDynaGroupDefNegative.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}
	
	@Test(dependsOnMethods={"createDynaGroupDefinition"},priority=0)
	public void editDynaGroupDefinition(){
		createJSRunner("groups/editDynaGroupDef.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}
	
	@Test(dependsOnMethods={"createDynaGroupDefinition"},priority=1,
			groups={"blockedByBug-974501","blockedByBug-974540"})
	public void editDynaGroupDefinitionNegativeTest(){
		createJSRunner("groups/editDynaGroupDefNegative.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}
	
	@Test(dependsOnMethods={"createDynaGroupDefinition"},priority=2)
	public void deleteDynaGroupDefinition(){
		createJSRunner("groups/deleteDynaGroupDef.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}
	@Test(dependsOnMethods={"createDynaGroupDefinition"},priority=3)
	public void staticMembershipTest(){
		createJSRunner("groups/staticMembership.js").
			addDepends("/rhqapi.js").
			addDepends("/groups/utils.js").
			run();
	}
	@Test(dependsOnMethods={"createDynaGroupDefinition"},priority=3)
	public void propagateOperationScheduleTest(){
		createJSRunner("groups/propagateOperationSchedule.js").
			addDepends("/rhqapi.js").
			addDepends("/groups/utils.js").
			addDepends("/operations/common.js").
			run();
	}
	@Test
	public void deleteDynaGroupDefinitionNegativeTest(){
		createJSRunner("groups/deleteDynaGroupDefNegative.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}
}
