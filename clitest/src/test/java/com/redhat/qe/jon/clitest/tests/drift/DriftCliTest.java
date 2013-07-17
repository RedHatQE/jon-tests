package com.redhat.qe.jon.clitest.tests.drift;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class DriftCliTest extends CliEngine {

	@Test
	public void createDeleteDriftTemplateTest() {
		createJSRunner("drift/create_delete_driftTemplate.js").
			run();
	}
}
