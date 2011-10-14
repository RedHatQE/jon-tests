package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class GroupConfigTest extends SahiTestScript {
	
		
	@Test (groups= "group config", dataProvider = "compatibleGroup")
	 public void navigationToGroupConfig(String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
		sahiTasks.navigationToGroupConfigurtion("Compatible Groups",compatibleGroup, groupDesc, resourceList);
	}
	
	@Test (groups ="group config", dependsOnMethods= {"navigationToGroupConfig"} )
	public void navigationToGroupConfigSubTabs(){
		sahiTasks.navigationToGroupConfigurationSubtabs();
		
	}
	@Test (groups="group config", dependsOnMethods= {"navigationToGroupConfigSubTabs"} )
	public void editAndSaveGroupConfiguration(){
		sahiTasks.editAndSaveGroupConfiguration();
	}
	@AfterClass
	public void deleteComaptibleGroup(){
		sahiTasks.deleteCompatibilityGroup("Compatible Groups","compatibleGroup");
	}
	
	@DataProvider(name="compatibleGroup")
	public Object[][] compatibleGroupData() {
		return TestNGUtils.convertListOfListsTo2dArray(getCompatibleGroup());
	}
	
	public List<List<Object>> getCompatibleGroup() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		ArrayList<String> resourceData = new ArrayList<String>();
		resourceData.add("RHQ Agent");
		data.add(Arrays.asList(new Object[]{"compatibleGroup", "Compatible Group description", resourceData}));
		return data;
	}

	

}
