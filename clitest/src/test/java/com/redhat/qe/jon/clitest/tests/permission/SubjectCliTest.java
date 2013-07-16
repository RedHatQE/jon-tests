package com.redhat.qe.jon.clitest.tests.permission;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class SubjectCliTest extends CliTest {
	
	@Test
	public void subjectTest() throws IOException, CliTasksException{
		runJSfile("permissions/subjects.js");
	}
	
	@Test
	public void subjectUserCreateTest() throws IOException, CliTasksException{
		runJSfile("permissions/subjectsusercreate.js");
	}

}
