package com.redhat.qe.jon.clitest.tests.bz;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class BzCliTest extends CliTest {

	@Test(groups={"blockedByBug-814579"})
	public void bz814579Test() throws IOException, CliTasksException{
		runJSfile("bugs/bug814579.js","Login successful,ResourceManagerBean");
	}
	
	@Test(groups={"blockedByBug-906754"})
	public void bz906754Test() throws IOException, CliTasksException{
		runJSfile("bugs/bug906754.js");
	}
	
	@Test(groups={"blockedByBug-907897"})
	public void bz907897Test() throws IOException, CliTasksException{
		runJSfile("bugs/bug907897.js");
	}
}
