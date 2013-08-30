package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class DynagroupDefinitionsCliTest extends RhqapiCliTest {
	@Test
    public void dynagroupDefinitionsTest() {
	createJSRunner("rhqapi/dynagroupDefinitions.js")
		.run();
    }
}
