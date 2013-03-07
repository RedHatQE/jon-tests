package com.redhat.qe.jon.clitest.tests.operation;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class ResourceOperationCliTest extends CliTest {

	@Test
	public void operationSchedulingOnResourceUsingCronTest() throws IOException, CliTasksException{
		runJSfile("operations/resourceOpScheduling.js",
				null,
				"Login successful",
				"rhqapi.js,operations/common.js");
	}
}
