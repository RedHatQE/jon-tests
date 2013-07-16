package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class GroupsCliTest extends RhqapiCliTest {

    @Test
    public void groups() {
	createJSRunner("rhqapi/groups.js").run();
    }
    
    @Test
    public void scheduleOperation() {
	createJSRunner("rhqapi/group_scheduleOperation.js").run();
    }
    
    @Test
    public void invokeOperation() {
	createJSRunner("rhqapi/group_invokeOperation.js").run();
    }
}
