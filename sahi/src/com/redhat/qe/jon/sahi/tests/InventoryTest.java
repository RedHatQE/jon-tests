package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.redhat.qe.auto.testng.TestNGUtils;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class InventoryTest extends SahiTestScript{
	@Test (groups="inventoryTest", dataProvider="groupData")
	public void createGroups(String groupName, String groupDesc) {
		sahiTasks.createGroup(groupName, groupDesc);
	}

	@Test (groups="inventoryTest", dataProvider="groupData", dependsOnMethods={"createGroups"})
	public void deleteGroups(String groupName, String groupDesc) {
		sahiTasks.deleteGroup(groupName);
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
}
