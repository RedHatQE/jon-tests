package com.redhat.qe.jon.clitest.tests.docexample;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class RhqDocExampleCliTest extends CliTest {
	
	@Test
	public void displayingOutputTest() throws IOException, CliTasksException{
		runJSfile("docExample/rhq/displayingOutputExample.js",
				"Login successful,name=Memory Subsystem,name: Memory Subsystem");
	}
	
	@Test
	public void echoArgumentsTest() throws IOException, CliTasksException{
		runJSfile("docExample/rhq/echo_args.js",
				"--args-style=named x=1 y=2",
				"Login successful, named args...,x = 1,y = 2");
	}
	
	@Test(groups={"blockedByBug-924304"})
	public void proxyTest() throws IOException, CliTasksException{
		runJSfile("docExample/rhq/proxyExample.js",
				null,
				"Login successful,OSName:,Invoking operation viewProcessList,Invoking operation updateAllPlugins,rhq.agent.plugins.directory =",
				"rhqapi.js");
	}
	
	@Test
	public void criteriaApiTipsTest(){
		createJSRunner("docExample/rhq/criteriaApiTips.js")
		.addDepends("rhqapi.js,rhqapi/config.js")
		.run();
	}

}
