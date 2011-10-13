package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class SchedulingForGroupsTest extends SahiTestScript{
	
	
	
	@ Test (groups= "schedule groups", dataProvider= "compatibleGroup")
	public void enableScheduleforGroup(String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
		sahiTasks.scheduleEnableForGroup("Compatible Groups",  compatibleGroup, groupDesc, resourceList);
		
	}
	
	@ Test (groups= "schedule groups",dependsOnMethods="enableScheduleforGroup")
	public void disableScheduleforGroups(){
		sahiTasks.disableScheduleGroup();
		
	}
	
	@ Test (groups= "schedule groups", dependsOnMethods="disableScheduleforGroups")
	public void refreshScheduleforGroups(){
		sahiTasks.refreshScheduledGroup();
		
	}
	@ Test (groups= "schedule groups", dependsOnMethods="refreshScheduleforGroups")
	public void scheduleCollectionIntervalforGroups(){
		sahiTasks.setCollectionIntervalForScheduledGroup(2);
		
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
