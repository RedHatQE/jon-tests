package com.redhat.qe.jon.clitest.tests.alerts;

import java.io.IOException;
import org.testng.annotations.Test;
import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;

public class StorageNodeAlertDefinitionCliTest extends CliEngine {
	@Test
	public void rhqStorageNodeTest() throws IOException, CliTasksException {
		createJSRunner("alertDefinition/testStorageNodeAlertDefinition.js")
				.addDepends("rhqapi.js").run();

	}
}