package com.redhat.qe.jon.clitest.tests.operation;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class ResourceOperationCliTest extends CliEngine {

	@Test
	public void operationSchedulingOnResourceUsingCronTest(){
		createJSRunner("operations/resourceOpScheduling.js").
			addDepends("rhqapi.js,operations/common.js").
			run();
	}
}
