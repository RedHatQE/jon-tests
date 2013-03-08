package com.redhat.qe.jon.clitest.tests.stress;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class StressCliTest extends CliTest {

	@Test(groups={"blockedByBug-829399"})
	public void deployLargeWarTest() throws IOException, CliTasksException{
		runJSfile("stress/test_LargeWarDeploy.js",
				"args-style=named bundle=/tmp/rh_dep1.war",
				"Login successful",
				null,
				"/bundles/byebye.war",
				"/tmp/byebye.war");
	}
	
	@Test(groups={"blockedByBug-829399"})
	public void deployLargeBundleTest() throws IOException, CliTasksException{
		runJSfile("stress/test_LargeBundleDeploy.js",
				"args-style=named bundle=/tmp/large_bundle.zip",
				"Login successful",
				null,
				"/bundles/byebye.war",
				"/tmp/byebye.war");
	}
}
