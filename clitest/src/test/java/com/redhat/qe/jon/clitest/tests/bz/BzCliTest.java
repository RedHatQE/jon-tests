package com.redhat.qe.jon.clitest.tests.bz;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;


public class BzCliTest extends CliEngine {

	@Test(groups={"blockedByBug-814579"})
	public void bz814579Test(){
		createJSRunner("bugs/bug814579.js").
			addExpect("ResourceManagerBean").
			run();
	}
	
	@Test(groups={"blockedByBug-906754"})
	public void bz906754Test(){
		createJSRunner("bugs/bug906754.js").
			run();
	}
	
	@Test(groups={"blockedByBug-907897"})
	public void bz907897Test(){
		createJSRunner("bugs/bug907897.js").
			run();
	}
}
