package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.redhat.qe.auto.testng.TestNGUtils;

import com.redhat.qe.auto.testng.Assert;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class InventoryTest extends SahiTestScript{
	@BeforeMethod(groups="inventoryTest")
	public void nap() {
		sahiTasks.waitFor(5000);
	}

	@Test (groups="inventoryTest", dataProvider="groupData")
	public void createGroups(String groupName, String groupDesc) {
		sahiTasks.createGroup("All Groups", groupName, groupDesc);
	}

	@Test (groups="inventoryTest", dataProvider="groupData", dependsOnMethods={"createGroups"})
	public void deleteGroups(String groupName, String groupDesc) {
		sahiTasks.deleteGroup("All Groups", groupName);
	}
	
	@Test (groups="inventoryTest", dataProvider="compatibleGroup")
	public void createCompatibleGroups(String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
		sahiTasks.createGroup("Compatible Groups", compatibleGroup, groupDesc, resourceList);
		
	}

	@Test (groups="inventoryTest", dataProvider="compatibleGroup", dependsOnMethods={"createCompatibleGroups"})	
	public void verifyCompatibleGroups(String compGroupName, String groupDesc, ArrayList<String> resourceList) {
		Assert.assertTrue(sahiTasks.verifyGroup("Compatible Groups", compGroupName), "Making sure compatible group is created.");
	}

	@Test (groups="inventoryTest", dataProvider="compatibleGroup", dependsOnMethods={"verifyCompatibleGroups"})	
	public void deleteCompatibilityGroups(String compGroupName, String groupDesc, ArrayList<String> resourceList){
		sahiTasks.deleteGroup("Compatible Groups", compGroupName);
		
	}

	@Test (groups="inventoryTest", dataProvider="mixedGroup")
	public void createMixedGroups(String mixedGroup, String groupDesc, ArrayList<String> resourceList){
		sahiTasks.createGroup("Mixed Groups", mixedGroup, groupDesc, resourceList);
		
	}
	
	@Test (groups="inventoryTest", dataProvider="mixedGroup", dependsOnMethods= {"createMixedGroups"})
	public void deleteMixedGroups(String mixedGroup, String groupDesc, ArrayList<String> resourceList){
		sahiTasks.deleteGroup("Mixed Groups", mixedGroup);
	}

	@Test (groups="inventoryTest", dataProvider="dynaGroup")
	public void createDynaGroups(String dynaGroup, String groupDesc, ArrayList<String> preloadExpressions, String otherExpressions) {
		sahiTasks.createDynaGroup(dynaGroup, groupDesc, preloadExpressions, otherExpressions);
	}
	
	@Test (groups="inventoryTest", dataProvider="dynaGroup", dependsOnMethods={"createDynaGroups"}) 
	public void verifyDynaGroups(String dynaGroup, String groupDesc, ArrayList<String> preloadExpressions, String otherExpressions) {
		sahiTasks.verifyGroup("Dynagroup Definitions", dynaGroup);
	}
	
	@Test (groups="inventoryTest", dataProvider="dynaGroup", dependsOnMethods= {"verifyDynaGroups"}, alwaysRun=true)
	public void deleteDynaGroups(String dynaGroup, String groupDesc, ArrayList<String> preloadExpressions, String otherExpression) {
		sahiTasks.deleteGroup("Dynagroup Definitions", dynaGroup);
	}
/*

	@Test (groups="inventoryResourceThruGroup", dataProvider="compatibleGroup",dependsOnMethods={"compatibleGroups"})	
	public void uninventoryResource(String compGroupName, String groupDesc){
		sahiTasks.uninventoryResourcethroughGroup(compGroupName, groupDesc);
		
	}
	@Test (groups="inventoryResourceThruGroup",  dependsOnMethods={"uninventoryResource"})	
	public void inventoryResource(){
		sahiTasks.inventoryResource();
		
	}
*/
	@Test (groups="inventoryConfiguration")
	public void inventoryConfiguration(){
		//sahiTasks.inventoryConfiguration();
	
	}
	@Test (groups="messageCenter", dependsOnGroups ={"inventoryConfiguration"})
	public void messageCenter(){
		//sahiTasks.messageCenter();
	}
		
	@DataProvider(name="groupData")
	public Object[][] groupData() {
		return TestNGUtils.convertListOfListsTo2dArray(getGroupData());
	}
	public List<List<Object>> getGroupData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"hello_auto_world", "this is a test description"}));
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

	@DataProvider(name="dynaGroup")
	public Object[][] dynaGroupData() {
		return TestNGUtils.convertListOfListsTo2dArray(getDynaGroup());
	}
	
	public List<List<Object>> getDynaGroup() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		ArrayList<String> preloadExpression = new ArrayList<String>();
		preloadExpression.add("JBossAS 4 - Unique versions");
		data.add(Arrays.asList(new Object[]{"dynaGroup", "dyna Group description", preloadExpression, "resource.availability = DOWN"}));
		return data;
	}
}
