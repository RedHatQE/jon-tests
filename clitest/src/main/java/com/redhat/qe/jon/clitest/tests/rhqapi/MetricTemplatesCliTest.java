package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class MetricTemplatesCliTest extends RhqapiCliTest {
    @Test
    public void resourceTypes() {
	createJSRunner("rhqapi/metricTemplates.js").run();
    }
}
