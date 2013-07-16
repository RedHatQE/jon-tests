package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class BundlesCliTest extends RhqapiCliTest {
    @Test
    public void bundles() {
	createJSRunner("rhqapi/bundles.js")
		.resourceSrcs("/bundles/bundle.zip","/bundles/bundle-incomplete.zip")
		.resourceDests("/tmp/bundle.zip","/tmp/bundle-incomplete.zip")
		.run();
    }
}
