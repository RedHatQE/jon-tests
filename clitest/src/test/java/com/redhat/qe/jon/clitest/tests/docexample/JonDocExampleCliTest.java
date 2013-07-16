package com.redhat.qe.jon.clitest.tests.docexample;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class JonDocExampleCliTest extends CliTest {
	

	@Test
	public void searchTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/searchExamples.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
	
	@Test
	public void gettingIdForObjectTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/gettingIdForObject.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
	
	@Test
	public void settingMethodVariableToStringTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/settingMethodVariableToString.js");
	}
	
	@Test
	public void creatingResGroupsAndAddingMembersTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/creatingResGroupsAndAddingMembers.js");
	}
	
	@Test
	public void viewingPluginConfForResTypeTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/viewingPluginConfForResType.js",
				"Login successful,ConfigurationDefinition,name: Linux");
	}
	
	@Test
	public void viewingConfPropFroResTypeTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/viewingConfPropFroResType.js",
				"Login successful,ConfigurationDefinition:,name: RHQ Agent");
	}
	
	@Test
	public void viewingResConfSettingsTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/viewingResConfSettings.js",
				null,
				"Login successful,rhq.agent.server.bind-port = 7080",
				"rhqapi.js");
	}
	
	@Test
	public void changingSimplePropertyTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/changingSimpleProperty.js");
	}
	
	@Test
	public void immediateOperationTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/immediateOperation.js",
				"Login successful,Invoking operation executeAvailabilityScan");
	}
	
	@Test
	public void scheduledOperationTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/scheduledOperation.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
	
	@Test
	public void retrievingResultsOperationTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/retrievingResultsOperation.js",
				null,
				"Login successful,init",
				"rhqapi.js");
	}
	
	@Test
	public void viewingOpHistoryTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/viewingOpHistory.js",
				null,
				"Login successful",
				"rhqapi.js");
	}
	
	@Test
	public void currentAvailabilityTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/currentAvailability.js");
	}
	
	@Test
	public void gettingSpecificMetricsTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/gettingSpecificMetrics.js");
	}
	
	@Test
	public void exportingAllMetricsDefForLinuxTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/exportingAllMetricsDefForLinux.js");
	}
	
	@Test
	public void newRoleTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/newRole.js");
	}
	
	@Test(dependsOnMethods = { "newRoleTest" })
	public void creatingUserAndRolesTest()throws IOException, CliTasksException{
		runJSfile("docExample/jon/short/creatingUserAndRoles.js");
	}

}
