package com.redhat.qe.jon.sahi.tasks;

import com.redhat.qe.auto.sahi.ExtendedSahi;
import org.testng.Assert;

public class SahiTasks extends ExtendedSahi {
	public SahiTasks(String browserPath, String browserName, String browserOpt, String sahiBaseDir, String sahiUserdataDir) {
		super(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
	}

	// ***************************************************************************
	// Inventory
	// ***************************************************************************
	public void createGroup(String groupName, String groupDesc) {
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("All Groups").click();
		this.cell("New").click();
		this.textbox("name").setValue(groupName);
		this.textarea("description").setValue(groupDesc);
		this.cell("Next").click();
		this.cell("Finish").click();
	}

	public void deleteGroup(String groupName) {
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("All Groups").click();
		this.div(groupName).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}

	// ***************************************************************************
	// Bundle
	// ***************************************************************************
	public void createBundleURL(String bundleURL) {
		this.link("Bundles").click();
		this.cell("New").click();
		this.radio("URL").click();
		this.textbox("url").setValue(bundleURL);
		this.cell("Next").click();
		this.cell("Next").click();
		this.cell("Finish").click();
	}

	public void deleteBundle(String bundleName) {
		this.link("Bundles").click();
		this.waitFor(5000);
		this.div(bundleName).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}
	// ***************************************************************************
	// Menus
	// ***************************************************************************
	public void topLevelMenusExist() {
		
		Assert.assertTrue(this.link("Dashboard").exists());
		Assert.assertTrue(this.link("Inventory").exists());
		Assert.assertTrue(this.link("Reports").exists());
		Assert.assertTrue(this.link("Bundles").exists());
		Assert.assertTrue(this.link("Administration").exists());
		Assert.assertTrue(this.link("Help").exists());
		Assert.assertTrue(this.link("Logout").exists());
	}
	
	// ***************************************************************************
	// Users and Groups
	// ***************************************************************************
	public void createDeleteUser() {
		this.link("Administration").click();
		this.cell("Users").click();
		this.cell("New").click();
		this.textbox("name").setValue("test1");
		this.password("password").setValue("password");
		this.textbox("firstName").setValue("testfirstname");
		this.textbox("emailAddress").setValue("testemail@redhat.com");
		this.textbox("department").setValue("testdepartment");
		this.password("passwordVerify").setValue("password");
		this.textbox("lastName").setValue("testlastname");
		this.textbox("phoneNumber").setValue("999 999-9999");
		this.cell("Save").click();
		this.div("test1").click();
		this.cell("Delete").click();
		this.cell("Yes").click();
		
	}
	
	public void createDeleteRole() {
		this.link("Administration").click();
		this.cell("Roles").click();
		this.cell("New").click();
		this.textbox("name").setValue("testrole");
		this.textbox("description").setValue("testdescription");
		this.cell("Save").click();
		this.div("testrole").click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}
	
	
}
