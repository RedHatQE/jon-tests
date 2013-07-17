package com.redhat.qe.jon.clitest.tests.docexample;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class RhqDocExampleCliTest extends CliEngine {
	
	@Test(priority=0)
	public void displayingOutputTest() {
		createJSRunner("docExample/rhq/displayingOutputExample.js").
			addExpect("name=Memory Subsystem").
			addExpect("name: Memory Subsystem").
			run();
	}
	
	@Test
	public void echoArgumentsTest() {
		createJSRunner("docExample/rhq/echo_args.js").
			withArg("x", "1").
			withArg("y", "2").
			addExpect("named args...").
			addExpect("x = 1").
			addExpect("y = 2").
			run();
	}
	
	@Test(groups={"blockedByBug-924304"})
	public void proxyTest() {
		createJSRunner("docExample/rhq/proxyExample.js").
			dependsOn("rhqapi.js").
			addExpect("OSName:").
			addExpect("Invoking operation viewProcessList").
			addExpect("Invoking operation updateAllPlugins").
			addExpect("rhq.agent.plugins.directory =").
			run();
	}
	
	@Test(priority=1)
	public void criteriaApiTipsTest(){
		createJSRunner("docExample/rhq/criteriaApiTips.js").
			addDepends("rhqapi.js,rhqapi/config.js").
			run();
	}

}
