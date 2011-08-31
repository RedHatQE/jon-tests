package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.*;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.ComboBox;
import com.redhat.qe.jon.sahi.base.Common;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 17, 2011
 */

public class AlertTest extends SahiTestScript{
	private static Logger _logger = Logger.getLogger(AlertTest.class.getName());
	
	public static String RESOURCE_NAME 			= "resourceName";
	public static String ALERT_NAME 			= "alertName";
	public static String ALERT_DESCRIPTION 		= "alertDescription";
	public static String CONDITION_DROPDOWNS 	= "conditionDropDowns";
	public static String CONDITION_TEXTBOX 		= "conditionTextbox";
	public static String NOTIFICATION_TYPE 		= "notificationType";
	public static String NOTIFICATION_DATA 		= "notificationData";
	
	
	public void gotoAlertDefinationPage(String resourceName){
		sahiTasks.link("Inventory").click();
		String[] resourceType = resourceName.split("=");
		if(resourceType.length>1){
			sahiTasks.cell(resourceType[0].trim()).click();
			sahiTasks.textbox("SearchPatternField").setValue(resourceType[1].trim());
			sahiTasks.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);");
		}else{
			sahiTasks.cell("Servers").click();
		}		
		sahiTasks.link(resourceType[1].trim()).click();
		sahiTasks.cell("Alerts").click();
		sahiTasks.xy(sahiTasks.cell("Definitions"), 3, 3).click();
	}
	
	public void selectConditionComboBoxes(String options){
		/*String comboBoxIdentifier = "selectItemText";
		int indexStartFrom = 3;
		String[] optionArray = Common.getCommaToArray(options);
		int totalComboBox = sahiTasks.div(comboBoxIdentifier).countSimilar();
		_logger.finer("ComboBoxIdentifier Count: "+totalComboBox);
		for(int i=0; i<totalComboBox; i++){
			_logger.finer("ComboBoxIdentifier Name: "+comboBoxIdentifier+"["+i+"] --> "+sahiTasks.div(comboBoxIdentifier+"["+i+"]").getText());
		}
		if(optionArray.length > 2){
			Wait.waitForElementDivExists(sahiTasks,  comboBoxIdentifier+"["+(optionArray.length+indexStartFrom)+"]", 1000*10);
		}
		for(int i=0;i<optionArray.length;i++){
			ComboBox.selectComboBoxDivRow(sahiTasks,  comboBoxIdentifier+"["+(i+indexStartFrom)+"]", optionArray[i].trim());
		}*/
		
		String[] optionArray = Common.getCommaToArray(options);
		for(String option : optionArray){
			String[] optionTmp = option.split("-->");
			ComboBox.selectComboBoxDivRow(sahiTasks, optionTmp[0].trim(), optionTmp[1].trim());
		}		
	}
	
	private void updateSystemUserNotification(String users){
		String[] usersArray = Common.getCommaToArray(users);
		for(String user: usersArray){
			sahiTasks.byText(user.trim(), "nobr").doubleClick();
		}
	}
	
	private int getNumberAlert(String alertName){
		return sahiTasks.link(alertName).countSimilar();
	}
	
	//@Parameters({ "resourceName", "alertName", "alertDescription", "conditionDropDowns", "conditionTextBox", "notificationType", "notificationData" })
	//@Test(groups = { "sanity","full","functional" })
	public void createAlert(@Optional String resourceName, String alertName, @Optional String alertDescription, String conditionsDropDown, @Optional String conditionTextBox, String notificationType, String notificationData){
	
		//Select Resource to define alert
		if(resourceName != null){
			gotoAlertDefinationPage(resourceName);
		}
		
		//Take current status
		int similarAlert = getNumberAlert(alertName);
		_logger.finer("pre-status of Alert definition ["+alertName+"]: "+similarAlert +" definition(s)");
		
		//Define new alert name and Description(if any)
		sahiTasks.cell("New").click();
		sahiTasks.textbox(0).setValue(alertName);
		if(alertDescription != null){
			sahiTasks.textarea(0).setValue(alertDescription);
		}
		
		//Add conditions
		sahiTasks.cell("Conditions").click();
		sahiTasks.cell("Add").click();
		
		selectConditionComboBoxes(conditionsDropDown);
		
		if(conditionTextBox != null){
			if(conditionTextBox.trim().length()>0){
				HashMap<String, String> keyValueMap = Common.getKeyValueMap(conditionTextBox);
				Set<String> keys = keyValueMap.keySet();
				for(String key: keys){
					sahiTasks.textbox(key).setValue(keyValueMap.get(key)); 
				}
			}
		}
		
		sahiTasks.cell("OK").click();
		
		//Add notifications
		sahiTasks.cell("Notifications").click();
		sahiTasks.cell("Add[1]").click();
		//Select Notification type
		if(notificationType.equalsIgnoreCase("System Users")){
			updateSystemUserNotification(notificationData);
		}else{
			_logger.log(Level.WARNING, "Undefined notification type: "+notificationType);
		}
		sahiTasks.cell("OK").click();
		sahiTasks.xy(sahiTasks.cell("Save"), 3, 3).click();
		sahiTasks.bold("Back to List").click();
		
		//Check Creation Status
		Assert.assertEquals(getNumberAlert(alertName)-similarAlert, 1, "Alert Definition: \""+alertName+"\"");
		_logger.finer( "\""+alertName+"\" alert definition successfully created!");
	}
	
	//@Parameters({ "resourceName", "alertName", "alertDescription", "conditionDropDowns", "conditionTextBox", "notificationType", "notificationData" })
		//@Test(groups = { "sanity","full","functional" })
		public void checkAlertFired(@Optional String resourceName, String alertName, @Optional String alertDescription, String conditionsDropDown, @Optional String conditionTextBox, String notificationType, String notificationData){
		
			//Select Resource to define alert
			if(resourceName != null){
				gotoAlertDefinationPage(resourceName);
			}
			
			//Take current status
			int similarAlert = getNumberAlert(alertName);
			_logger.finer("pre-status of Alert definition ["+alertName+"]: "+similarAlert +" definition(s)");
			
			//Define new alert name and Description(if any)
			sahiTasks.cell("New").click();
			sahiTasks.textbox(0).setValue(alertName);
			if(alertDescription != null){
				sahiTasks.textarea(0).setValue(alertDescription);
			}
			
			//Add conditions
			sahiTasks.cell("Conditions").click();
			sahiTasks.cell("Add").click();
			
			selectConditionComboBoxes(conditionsDropDown);
			
			if(conditionTextBox != null){
				if(conditionTextBox.trim().length()>0){
					HashMap<String, String> keyValueMap = Common.getKeyValueMap(conditionTextBox);
					Set<String> keys = keyValueMap.keySet();
					for(String key: keys){
						sahiTasks.textbox(key).setValue(keyValueMap.get(key)); 
					}
				}
			}
			
			sahiTasks.cell("OK").click();
			
			//Add notifications
			sahiTasks.cell("Notifications").click();
			sahiTasks.cell("Add[1]").click();
			//Select Notification type
			if(notificationType.equalsIgnoreCase("System Users")){
				updateSystemUserNotification(notificationData);
			}else{
				_logger.log(Level.WARNING, "Undefined notification type: "+notificationType);
			}
			sahiTasks.cell("OK").click();
			sahiTasks.xy(sahiTasks.cell("Save"), 3, 3).click();
			sahiTasks.bold("Back to List").click();
			
			//Check Creation Status
			Assert.assertEquals(getNumberAlert(alertName)-similarAlert, 1, "Alert Definition: \""+alertName+"\"");
			_logger.finer( "\""+alertName+"\" alert definition successfully created!");
		}
		
		
	
	@Test (groups="alertTest", dataProvider="alertCreationData")
	public void createAlert(Object alertDetail){
		HashMap<String, String> alertDetails = (HashMap<String, String>)alertDetail;
		createAlert(alertDetails.get(RESOURCE_NAME), alertDetails.get(ALERT_NAME), alertDetails.get(ALERT_DESCRIPTION), alertDetails.get(CONDITION_DROPDOWNS), alertDetails.get(CONDITION_TEXTBOX), alertDetails.get(NOTIFICATION_TYPE), alertDetails.get(NOTIFICATION_DATA));
	}
	
	@DataProvider(name="alertCreationData")
	public Object[][] getAlertCreationData(){
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent-Server Clock Difference");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->Agent-Server Clock Difference[1],< (Less than) --> < (Less than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=5000");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent - Avg Execution Time Commands Received Successfully");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->Avg Execution Time Commands Received Successfully,< (Less than) --> > (Greater Than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=21");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent - Avg Execution Time Commands Sent Successfully");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->Avg Execution Time Commands Sent Successfully,< (Less than) --> > (Greater Than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=21");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent - JVM Active Threads");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->JVM Active Threads,< (Less than) --> > (Greater Than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=35");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent - JVM Free Memory");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->JVM Free Memory,< (Less than) --> < (Less than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=20971520");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent Number Of Active Commands Being Sent");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->Number Of Active Commands Being Sent,< (Less than) --> < (Less than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=500");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent - Number of restarts");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->Number of Agent Restarts,< (Less than) --> < (Less than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=50");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		

		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent - Number of Commands In Queue");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->Number of Commands In Queue,< (Less than) --> < (Less than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=500");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		

		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(ALERT_NAME, "RHQ Agent - Number of Commands Received Successfully");
		map.put(ALERT_DESCRIPTION, "Generated by automation");
		map.put(CONDITION_DROPDOWNS, "Availability Change-->Measurement Absolute Value Threshold,Agent-Server Clock Difference-->Number of Commands Received Successfully,< (Less than) --> < (Less than)");
		map.put(CONDITION_TEXTBOX, "metricAbsoluteValue=500");
		map.put(NOTIFICATION_TYPE, "System Users");
		map.put(NOTIFICATION_DATA, "rhqadmin");		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		

		
		return TestNGUtils.convertListTo2dArray(data);
		
	}
}
