package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.*;
import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class SearchTest extends SahiTestScript {
	
	@Test (groups="search", dataProvider ="compatibleGroup")
	public void searchCompGroupWithText(String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
		//sahiTasks.createGroup("Compatible Groups",  compatibleGroup, groupDesc, resourceList);
		//sahiTasks.serachForCompatiblityGroups("Compatible Groups",  compatibleGroup, groupDesc, resourceList);
		//Assert.assertEquals(true, sahiTasks.serachForCompatiblityGroups("Compatible Groups",  compatibleGroup, groupDesc, resourceList), "Fine");
		Assert.assertEquals(true,sahiTasks.searchComaptibilityGroupWithText("Compatible Groups",  compatibleGroup, groupDesc, resourceList),"Comp Group Going good ...");
		
	}
	@Test (groups="search", dataProvider ="compatibleGroup")
	public void searchAllGroupWithText(String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
		Assert.assertEquals(true,sahiTasks.searchAllGroupWithText("All Groups",  compatibleGroup, groupDesc, resourceList),"All Groups Going good ...");
		
	}
	@Test (groups="search", dataProvider ="mixedGroup")
	public void searchMixedGroupWithText(String mixedGroup, String groupDesc, ArrayList<String> resourceList){
		Assert.assertEquals(true,sahiTasks.searchAllGroupWithText("Mixed Groups",  mixedGroup, groupDesc, resourceList),"Mixed Groups Going good ...");
		
	}
	
	@Test (groups="delete", dependsOnMethods ="searchCompGroupWithText")
	public void deleteComaptibleGroup(){
		sahiTasks.deleteCompatibilityGroup("Compatible Groups","compatibleGroup");
	}
	@Test (groups="delete", dependsOnMethods ="searchMixedGroupWithText")
	public void deleteMixedGroup(){
		sahiTasks.deleteCompatibilityGroup("Mixed Groups","mixedGroup");
	}
	@Test (groups="delete", dependsOnMethods ="searchAllGroupWithText")
	public void deleteAllGroups(){
		sahiTasks.deleteCompatibilityGroup("All Groups","mixedGroup");
	}

	@DataProvider(name="mixedGroup")
	public Object[][] mixedGroupData() {
		return TestNGUtils.convertListOfListsTo2dArray(getMixedGroup());
	}
	
	public List<List<Object>> getMixedGroup() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		ArrayList<String> resourceData = new ArrayList<String>();
		resourceData.add("RHQ Agent");
		resourceData.add("Cron");
		data.add(Arrays.asList(new Object[]{"mixedGroup", "mixed Group description", resourceData}));
		return data;
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
