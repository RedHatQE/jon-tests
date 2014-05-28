package com.redhat.qe.jon.clitest.tests.importPortServices;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class ImportPortServicesTest extends CliEngine {

	@Test
	public void importPortServices() {
		createJSRunner("importPortServices/importPortServices.js").addDepends("rhqapi.js").run();
	}
}
