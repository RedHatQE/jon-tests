package com.redhat.qe.jon.sahi.tests.alerts;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.OnAgentSahiTestScript;
import com.redhat.qe.jon.sahi.base.administration.AdministrationPage;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionsPage;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.AvailChangeCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.AvailDurationCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.Condition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.EventDetectionCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.GenCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasAbsValTresholdCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasBaselineTresholdCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasValueChangeCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasValueRangeCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.OperationExecutionCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.ResourceConfigChangeCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.TraitValueChangeCondition;

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
	
	// conditions
	AvailChangeCondition availChangeCond;
	AvailDurationCondition availDurCond;
	EventDetectionCondition eventDetectionCond;
	MeasAbsValTresholdCondition measAbsValTresholdCond;
	MeasBaselineTresholdCondition measBaselineTresholdCond;
	MeasValueChangeCondition measValChangeCond;
	MeasValueRangeCondition measValueRangeCond;
	OperationExecutionCondition operationExecutionCond;
	ResourceConfigChangeCondition resourceConfigChangeCond;
	TraitValueChangeCondition traitValueChangeCond;

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
		
		
		// prepare different types of conditions
		availChangeCond = new AvailChangeCondition(sahiTasks);
		availChangeCond.setAvailChangeType("Goes unknown");
		
		availDurCond = new AvailDurationCondition(sahiTasks);
		availDurCond.setAvailState("Stays Not Up").setDuration("10");
		
		eventDetectionCond = new EventDetectionCondition(sahiTasks);
		eventDetectionCond.setEventSeverity("Warn").setRegularExpression("10");
		
		measAbsValTresholdCond = new MeasAbsValTresholdCondition(sahiTasks);
		measAbsValTresholdCond.setMetric("Number of Commands Sent but Failed").
			setComparator(MeasAbsValTresholdCondition.Comparator.EQUAL).
			setMetricValue("12");
		
		measBaselineTresholdCond = new MeasBaselineTresholdCondition(sahiTasks);
		measBaselineTresholdCond.setComparator(MeasAbsValTresholdCondition.Comparator.LESS_THEN);
		measBaselineTresholdCond.setMetric("Up Time").
			setBaselinePercentage("12").
			setBaseline(MeasBaselineTresholdCondition.Baseline.Maximum);
		
		measValChangeCond = new MeasValueChangeCondition(sahiTasks);
		measValChangeCond.setMetric("Number of Commands Received but Failed per Minute");
		
		measValueRangeCond = new MeasValueRangeCondition(sahiTasks);
		measValueRangeCond.setMetric("Number of Agent Restarts").
			setComparator(MeasValueRangeCondition.RangeComparator.OUT_EXCLUSIVE).
			setLowValue("12").
			setHighValue("20");
		
		// generic condition
		Condition condition2 = new GenCondition("Measurement Value Range",sahiTasks);
		condition2.addField("Metric :", "JVM Total Memory", AlertDefinitionEditor.Field.FieldType.COMBO);
		condition2.addField("Comparator :", "Inside, inclusive", AlertDefinitionEditor.Field.FieldType.COMBO);
		condition2.addField("Low Value :", "10", AlertDefinitionEditor.Field.FieldType.TEXT);
		condition2.addField("High Value :", "50", AlertDefinitionEditor.Field.FieldType.TEXT);
		
		operationExecutionCond = new OperationExecutionCondition(sahiTasks);
		operationExecutionCond.setValue("Get Info On All Plugins").setStatus("Success");
		
		resourceConfigChangeCond = new ResourceConfigChangeCondition(sahiTasks);
		
		traitValueChangeCond = new TraitValueChangeCondition(sahiTasks);
		traitValueChangeCond.setTrait("SIGAR Version").setRegularExpression("regexp");
		
		// add two new alert definitions 
		alertDefPage.createAlarmDefinition(ALERT_DEF_NAME1).
			setGeneralProp("This is test alert definition",AlertDefinition.Priority.High,false).
			addCondition(availChangeCond).
			addCondition(availDurCond).
			addCondition(eventDetectionCond).
			addCondition(measAbsValTresholdCond).
			addCondition(measBaselineTresholdCond).
			addCondition(measValChangeCond).
			addCondition(measValueRangeCond).
			addCondition(operationExecutionCond).
			addCondition(resourceConfigChangeCond).
			addCondition(traitValueChangeCond).
			setConditionOperator(Condition.Operator.ALL).
			addCliScriptNotification("rhqadmin", "rhqadmin", REPOSITORY_NAME, "/serverCliScripts/enableMetricOnPlatforms.js", "1.0").
			save();
		
		alertDefPage.navigateTo().createAlarmDefinition(ALERT_DEF_NAME2).
			setGeneralProp("mydes",AlertDefinition.Priority.Medium,true).
			addCondition(traitValueChangeCond).
			setConditionOperator(Condition.Operator.ALL).
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
		
		assertAlertDefDisabled(ALERT_DEF_NAME1,alertDefPage);
		assertAlertDefDisabled(ALERT_DEF_NAME2,alertDefPage);
		
		
		// enable both alert definitions
		alertDefPage.enableAlertDefinition(ALERT_DEF_NAME1);
		alertDefPage.enableAlertDefinition(ALERT_DEF_NAME2);
		
		assertAlertDefEnabled(ALERT_DEF_NAME1,alertDefPage);
		assertAlertDefEnabled(ALERT_DEF_NAME2,alertDefPage);
	}
	
	@Test(dependsOnMethods="createAlarmDefTest",priority=1,
			description="Checks that condition edit dialog is correcttly filed with values given during " +
					"creation of the conditon.",
			groups={"blockedByBug-994904"})
	public void checkConditionsTest(){
		AlertDefinitionsPage alertDefPage = agent.alerts().alertDefinitionsPage();
		AlertDefinitionEditor alertDefEditor = alertDefPage.editAlertDefinition(ALERT_DEF_NAME1);
		
		
		AvailChangeCondition availChangeConditionParsed = alertDefEditor.getCondition("Availability [Goes unknown]",
				AvailChangeCondition.class);
		Assert.assertTrue(availChangeConditionParsed.getAvailChangeType().equals(availChangeCond.getAvailChangeType()),
				"Expected avail change type is: "+availChangeCond.getAvailChangeType()+
				", but actual is:"+availChangeConditionParsed.getAvailChangeType());
		
		
		AvailDurationCondition availDurCondParsed = alertDefEditor.getCondition("Availability Duration [Stays Not Up For 10 m]",
				AvailDurationCondition.class);
		Assert.assertTrue(availDurCondParsed.getAvailState().equals(availDurCond.getAvailState()),
				"Expected avail duration state is: "+availDurCond.getAvailState()+
				", but actual is:"+availDurCondParsed.getAvailState());
		Assert.assertTrue(availDurCondParsed.getDuration().equals(availDurCond.getDuration()),
				"Expected avail duration is: "+availDurCond.getDuration()+
				", but actual is:"+availDurCondParsed.getDuration());
		
		
		EventDetectionCondition eventDetectionCondParsed = alertDefEditor.
				getCondition("Event Detection [WARN] with event source matching '10'",EventDetectionCondition.class);
		Assert.assertTrue(eventDetectionCondParsed.getEventSeverity().equals(eventDetectionCond.getEventSeverity()),
				"Expected event severity is: "+eventDetectionCond.getEventSeverity()+
				", but actual is:"+eventDetectionCondParsed.getEventSeverity());
		Assert.assertTrue(eventDetectionCondParsed.getRegularExpression().equals(eventDetectionCond.getRegularExpression()),
				"Expected regexpr is: "+eventDetectionCond.getRegularExpression()+
				", but actual is:"+eventDetectionCondParsed.getRegularExpression());
		
		
		MeasAbsValTresholdCondition measAbsValTresholdCondParsed = alertDefEditor.
				getCondition("Metric Value Threshold [Number of Commands Sent but Failed = 12.0]",MeasAbsValTresholdCondition.class);
		Assert.assertTrue(measAbsValTresholdCondParsed.getMetric().equals(measAbsValTresholdCond.getMetric()),
				"Expected metric is:"+measAbsValTresholdCond.getMetric()+", but actual is:"+
				measAbsValTresholdCondParsed.getMetric());
		Assert.assertTrue(measAbsValTresholdCondParsed.getComparator().equals(measAbsValTresholdCond.getComparator()),
				"Expected comparator is: "+measAbsValTresholdCond.getComparator()+", but actual is:"+
				measAbsValTresholdCondParsed.getComparator());
		Assert.assertTrue(measAbsValTresholdCondParsed.getMetricValue().trim().equals(measAbsValTresholdCond.getMetricValue()),
				"Expected metric value is: "+measAbsValTresholdCond.getMetricValue()+", but actual is:"+
				measAbsValTresholdCondParsed.getMetricValue());
		
		
		MeasBaselineTresholdCondition measBaselineTresholdCondParsed = alertDefEditor.
				getCondition("Metric Value Baseline [Up Time < 12.0 % of max]",MeasBaselineTresholdCondition.class);
		Assert.assertTrue(measBaselineTresholdCondParsed.getMetric().equals(measBaselineTresholdCond.getMetric()),
				"Expected metric is: "+measBaselineTresholdCond.getMetric()+", but actual is:"+
						measBaselineTresholdCondParsed.getMetric());
		Assert.assertTrue(measBaselineTresholdCondParsed.getComparator().equals(measBaselineTresholdCond.getComparator()),
				"Expected comparator is: "+measBaselineTresholdCond.getComparator()+", but actual is:"+
						measBaselineTresholdCondParsed.getComparator());
		Assert.assertTrue(measBaselineTresholdCondParsed.getBaselinePercentage().trim().
				equals(measBaselineTresholdCond.getBaselinePercentage()),
				"Expected baseline percentage is: "+measBaselineTresholdCond.getBaselinePercentage()+", but actual is:"+
						measBaselineTresholdCondParsed.getBaselinePercentage());
		Assert.assertTrue(measBaselineTresholdCondParsed.getBaseline().equals(measBaselineTresholdCond.getBaseline()),
				"Expected baseline is: "+measBaselineTresholdCond.getBaseline()+", but actual is:"+
						measBaselineTresholdCondParsed.getBaseline());
		
		
		MeasValueChangeCondition measValueChangeCondParsed = alertDefEditor.
				getCondition("Metric Value Change [Number of Commands Received but Failed  per Minute]",
						MeasValueChangeCondition.class);
		Assert.assertTrue(measValueChangeCondParsed.getMetric().equals(measValChangeCond.getMetric()),
				"Expected metric is: "+measValChangeCond.getMetric()+", but actual is:"+
						measValueChangeCondParsed.getMetric());
		
		
		MeasValueRangeCondition measValueRangeCondParsed = alertDefEditor.
				getCondition("Metric Value Range: [Number of Agent Restarts] outside [12.0] and [20.0], exclusive",
						MeasValueRangeCondition.class);
		Assert.assertTrue(measValueRangeCondParsed.getMetric().equals(measValueRangeCond.getMetric()),
				"Expected metric is: "+measValueRangeCond.getMetric()+", but actual is:"+
						measValueRangeCondParsed.getMetric());
		Assert.assertTrue(measValueRangeCondParsed.getComparator().equals(measValueRangeCond.getComparator()),
				"Expected comparator is: "+measValueRangeCond.getComparator()+", but actual is:"+
						measValueRangeCondParsed.getComparator());
		Assert.assertTrue(measValueRangeCondParsed.getLowValue().equals(measValueRangeCond.getLowValue()),
				"Expected low value is: "+measValueRangeCond.getLowValue()+", but actual is:"+
						measValueRangeCondParsed.getLowValue());
		Assert.assertTrue(measValueRangeCondParsed.getHighValue().equals(measValueRangeCond.getHighValue()),
				"Expected high value is: "+measValueRangeCond.getHighValue()+", but actual is:"+
						measValueRangeCondParsed.getHighValue());
		
		
		OperationExecutionCondition operationExecutionCondParsed = alertDefEditor.
				getCondition("Operation Execution [retrieveAllPluginInfo] with result status [SUCCESS]",
					OperationExecutionCondition.class);
		Assert.assertTrue(operationExecutionCondParsed.getValue().equals(operationExecutionCond.getValue()),
				"Expected operations is: "+operationExecutionCond.getValue()+", but actual is:"+
						operationExecutionCondParsed.getValue());
		Assert.assertTrue(operationExecutionCondParsed.getStatus().equals(operationExecutionCond.getStatus()),
				"Expected status is: "+operationExecutionCond.getStatus()+", but actual is:"+
						operationExecutionCondParsed.getStatus());
		
		// there are no fields to check, checking just condition existence is enough
		alertDefEditor.getCondition("Resource Configuration Change",ResourceConfigChangeCondition.class);
		
		
		TraitValueChangeCondition traitValueChangeCondParsed = alertDefEditor.
				getCondition("Trait Change [SIGAR Version] with trait value matching 'regexp'",TraitValueChangeCondition.class);
		Assert.assertTrue(traitValueChangeCondParsed.getTrait().equals(traitValueChangeCond.getTrait()),
				"Expected trait is: "+traitValueChangeCond.getTrait()+
				", but actual is:"+traitValueChangeCondParsed.getTrait());
		Assert.assertTrue(traitValueChangeCondParsed.getRegularExpression().equals(traitValueChangeCond.getRegularExpression()),
				"Expected regexpr is: "+traitValueChangeCond.getRegularExpression()+
				", but actual is:"+traitValueChangeCondParsed.getRegularExpression());
		
		alertDefEditor.cancel();
	}

	@Test(dependsOnMethods="createAlarmDefTest",priority=2)
	public void editAlarmDefTest(){
		AlertDefinitionsPage alertDefPage = agent.alerts().alertDefinitionsPage();
		AlertDefinitionEditor alertDefEditor = alertDefPage.editAlertDefinition(ALERT_DEF_NAME1);
		
		alertDefEditor.setGeneralProp("new desc", AlertDefinition.Priority.Low, true);
		
		// prepare condition
		OperationExecutionCondition operationExecutionCond = new OperationExecutionCondition(sahiTasks);
		operationExecutionCond.setValue("Get Info On All Plugins").setStatus("In Progress");

		alertDefEditor.editCondition("Resource Configuration Change",operationExecutionCond);
		
		alertDefEditor.deleteCondition("Event Detection [WARN] with event source matching '10'");
		
		alertDefEditor.save();
		
		
		
		// checking updated alarm definition
		ArrayList<AlertDefinition> alertDefs = alertDefPage.navigateTo().getAlertDefinitions(ALERT_DEF_NAME1);
		Assert.assertTrue(alertDefs.size() > 0,"Previously edite alert definition with name "+
				ALERT_DEF_NAME1+", was not found!!");
		assertAlerDefinition(alertDefs.get(0), "new desc", true, 
				AlertDefinition.Priority.Low, "", "N/A");
		
		alertDefEditor = alertDefPage.editAlertDefinition(ALERT_DEF_NAME1);
		Assert.assertFalse(alertDefEditor.doesConditionExist("Event Detection [WARN] with event source matching '10'"),
				"Removed condition is still visible on the page.");
		
		OperationExecutionCondition operationExecutionCondParsed = alertDefEditor.
				getCondition("Operation Execution [retrieveAllPluginInfo] with result status [INPROGRESS]",
					OperationExecutionCondition.class);
		Assert.assertTrue(operationExecutionCondParsed.getValue().equals(operationExecutionCond.getValue()),
				"Expected operations is: "+operationExecutionCond.getValue()+", but actual is:"+
						operationExecutionCondParsed.getValue());
		Assert.assertTrue(operationExecutionCondParsed.getStatus().equals(operationExecutionCond.getStatus()),
				"Expected status is: "+operationExecutionCond.getStatus()+", but actual is:"+
						operationExecutionCondParsed.getStatus());
		
		alertDefEditor.cancel();
		
	}
	
	@Test(dependsOnMethods="createAlarmDefTest",priority=4)
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
	
	private void assertAlertDefEnabled(String alertDefName, AlertDefinitionsPage alertDefP){
		ArrayList<AlertDefinition> alertDefs = alertDefP.getAlertDefinitions(alertDefName);
		Assert.assertTrue(alertDefs.size() > 0,"Alert definition with name "+alertDefName+" was not found!!");
		Assert.assertTrue(alertDefs.get(0).isEnabled(), 
				"Alert definition with name "+alertDefName+" is expected to be enabled!!");
	}
	
	private void assertAlertDefDisabled(String alertDefName,AlertDefinitionsPage alertDefP){
		ArrayList<AlertDefinition> alertDefs = alertDefP.getAlertDefinitions(alertDefName);
		Assert.assertTrue(alertDefs.size() > 0,"Alert definition with name "+alertDefName+" was not found!!");
		Assert.assertTrue(!alertDefs.get(0).isEnabled(), 
				"Alert definition with name "+alertDefName+" is expected to be disabled!!");
	}
}
