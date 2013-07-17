package com.redhat.qe.jon.clitest.tests.permission;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class RoleCliTest extends CliEngine {
	
	@Test(groups={"blockedByBug-841625"})
	public void userRolesTest() {
		createJSRunner("permissions/userRoles.js").
		run();
	}
	
	@Test
	public void rolesTest(){
		createJSRunner("permissions/roles.js").
		run();
	}

}
