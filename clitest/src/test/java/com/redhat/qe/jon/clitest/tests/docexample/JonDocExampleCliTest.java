package com.redhat.qe.jon.clitest.tests.docexample;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class JonDocExampleCliTest extends CliEngine {
	

	@Test
	public void searchTest(){
		createJSRunner("docExample/jon/short/searchExamples.js").
			dependsOn("rhqapi.js").
			run();
	}
	
	@Test
	public void gettingIdForObjectTest(){
		createJSRunner("docExample/jon/short/gettingIdForObject.js").
			dependsOn("rhqapi.js").
			run();
	}
	
	@Test
	public void settingMethodVariableToStringTest(){
		createJSRunner("docExample/jon/short/settingMethodVariableToString.js").
			run();
	}
	
	@Test
	public void creatingResGroupsAndAddingMembersTest(){
		createJSRunner("docExample/jon/short/creatingResGroupsAndAddingMembers.js").
			run();
	}
	
	@Test
	public void viewingPluginConfForResTypeTest(){
		createJSRunner("docExample/jon/short/viewingPluginConfForResType.js").
			addExpect("ConfigurationDefinition").
			addExpect("name: Linux").
			run();
	}
	
	@Test
	public void viewingConfPropFroResTypeTest(){
		createJSRunner("docExample/jon/short/viewingConfPropFroResType.js").
			addExpect("ConfigurationDefinition").
			addExpect("name: RHQ Agent").
			run();
	}
	
	@Test
	public void viewingResConfSettingsTest(){
		createJSRunner("docExample/jon/short/viewingResConfSettings.js").
			dependsOn("rhqapi.js").
			addExpect("rhq.agent.server.bind-port = 7080").
			run();
	}
	
	@Test
	public void changingSimplePropertyTest(){
		createJSRunner("docExample/jon/short/changingSimpleProperty.js").
			run();
	}
	
	@Test
	public void immediateOperationTest(){
		createJSRunner("docExample/jon/short/immediateOperation.js").
			addExpect("Invoking operation executeAvailabilityScan").
			run();
	}
	
	@Test
	public void scheduledOperationTest(){
		createJSRunner("docExample/jon/short/scheduledOperation.js").
			dependsOn("rhqapi.js").
			run();
	}
	
	@Test
	public void retrievingResultsOperationTest(){
		createJSRunner("docExample/jon/short/retrievingResultsOperation.js").
			dependsOn("rhqapi.js").
			addExpect("init").
			run();
	}
	
	@Test
	public void viewingOpHistoryTest(){
		createJSRunner("docExample/jon/short/viewingOpHistory.js").
			dependsOn("rhqapi.js").
			run();
	}
	
	@Test
	public void currentAvailabilityTest(){
		createJSRunner("docExample/jon/short/currentAvailability.js").
			run();
	}
	
	@Test
	public void gettingSpecificMetricsTest(){
		createJSRunner("docExample/jon/short/gettingSpecificMetrics.js").
			run();
	}
	
	@Test
	public void exportingAllMetricsDefForLinuxTest(){
		createJSRunner("docExample/jon/short/exportingAllMetricsDefForLinux.js").
			run();
	}
	
	@Test
	public void newRoleTest(){
		createJSRunner("docExample/jon/short/newRole.js").
			run();
	}
	
	@Test(dependsOnMethods = { "newRoleTest" })
	public void creatingUserAndRolesTest(){
		createJSRunner("docExample/jon/short/creatingUserAndRoles.js").
			run();
	}
}
