package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.redhat.qe.auto.testng.TestNGUtils;

public class ResourceTest extends SahiTestScript{
	
	
	@Test (groups="resourceTest")
	public void testResourceBrowserAvailabilityColumnsInGroupDefinitions(){		
		sahiTasks.checkResourceBrowserAvailabilityColumnsInGroupDef();
	}
	
	@Test (groups="resourceTest")
	public void testResourceBrowserAvailabilityColumnsInGroups(){		
		sahiTasks.checkResourceBrowserAvailabilityColumnsInEachGroup();
	}
	
	@Test (groups="resourceTest")
	public void checkSearchTextBoxInEachResourceBrowserGroups(){
		sahiTasks.checkSearchTextBoxInEachResourceBrowserGroup();
		
	}
	
	@Test (groups="resourceTest",dataProvider="dynagroup")
	public void createDynaGroups(String groupName, String groupDesc){
		sahiTasks.createDyanGroup(groupName, groupDesc);
		
	}
	
	@Test (groups="resourceTest",dependsOnMethods="createDynaGroups", dataProvider="dynagroup")
	public void deleteDynaGrroups(String groupName, String groupDesc){
		sahiTasks.deleteDynaGroup(groupDesc);
	}
	@DataProvider(name="dynagroup")
	public Object[][] dynaGroupData() {
		return TestNGUtils.convertListOfListsTo2dArray(getDynaGroup());
	}
	
	
	public List<List<Object>> getDynaGroup() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"dyna group", "dyna Group description"}));
		return data;
	}
	

}
