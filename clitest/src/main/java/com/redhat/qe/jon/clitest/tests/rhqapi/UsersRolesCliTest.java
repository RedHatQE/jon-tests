package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class UsersRolesCliTest extends RhqapiCliTest {

    @Test
    public void usersRolesPermissions() {
	createJSRunner("rhqapi/users-roles-permissions.js").run();
    }
}
