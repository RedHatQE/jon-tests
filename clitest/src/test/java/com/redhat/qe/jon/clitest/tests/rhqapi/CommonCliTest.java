package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class CommonCliTest extends RhqapiCliTest {

    @Test
    public void common() {
	createJSRunner("rhqapi/common.js").run();
    }
}
