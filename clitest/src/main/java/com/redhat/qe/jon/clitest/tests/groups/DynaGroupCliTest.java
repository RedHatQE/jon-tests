package com.redhat.qe.jon.clitest.tests.groups;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class DynaGroupCliTest extends CliEngine {
	
	@Test
	public void createDynaGroupDefinition(){
		createJSRunner("groups/createDynaGroupDef.js").
			addDepends("/rhqapi.js").
			addDepends("/groups/utils.js").
			run();
	}
	
	@Test(dependsOnMethods={"createDynaGroupDefinition"})
	public void editDynaGroupDefinition(){
		createJSRunner("groups/editDynaGroupDef.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}
	
	@Test(dependsOnMethods={"editDynaGroupDefinition"})
	public void deleteDynaGroupDefinition(){
		createJSRunner("groups/deleteDynaGroupDef.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}

}
