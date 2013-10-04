package com.redhat.qe.jon.clitest.tests.stress;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class LargeFilesTest extends CliEngine {

	@Test(groups={"blockedByBug-887861"})
	public void deployLargeWarTest() {
		createJSRunner("stress/test_LargeWarDeploy.js").
			withArg("bundle", "/tmp/rh_dep1.war").
			resourceSrcs("/bundles/byebye.war").
			resourceDests("/tmp/byebye.war").
			run();
	}
	
	@Test(groups={"blockedByBug-887861"})
	public void deployLargeBundleTest(){
		createJSRunner("stress/test_LargeBundleDeploy.js").
			withArg("bundle", "/tmp/large_bundle.zip").
			resourceSrcs("/bundles/byebye.war").
			resourceDests("/tmp/byebye.war").
			run();
	}
}
