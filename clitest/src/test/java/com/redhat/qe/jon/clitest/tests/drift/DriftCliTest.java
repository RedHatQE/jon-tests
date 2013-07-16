package com.redhat.qe.jon.clitest.tests.drift;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class DriftCliTest extends CliTest {

	@Test
	public void createDeleteDriftTemplateTest() throws IOException, CliTasksException{
		runJSfile("drift/create_delete_driftTemplate.js");
	}
}
