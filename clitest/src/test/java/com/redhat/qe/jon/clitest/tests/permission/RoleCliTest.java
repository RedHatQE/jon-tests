package com.redhat.qe.jon.clitest.tests.permission;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class RoleCliTest extends CliTest {
	
	@Test(groups={"blockedByBug-841625"})
	public void userRolesTest() throws IOException, CliTasksException{
		runJSfile("permissions/userRoles.js");
	}
	
	@Test
	public void rolesTest() throws IOException, CliTasksException{
		runJSfile("permissions/roles.js");
	}

}
