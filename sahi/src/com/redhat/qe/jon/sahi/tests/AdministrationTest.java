package com.redhat.qe.jon.sahi.tests;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.redhat.qe.auto.testng.Assert;

import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.tasks.SahiTasksException;

public class AdministrationTest extends SahiTestScript {
	
	@Test (groups="user", dataProvider="roleData")
	public void createRoles(String roleName, String roleDesc){
		sahiTasks.createRole(roleName, roleDesc);
	}

	@Test (groups="user", dataProvider="userData", dependsOnMethods={"createRoles"})
	public void createUsers(String userName, String password, String firstName, String lastName, String email){
		sahiTasks.createUser(userName, password, firstName, lastName, email);
	}

	@Test (groups="user", dataProvider="userRoleData", dependsOnMethods={"createUsers", "createRoles"})
	public void addRolesToUser(String userName, String password, ArrayList<String> roleNames) {
		sahiTasks.addRolesToUser(userName, roleNames);
	}

	@Test (groups="user", dataProvider="userRoleData", dependsOnMethods={"addRolesToUser"})
	public void verifyUserRole(String userName, String password, ArrayList<String> roleNames) {
		Assert.assertTrue(sahiTasks.verifyUserRole(userName, password, roleNames), "Making sure user created has the right permission according to role.");
	}

	@Test (groups="user", dataProvider="userData", dependsOnMethods={"createUsers", "verifyUserRole"}, alwaysRun=true)
	public void deleteUsers(String userName, String password, String firstName, String lastName, String email){
		sahiTasks.deleteUser(userName);
	}

	@Test (groups="user", dataProvider="roleData", dependsOnMethods={"deleteUsers", "verifyUserRole"}, alwaysRun=true)
	public void deleteRole(String roleName, String roleDesc) throws SahiTasksException{
		sahiTasks.deleteRole(roleName);
	}

	@DataProvider(name="roleData")
	public Object[][] roleData() {
		return TestNGUtils.convertListOfListsTo2dArray(getRoleData());
	}

	public List<List<Object>> getRoleData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"Test Role", "Description"}));
		return data;
	}
	
	@DataProvider(name="userData")
	public Object[][] userData() {
		return TestNGUtils.convertListOfListsTo2dArray(getUserData());
	}

	public List<List<Object>> getUserData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"testuser", "password","jboss","operations","test@redhat.com"}));
		return data;
	}

	@DataProvider(name="userRoleData")
	public Object[][] userRoleData() {
		return TestNGUtils.convertListOfListsTo2dArray(getUserRoleData());
	}
	public List<List<Object>> getUserRoleData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		ArrayList<String> roleList = new ArrayList<String>();
		roleList.add("Test Role");
		data.add(Arrays.asList(new Object[]{"testuser", "password", roleList}));
		return data;
	}

	@DataProvider(name="userCreationWithRoleVerifications")
	public Object[][] userCreationWithRoleVerificationsData() {
		return TestNGUtils.convertListOfListsTo2dArray(userCreationRoleVerificationData());
	}
	public List<List<Object>> userCreationRoleVerificationData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"testuser", "password","jboss","operations","test@redhat.com","Test Role", "Description"}));
		return data;
	}
}
