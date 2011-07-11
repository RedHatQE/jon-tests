package com.redhat.qe.jon.sahi.tests;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class AdministrationTest extends SahiTestScript {
	
	
	@Test(groups="userCreation", dataProvider="userdata")
	public void createUsers(String userName, String password, String firstname, String secondname, String email){
		sahiTasks.createUser(userName, password, firstname, secondname, email);
	}
	@Test(groups="userCreation",dataProvider="userdata", dependsOnMethods={"createUsers"})
	public void deleteUsers(String userName, String password, String firstname, String secondname, String email){
		sahiTasks.deleteUser(userName);
	}
	@Test(groups="roleCreation", dataProvider="roledata")
	public void roleCreationWithValidations(String roleName, String roleDesc){
		sahiTasks.createRoleWithValidations(roleName, roleDesc);
	}
	@Test(groups="roleCreation",dataProvider="roledata", dependsOnMethods={"roleCreationWithValidations"})
	public void deleteRoles(String roleName, String roleDesc){
		sahiTasks.deleteRole(roleName);
	}
	@Test(groups="userRoleCreation", dataProvider="userCreationWithRoleVerifications")
	public void userCreationWithRoleVerifications(String userName, String password, String firstname, String secondname, String email, String roleName, String roleDesc){
		sahiTasks.userCreationWithRoleVerification(userName, password, firstname, secondname, email, roleName, roleDesc);
		
	}
	
	@DataProvider(name="roledata")
	public Object[][] roledata() {
		return TestNGUtils.convertListOfListsTo2dArray(getRoleData());
	}
	
	@DataProvider(name="userdata")
	public Object[][] userdata() {
		return TestNGUtils.convertListOfListsTo2dArray(getUserData());
	}
	public List<List<Object>> getUserData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"testuser", "password","jboss","operations","test@redhat.com"}));
		return data;
	}
	public List<List<Object>> getRoleData() {
		ArrayList<List<Object>> data = new ArrayList<List<Object>>();
		data.add(Arrays.asList(new Object[]{"Test Role", "Description"}));
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
