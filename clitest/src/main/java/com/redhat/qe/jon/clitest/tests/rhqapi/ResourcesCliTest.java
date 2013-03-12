package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class ResourcesCliTest extends RhqapiCliTest {

    @Test
    public void discoveryQueue() {
	createJSRunner("rhqapi/discoveryQueue.js").run();
    }
    
    @Test
    public void importResources() {
	createJSRunner("rhqapi/import.js").run();
    }
    @Test
    public void find() {
	createJSRunner("rhqapi/find.js").run();
    }
}
