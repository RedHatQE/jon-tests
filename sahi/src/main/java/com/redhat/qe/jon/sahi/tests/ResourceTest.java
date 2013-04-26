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

	@Test (groups="resourceSearchTest", dataProvider="roleUserAndGroupCreationData")
	public void resourceSearch(String searchTestuser, String password, String firstName, String secondName, String emailId, String searchRoleName, String desc, String compTestGroup, String searchQueryName){
		sahiTasks.resourceSearch(searchRoleName, password, firstName, secondName, emailId, searchRoleName, desc, compTestGroup,searchQueryName);
	}
	
	@Test (groups="resourceSearchTest")
	public void navigateToAllGroups(){
		sahiTasks.navigateToAllGroups();
	}
	@DataProvider (name="roleUserAndGroupCreationData")
	public static Object[][] userCreationWithRoleData() {
		return TestNGUtils.convertListOfListsTo2dArray(userRoleAndGroupData());
	}
	public static List<List<Object>> userRoleAndGroupData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"searchtestuser", "password", "jboss", "operations", "test@redhat.com", "search Test Role1", "Description", "testCompGroup", "name"}));
		return data;
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
