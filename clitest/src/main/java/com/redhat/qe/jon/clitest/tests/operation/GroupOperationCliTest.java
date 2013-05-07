package com.redhat.qe.jon.clitest.tests.operation;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class GroupOperationCliTest extends CliEngine {
	
	@Test
	public void operationSchedulingOnGroupUsingCronTest() {
		createJSRunner("operations/groupOpSchedulingUsingCron.js").
			addDepends("rhqapi.js,operations/common.js").
			run();
	}
	
	@Test
	public void operationSchedulingOnGroupTest(){
		createJSRunner("operations/groupOpScheduling.js").
			addDepends("rhqapi.js,operations/common.js").
			run();
	}

}
