package com.redhat.qe.jon.clitest.tests.permission;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class SubjectCliTest extends CliEngine {
	
	@Test
	public void subjectTest() {
		createJSRunner("permissions/subjects.js").
		run();
	}
	
	@Test
	public void subjectUserCreateTest() {
		createJSRunner("permissions/subjectsusercreate.js").
		run();
	}

}
