package com.redhat.qe.jon.clitest.tests.upgrade;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class Snapshot extends CliEngine {

	@Test
	public void createSnapshot() {
		createJSRunner("upgrade/createSnapshot.js").
				addDepends("rhqapi.js").
				run();
	}
}
