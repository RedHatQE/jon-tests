package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.redhat.qe.auto.testng.TestNGUtils;

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
		sahiTasks.createGroup(groupName, groupDesc);
	}

	@Test (groups="inventoryTest", dataProvider="groupData", dependsOnMethods={"createGroups"})
	public void deleteGroups(String groupName, String groupDesc) {
		sahiTasks.deleteGroup(groupName);
	}
	

	@Test (groups="inventoryTest", dataProvider="compatibleGroup")
	public void compatibleGroups(String compatibleGroup, String groupDesc){
		sahiTasks.createCompatibleGroup(compatibleGroup, groupDesc);
		
	}
	@Test (groups="inventoryTest", dataProvider="compatibleGroup", dependsOnMethods={"compatibleGroups"})	
	public void deleteCompatibilityGroups(String compGroupName, String groupDesc){
		sahiTasks.deleteCompGroup(compGroupName);
		
	}
	
	
	@Test (groups="inventoryTest", dataProvider="mixedGroup")
	public void createMixedGroups(String mixedGroup, String groupDesc){
		sahiTasks.createMixedGroup(mixedGroup, groupDesc);
		
	}
	
	@Test (groups="inventoryTest", dataProvider="mixedGroup", dependsOnMethods= {"createMixedGroups"})
	public void deleteMixedGroups(String mixedGroup, String groupDesc){
		sahiTasks.deleteMixedGroup(mixedGroup);
	}

	@Test (groups="inventoryResourceThruGroup", dataProvider="compatibleGroup",dependsOnMethods={"compatibleGroups"})	
	public void uninventoryResource(String compGroupName, String groupDesc){
		sahiTasks.uninventoryResourcethroughGroup(compGroupName, groupDesc);
		
	}
	@Test (groups="inventoryResourceThruGroup",  dependsOnMethods={"uninventoryResource"})	
	public void inventoryResource(){
		sahiTasks.inventoryResource();
		
	}
	
	Test (groups="inventoryConfiguration")
	public void inventoryConfiguration(){
		sahiTasks.inventoryConfiguration();
	
	}
	@Test (groups="messageCenter", dependsOnGroups ={"inventoryConfiguration"})
	public void messageCenter(){
		sahiTasks.messageCenter();
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
		data.add(Arrays.asList(new Object[]{"compatible group", "Compatible Group description"}));
		return data;
	}
	
	@DataProvider(name="mixedGroup")
	public Object[][] mixedGroupData() {
		return TestNGUtils.convertListOfListsTo2dArray(getMixedGroup());
	}
	
	public List<List<Object>> getMixedGroup() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"mixed group", "mixed Group description"}));
		return data;
	}
}
