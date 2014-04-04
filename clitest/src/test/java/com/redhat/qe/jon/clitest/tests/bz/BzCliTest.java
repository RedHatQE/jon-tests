package com.redhat.qe.jon.clitest.tests.bz;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;


public class BzCliTest extends CliEngine {

	@Test(groups={"blockedByBug-814579"})
	public void bz814579Test(){
		createJSRunner("bugs/bug814579.js").
			addExpect("Launch").
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
	
	@Test(groups={"blockedByBug-855674"})
	public void bz855674() {
	    createJSRunner("bugs/bug855674.js")
	    	.addDepends("/rhqapi.js")
	    	.run();
	}
	@Test(groups={"blockedByBug-1063480"})
    public void bz1063480() {
        createJSRunner("bugs/bz1063480.js")
            .addDepends("/rhqapi.js")
            .addDepends("jon-common.js")
            .run();
    }
}
