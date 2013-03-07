package com.redhat.qe.jon.clitest.tests.operation;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class GroupOperationCliTest extends CliTest {
	
	@Test
	public void operationSchedulingOnGroupUsingCronTest() throws IOException, CliTasksException{
		runJSfile("operations/groupOpScheduling.js",
				null,
				"Login successful",
				"rhqapi.js,operations/common.js");
	}

}
