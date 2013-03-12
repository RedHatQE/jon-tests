package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class ResourceTypesCliTest extends RhqapiCliTest {
    @Test
    public void resourceTypes() {
	createJSRunner("rhqapi/resourceTypes.js").run();
    }
}
