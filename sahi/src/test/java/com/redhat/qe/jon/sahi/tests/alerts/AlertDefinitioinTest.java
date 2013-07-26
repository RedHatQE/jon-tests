package com.redhat.qe.jon.sahi.tests.alerts;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.OnAgentSahiTestScript;
import com.redhat.qe.jon.sahi.base.administration.AdministrationPage;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionCreator;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionsPage;

/**
 * Purpose of this simple test is to show and test abstraction layer for alert definitions.
 * 
 * @author fbrychta
 *
 */
public class AlertDefinitioinTest extends OnAgentSahiTestScript{
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private Resource agent;
	private static final String ALERT_DEF_NAME1 = "test alert definition1";
	private static final String ALERT_DEF_NAME2 = "test alert definition2";
	
	private static final String REPOSITORY_NAME = "Server CLI repository";

	@Test
	public void createAlarmDefTest(){
		// create a new empty repository
		AdministrationPage.Repositories repos = new AdministrationPage.Repositories(sahiTasks);
		repos.navigate().deleteRepository(REPOSITORY_NAME);
		repos.createNewRepository(REPOSITORY_NAME);
		
		
		agent = new Resource(sahiTasks,agentName,"RHQ Agent");
		AlertDefinitionsPage alertDefPage = agent.alerts().alertDefinitionsPage();

		
		// clean old alert definitions
		alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME1);
		alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME2);
		
		
		// prepare conditions
		AlertDefinitionCreator.Condition condition = new AlertDefinitionCreator.Condition("Measurement Baseline Threshold");
		condition.addField("Metric :", "Number of Commands In Queue", AlertDefinitionCreator.Field.FieldType.COMBO);
		condition.addField("Comparator :", "> (Greater Than)", AlertDefinitionCreator.Field.FieldType.COMBO);
		condition.addField("Baseline Percentage :", "10", AlertDefinitionCreator.Field.FieldType.TEXT);
		condition.addField("Baseline :", "Maximum", AlertDefinitionCreator.Field.FieldType.COMBO);
		
		AlertDefinitionCreator.Condition condition2 = new AlertDefinitionCreator.Condition("Measurement Value Range");
		condition2.addField("Metric :", "JVM Total Memory", AlertDefinitionCreator.Field.FieldType.COMBO);
		condition2.addField("Comparator :", "Inside, inclusive", AlertDefinitionCreator.Field.FieldType.COMBO);
		condition2.addField("Low Value :", "10", AlertDefinitionCreator.Field.FieldType.TEXT);
		condition2.addField("High Value :", "50", AlertDefinitionCreator.Field.FieldType.TEXT);
		
		// add two new alert definitions 
		alertDefPage.getAlertDefCreator(ALERT_DEF_NAME1).
			withGeneralProp("This is test alert definition",AlertDefinition.Priority.High,false).
			addCondition(condition).
			addCondition(condition2).
			setConditionOperator(AlertDefinitionCreator.Condition.Operator.ALL).
			addCliScriptNotification("rhqadmin", "rhqadmin", REPOSITORY_NAME, "/serverCliScripts/enableMetricOnPlatforms.js", "1.0").
			save();
		
		alertDefPage.navigateTo().getAlertDefCreator(ALERT_DEF_NAME2).
			withGeneralProp("mydes",AlertDefinition.Priority.Medium,true).
			addCondition(condition).
			setConditionOperator(AlertDefinitionCreator.Condition.Operator.ALL).
			addCliScriptNotification("rhqadmin", "rhqadmin", REPOSITORY_NAME, "enableMetricOnPlatforms.js (rhqadmin:1.0)").
			save();
		
		
		// checking first alarm definition
		ArrayList<AlertDefinition> alertDefs = alertDefPage.navigateTo().getAlertDefinitions(ALERT_DEF_NAME1);
		Assert.assertTrue(alertDefs.size() > 0,"Previously creted alert definition with name "+
				ALERT_DEF_NAME1+", was not found!!");
		assertAlerDefinition(alertDefs.get(0), "This is test alert definition", false, 
				AlertDefinition.Priority.High, "", "N/A");
		
		// checking second alarm definition
		alertDefs = alertDefPage.getAlertDefinitions(ALERT_DEF_NAME2);
		Assert.assertTrue(alertDefs.size() > 0,"Previously creted alert definition with name "+
				ALERT_DEF_NAME2+", was not found!!");
		assertAlerDefinition(alertDefs.get(0), "mydes", true, 
				AlertDefinition.Priority.Medium, "", "N/A");
	}
	
	
	@Test(dependsOnMethods="createAlarmDefTest",priority=0)
	public void enableDisableAlarmDefTest(){
		AlertDefinitionsPage alertDefPage = agent.alerts().alertDefinitionsPage();
	
		// disable both alert definitions
		alertDefPage.disableAlertDefinition(ALERT_DEF_NAME1);
		alertDefPage.disableAlertDefinition(ALERT_DEF_NAME2);
		
		assertAlertDefDisabled(ALERT_DEF_NAME1);
		assertAlertDefDisabled(ALERT_DEF_NAME2);
		
		
		// enable both alert definitions
		alertDefPage.enableAlertDefinition(ALERT_DEF_NAME1);
		alertDefPage.enableAlertDefinition(ALERT_DEF_NAME2);
		
		assertAlertDefEnabled(ALERT_DEF_NAME1);
		assertAlertDefEnabled(ALERT_DEF_NAME2);
	}
	
	
	@Test(dependsOnMethods="createAlarmDefTest",priority=2)
	public void deleteAlarmDefTest(){
		AlertDefinitionsPage alertDefPage = agent.alerts().alertDefinitionsPage();
		
		// delete both alert definitions
		alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME1);
		alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME2);
		
		// check that definitions are deleted
		ArrayList<AlertDefinition> alertDefs = alertDefPage.getAlertDefinitions(ALERT_DEF_NAME1);
		Assert.assertTrue(alertDefs.size() == 0,"Alert definition with name "+ALERT_DEF_NAME1+
				" was found, but it should be deleted!!");
		alertDefs = alertDefPage.getAlertDefinitions(ALERT_DEF_NAME2);
		Assert.assertTrue(alertDefs.size() == 0,"Alert definition with name "+ALERT_DEF_NAME2+
				" was found, but it should be deleted!!");
	}

	
	private void assertAlerDefinition(AlertDefinition alertDef,String expectedDescription,boolean expectedAvail,
			AlertDefinition.Priority priority,String parent,String protectedField){
		log.fine("Checking alert definition with name " + alertDef.getName());
		Assert.assertTrue(alertDef.getDescription().equals(expectedDescription),"Expected description is: "+
				expectedDescription+", but actual is: "+alertDef.getDescription());
		Assert.assertTrue(alertDef.isEnabled() == expectedAvail,"Expected availability is: "+
				expectedAvail+", but actual is: "+alertDef.isEnabled());
		Assert.assertTrue(alertDef.getPriority().equals(priority),"Expected priority is: "+
				priority+", but actual is: "+alertDef.getPriority());
		Assert.assertTrue(alertDef.getParent().equals(parent),"Expected parent is: "+
				parent+", but actual is: "+alertDef.getParent());
		Assert.assertTrue(alertDef.getProtectedField().equals(protectedField),"Expected protectedField is: "+
				protectedField+", but actual is: "+alertDef.getProtectedField());
	}
	
	private void assertAlertDefEnabled(String alertDefName){
		ArrayList<AlertDefinition> alertDefs = agent.alerts().alertDefinitionsPage().getAlertDefinitions(alertDefName);
		Assert.assertTrue(alertDefs.size() > 0,"Alert definition with name "+alertDefName+" was not found!!");
		Assert.assertTrue(alertDefs.get(0).isEnabled(), 
				"Alert definition with name "+alertDefName+" is expected to be enabled!!");
	}
	
	private void assertAlertDefDisabled(String alertDefName){
		ArrayList<AlertDefinition> alertDefs = agent.alerts().alertDefinitionsPage().getAlertDefinitions(alertDefName);
		Assert.assertTrue(alertDefs.size() > 0,"Alert definition with name "+alertDefName+" was not found!!");
		Assert.assertTrue(!alertDefs.get(0).isEnabled(), 
				"Alert definition with name "+alertDefName+" is expected to be disabled!!");
	}
}
