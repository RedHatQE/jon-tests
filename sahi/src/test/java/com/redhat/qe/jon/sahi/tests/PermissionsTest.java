package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.SahiTasksException;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class PermissionsTest extends SahiTestScript{
	
	@Test (groups="permissionsTest",  dataProvider="roleUserAndGroupCreationData", dataProviderClass = com.redhat.qe.jon.sahi.tests.ResourceTest.class)
	public void checkManageSecurity(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		sahiTasks.checkManageSecurity( searchTestuser,  password,  firstName,  secondName,  emailId,  roleName,  desc,  compTestGroup,  searchQueryName);
		
	}
	
	@Test (groups="permissionsTest",  dataProvider="roleUserAndGroupCreationData", dataProviderClass = com.redhat.qe.jon.sahi.tests.ResourceTest.class, dependsOnMethods="checkManageSecurity")
	public void checkManageInventory(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		sahiTasks.checkManageInventory( searchTestuser,  password,  firstName,  secondName,  emailId,  roleName,  desc,  compTestGroup,  searchQueryName);
		
	}
	
	@Test (groups="permissionsTest",  dataProvider="roleUserAndGroupCreationData", dataProviderClass = com.redhat.qe.jon.sahi.tests.ResourceTest.class, dependsOnMethods="checkManageInventory")
	public void checkManageRespository(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		sahiTasks.checkManageRespository( searchTestuser,  password,  firstName,  secondName,  emailId,  roleName,  desc,  compTestGroup,  searchQueryName);
		sahiTasks.waitFor(Timing.TIME_5S);		
	}
	
	@Test (groups="permissionsTest",  dataProvider="roleUserAndGroupCreationData", dataProviderClass = com.redhat.qe.jon.sahi.tests.ResourceTest.class, dependsOnMethods="checkManageRespository")
	public void checkViewUsers(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		sahiTasks.checkViewUsers( searchTestuser,  password,  firstName,  secondName,  emailId,  roleName,  desc,  compTestGroup,  searchQueryName);
		sahiTasks.waitFor(Timing.TIME_5S);
		
	}
	
	@Test (groups="permissionsTest",  dataProvider="roleUserAndGroupCreationData", dataProviderClass = com.redhat.qe.jon.sahi.tests.ResourceTest.class, dependsOnMethods="checkViewUsers")
	public void checkManageSettings(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		sahiTasks.checkManageSettings( searchTestuser,  password,  firstName,  secondName,  emailId,  roleName,  desc,  compTestGroup,  searchQueryName);
		sahiTasks.waitFor(Timing.TIME_5S);
		
	}
	
	@Test (groups="permissionsTest",  dataProvider="roleUserAndGroupCreationData", dataProviderClass = com.redhat.qe.jon.sahi.tests.ResourceTest.class, dependsOnMethods="checkManageSettings")
	public void checkManageBundles(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		sahiTasks.checkManageBundles( searchTestuser,  password,  firstName,  secondName,  emailId,  roleName,  desc,  compTestGroup,  searchQueryName);
		sahiTasks.waitFor(Timing.TIME_5S);
	}
	
	@Test (groups="permissionsTest",  dataProvider="roleUserAndGroupCreationData", dataProviderClass = com.redhat.qe.jon.sahi.tests.ResourceTest.class, dependsOnMethods="checkManageBundles")
	public void checkGroupsPermission(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		sahiTasks.checkGroupsPermission( searchTestuser,  password,  firstName,  secondName,  emailId,  roleName,  desc,  compTestGroup,  searchQueryName);
		sahiTasks.waitFor(Timing.TIME_5S);
	}
	
	@AfterClass
	public void checkAdminUser(){
		sahiTasks.relogin(SahiTasks.ADMIN_USER, SahiTasks.ADMIN_PASSWORD);
	}
		
}
