package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class AlertDefinitionTemplateTest extends SahiTestScript{
	
	@Test (groups="alertDefinitionsTemplate", dataProvider="sample alert definition data")
	public void createAlertDefinitionTemplate(String groupPanelName, String templateName, String platform){
		sahiTasks.createAlertDefinitionTemplate(groupPanelName, templateName, platform);
	}
	@Test (groups="alertDefinitionsTemplate", dataProvider="sample alert definition data", dependsOnMethods="createAlertDefinitionTemplate")
	public void navigationWithConditionsTab(String groupPanelName, String templateName, String platform){
		sahiTasks.navigationThruConditionsTabAlertDefTemplate(groupPanelName, templateName);
	}
	
	/*@Test (groups="alertDefinitionsTemplate", dataProvider="sample alert definition data", dependsOnMethods="createAlertDefinitionTemplate")
	public void navigationWithNotificationsTab(String groupPanelName, String templateName){
		sahiTasks.navigationThruNotificationsTabAlertDefTemplate(groupPanelName, templateName);
	}*/
	
	@Test (groups="alertDefinitionsTemplate", dataProvider="sample alert definition data", dependsOnMethods="navigationWithConditionsTab")
	public void navigationWithRecoveryTab(String groupPanelName, String templateName, String platform){
		sahiTasks.navigationThruRecoveryTabAlertDefTemplate(groupPanelName, templateName);
	}
	@Test (groups="alertDefinitionsTemplate", dataProvider="sample alert definition data", dependsOnMethods="navigationWithRecoveryTab")
	public void navigationWithDampeningTab(String groupPanelName, String templateName, String platform){
		sahiTasks.navigationThruDampeningTabAlertDefTemplate(groupPanelName, templateName);
	}
	@Test (groups="alertDefinitionsTemplate", dataProvider="sample alert definition data", dependsOnMethods="navigationWithDampeningTab")
	public void deleteAlertDefinitionTemplate(String groupPanelName, String templateName, String platform){
		Assert.assertFalse(sahiTasks.deleteAlertDefinitionTemplate(templateName), "Template ["+templateName+"] deletion status");
	}
	
	@DataProvider(name="sample alert definition data")
	public Object[][] alertDefTemplateData() {
		return TestNGUtils.convertListOfListsTo2dArray(getAlertDefTemplateData());
	}
	
	public List<List<Object>> getAlertDefTemplateData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"Alert Definition Templates", "testAlertDefTemplate", "Java" }));
		return data;
	}
	

}
