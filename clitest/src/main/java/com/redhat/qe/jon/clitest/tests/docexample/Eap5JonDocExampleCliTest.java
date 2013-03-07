package com.redhat.qe.jon.clitest.tests.docexample;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class Eap5JonDocExampleCliTest extends CliTest {

	@Test
	public void gettingDataForSingleAndMultipleResTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/gettingDataForSingleAndMultipleRes.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
	
	@Test
	public void creatingContentBackedResTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/creatingContentBackedRes.js",
				"--args-style=named deployment=/tmp/MiscBeans-3.2.2.ear",
				"Login successful",
				"rhqapi.js",
				"/deployments/MiscBeans-3.2.2.ear",
				"/tmp/MiscBeans-3.2.2.ear");
		
	}
	
	@Test(groups={"blockedByBug-851145"})
	public void updatingContentBackedResTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/updatingContentBackedRes.js",
				"--args-style=named deployment=/tmp/MiscBeans-3.2.5.ear",
				"Login successful",
				"rhqapi.js",
				"/deployments/MiscBeans-3.2.5.ear",
				"/tmp/MiscBeans-3.2.5.ear");
		
	}
	
	@Test
	public void deletingContentBackedResTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/deletingContentBackedRes.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
	
	@Test
	public void startingArrayTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/startingArray.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
}
