package com.redhat.qe.jon.clitest.tests.operation;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class GroupOperationCliTest extends CliTest {
	
	@Test
	public void operationSchedulingOnGroupUsingCronTest() throws IOException, CliTasksException{
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
