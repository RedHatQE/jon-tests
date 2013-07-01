package com.redhat.qe.jon.clitest.tests.alerts;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class StorageNodeAlertDefinitionCliTest extends CliTest {

	@Test
	public void rhqStorageNodeTest() throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "alertDefinition/testStorageNodeAlertDefinition.js",
				null, null, null, "rhqapi.js", null, null);
	}
}
