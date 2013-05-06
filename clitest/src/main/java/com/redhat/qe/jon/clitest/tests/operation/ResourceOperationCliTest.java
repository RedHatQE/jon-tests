package com.redhat.qe.jon.clitest.tests.operation;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tests.CliTest;

public class ResourceOperationCliTest extends CliTest {

	@Test
	public void operationSchedulingOnResourceUsingCronTest(){
		createJSRunner("operations/resourceOpScheduling.js").
			addDepends("rhqapi.js,operations/common.js").
			run();
	}
}
