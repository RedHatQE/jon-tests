package com.redhat.qe.jon.clitest.tests.permission;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class RoleCliTest extends CliEngine {
	
	@Test
	public void rolesTest(){
		createJSRunner("permissions/roles.js").
		run();
	}

}
