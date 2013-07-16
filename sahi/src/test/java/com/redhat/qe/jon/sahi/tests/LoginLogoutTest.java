package com.redhat.qe.jon.sahi.tests;

import java.util.logging.Logger;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class LoginLogoutTest extends SahiTestScript {
	private static Logger _logger = Logger.getLogger(LoginLogoutTest.class.getName());
	private static String loginErrorMessgae = "The username or password provided does not match our records.";
	
	@Parameters({ "gui.username", "gui.password" })
	@Test (groups={"functional","sanity","setup","login"})
	public void loginTest(String guiUsername, String guiPassword){
		_logger.finer("Logging into RHQ system");
		Assert.assertTrue(sahiTasks.login(guiUsername, guiPassword), "Login status");
		Assert.assertFalse(sahiTasks.cell(loginErrorMessgae).exists(), "Login error message["+loginErrorMessgae+"] available?: "+sahiTasks.cell(loginErrorMessgae).exists());
		Assert.assertFalse(sahiTasks.textbox("user").exists(), "Login user TextBox available?: "+sahiTasks.textbox("user").exists());
		Assert.assertFalse(sahiTasks.password("password").exists(), "Login user password field available?: "+sahiTasks.password("password").exists());
	}
	
	@Parameters({ "ldap.username", "ldap.password", "ldap.first.name", "ldap.last.name", "ldap.email", "ldap.phone.number", "ldap.department" })
	@Test (groups={"functional","sanity","setup","login"})
	public void ldapLoginTest(String guiUsername, String guiPassword, @Optional String firstName, @Optional String lastName, @Optional String email, @Optional String phoneNumber, @Optional String department){
		_logger.finer("Logging into RHQ system");
		Assert.assertTrue(sahiTasks.ldapLogin(guiUsername, guiPassword, firstName, lastName, email, phoneNumber, department), "Login status");
		Assert.assertFalse(sahiTasks.cell(loginErrorMessgae).exists(), "Login error message["+loginErrorMessgae+"] available?: "+sahiTasks.cell(loginErrorMessgae).exists());
		Assert.assertFalse(sahiTasks.textbox("user").exists(), "Login user TextBox available?: "+sahiTasks.textbox("user").exists());
		Assert.assertFalse(sahiTasks.password("password").exists(), "Login user password field available?: "+sahiTasks.password("password").exists());
	}
	
	@Test (groups={"functional","sanity","setup","logout"})
	public void logoutTest(){
		_logger.finer("Logging out RHQ system");
		sahiTasks.logout();
		Assert.assertTrue(sahiTasks.textbox("user").exists(), "Login user TextBox available?: "+sahiTasks.textbox("user").exists());
		Assert.assertTrue(sahiTasks.password("password").exists(), "Login user password field available?: "+sahiTasks.password("password").exists());
		Assert.assertTrue(sahiTasks.cell("Login").exists(), "Login button available?: "+sahiTasks.cell("Login").exists());
	}	
	
	@Parameters({ "ldap.url", "ldap.search.base", "ldap.login.property", "ldap.enable.ssl" })
	@Test
	public void setupLdapServer(String ldapUrl, String ldapSearchBase, String ldapLoginProperty, String enableSSLStr){
		boolean enableSSL = false;
		if(enableSSLStr.trim().equalsIgnoreCase("true")){
			enableSSL = true;
		}
		Assert.assertTrue(sahiTasks.registerLdapServer(ldapUrl, ldapSearchBase, ldapLoginProperty, enableSSL, true), "LDAP Setup Status");
	}
}
