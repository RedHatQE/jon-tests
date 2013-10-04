package com.redhat.qe.jon.clitest.tests.stress;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class DynaGroupStressTest extends CliEngine{

	@Test(groups={"blockedByBug-976265"})
	public void createEditRecalculateDeleteStressTest(){
		createJSRunner("stress/createEditRecalculateDeleteStressTest.js").
		addDepends("/rhqapi.js").
		addDepends("/groups/utils.js").
		run();
	}
}
