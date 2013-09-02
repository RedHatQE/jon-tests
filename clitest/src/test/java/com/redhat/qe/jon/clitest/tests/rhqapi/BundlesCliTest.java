package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class BundlesCliTest extends RhqapiCliTest {
    @Test
    public void bundles() {
	createJSRunner("rhqapi/bundles.js")
		.withResource("/bundles/bundle-incomplete.zip", "bundleIncomplete")
		.withResource("/bundles/bundle.zip","bundle")
		.run();
    }
}
