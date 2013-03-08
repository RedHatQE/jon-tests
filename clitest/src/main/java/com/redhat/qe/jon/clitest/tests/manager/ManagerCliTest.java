package com.redhat.qe.jon.clitest.tests.manager;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class ManagerCliTest extends CliTest {
	
	@Test
	public void systemManagerTest() throws IOException, CliTasksException{
		runJSfile("managers/systemmanager.js");
	}
	
	@Test
	public void resourceTypeManagerTest() throws IOException, CliTasksException{
		runJSfile("managers/resourcetypemanager.js");
	}
	
	@Test
	public void dataAccessManagerTest() throws IOException, CliTasksException{
		runJSfile("managers/dataaccessmanager.js");
	}
	
	@Test
	public void repoManagerTest() throws IOException, CliTasksException{
		runJSfile("managers/repo.js");
	}
	
	@Test
	public void operationManagerTest() throws IOException, CliTasksException{
		runJSfile("managers/viewProcessList.js");
	}
	
	@Test
	public void measurementDefinitionManagerTest() throws IOException, CliTasksException{
		runJSfile("managers/measurementdefinitions.js");
	}

}
