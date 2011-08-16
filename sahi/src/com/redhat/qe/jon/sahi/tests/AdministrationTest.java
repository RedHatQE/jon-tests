package com.redhat.qe.jon.sahi.tests;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class AdministrationTest extends SahiTestScript {
	
	@Test (groups="user", dataProvider="roleData")
	public void createRoles(String roleName, String roleDesc){
		sahiTasks.createRole(roleName, roleDesc);
	}

	@Test (groups="user", dataProvider="userData", dependsOnMethods={"createRoles"})
	public void createUsers(String userName, String password, String firstName, String lastName, String email){
		sahiTasks.createUser(userName, password, firstName, lastName, email);
	}

	@Test (groups="user",dataProvider="userData", dependsOnMethods={"createUsers"})
	public void deleteUsers(String userName, String password, String firstName, String lastName, String email){
		sahiTasks.deleteUser(userName);
	}

	@Test (groups="user",dataProvider="roleData", dependsOnMethods={"deleteUsers"})
	public void deleteRole(String roleName, String roleDesc){
		sahiTasks.deleteRole(roleName);
	}

/*
	@Test (groups="role", dataProvider="roledata")
	public void roleCreationWithValidations(String roleName, String roleDesc){
		sahiTasks.createRoleWithValidations(roleName, roleDesc);
	}

	@Test (groups="userAndRole", dataProvider="userCreationWithRoleVerifications")
	public void userCreationWithRoleVerifications(String userName, String password, String firstName, String lastName, String email, String roleName, String roleDesc){
		sahiTasks.userCreationWithRoleVerification(userName, password, firstName, lastName, email, roleName, roleDesc);
	}
*/
	
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
