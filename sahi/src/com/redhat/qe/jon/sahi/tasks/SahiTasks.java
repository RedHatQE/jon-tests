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

	//***************************************************************************
	//Compatible Group Creation
	//*****************************************************************
	
	public void createCompatibleGroup(String compGroupName, String groupDesc) {
		// TODO Auto-generated method stub
		
		 this.link("Inventory").click();
		 this.waitFor(5000);
		 this.cell("Compatible Groups").click();
		 this.cell("New").click();
		 this.textbox("name").setValue(compGroupName);
		 this.textarea("description").setValue(groupDesc);
		 this.cell("Next").click();
		 this.textbox("search").setValue("rhq agent");
		 this.div("RHQ Agent").click();
		 this.image("right.png").click();
		 this.cell("Finish").click();
		
	}
	
	public void deleteCompGroup(String compGroupName) {
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("Compatible Groups").click();
		this.div("compatible group").click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}


        //***************************************************************************
	//Mixed Group Creation
	//*****************************************************************
	
	public void createMixedGroup(String mixedGroupName, String groupDesc){
		
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("Mixed Groups").click();
		this.cell("New").click();
		this.textbox("name").setValue(mixedGroupName);
		this.textarea("description").setValue(groupDesc);
		this.cell("Next").click();
		this.textbox("search").setValue("rhq agent");
		this.div("RHQ Agent").click();
		this.image("right.png").click();
		this.waitFor(5000);
		this.textbox("search").setValue("cron");
		this.div("Cron").click();
		this.image("right.png").click();
		this.cell("Finish").click();
		
	}
	public void deleteMixedGroup(String compGroupName) {
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("Mixed Groups").click();
		this.div("mixed group").click();
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
	
	// ***************************************************************************
	// Reports
	// ***************************************************************************
	public void inventoryReport() {
		this.link("Reports").click();
		this.cell("Inventory Summary").click();
		
	}
	public void platformUtilization() {
		this.link("Reports").click();
		this.cell("Platform Utilization").click();
	}
	
	public void suspectMetrics() {
		this.link("Reports").click();
		this.cell("Suspect Metrics").click();
	}
	
	public void configurationHistory() {
		this.link("Reports").click();
		this.cell("Configuration History").click();
	}
	
	public void recentOperations() {
		this.link("Reports").click();
		this.cell("Recent Operations").click();
	}
	
	public void recentAlerts() {
		this.link("Reports").click();
		this.cell("Recent Alerts").click();
	}
	
	public void alertDefinitions() {
		this.link("Reports").click();
		this.cell("Alert Definitions").click();
	}
	//******************************************************************
	//Resource 
	//******************************************************************
	public void checkResourceBrowserAvailabilityColumnsInGroupDef( ){
			
		this.link("Inventory").click();
		this.cell("Dynagroup Definitions").click();
		this.cell("Name").exists();
		org.testng.Assert.assertTrue(this.cell("Description").exists());
		org.testng.Assert.assertTrue(this.cell("Description").exists());
		org.testng.Assert.assertTrue(this.cell("Expression Set").exists());
		org.testng.Assert.assertTrue(this.cell("Last Calculation Time").exists());
	}
		
	public void checkResourceBrowserAvailabilityColumnsInEachGroup(){
		this.link("Inventory").click();
		this.cell("All Groups").click();
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Description").exists());
		org.testng.Assert.assertTrue(this.cell("Type").exists());
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Plugin").exists());
		org.testng.Assert.assertTrue(this.cell("Descendants").exists());
		this.cell("Compatible Groups").click();
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Description").exists());
		org.testng.Assert.assertTrue(this.cell("Type").exists());
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Plugin").exists());
		org.testng.Assert.assertTrue(this.cell("Descendants").exists());
		this.cell("Mixed Groups").click();
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Description").exists());
		org.testng.Assert.assertTrue(this.cell("Type").exists());
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Plugin").exists());
		org.testng.Assert.assertTrue(this.cell("Descendants").exists());
		this.cell("Problem Groups").click();
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Description").exists());
		org.testng.Assert.assertTrue(this.cell("Type").exists());
		org.testng.Assert.assertTrue(this.cell("Name").exists());
		org.testng.Assert.assertTrue(this.cell("Plugin").exists());
		org.testng.Assert.assertTrue(this.cell("Descendants").exists());
		this.link("Inventory").click();
		
	}
		
		
	public void checkSearchTextBoxInEachResourceBrowserGroup(){
		this.link("Inventory").click();
		this.cell("All Groups").click();
		org.testng.Assert.assertTrue(this.textbox("SearchPatternField").exists());
		this.cell("Compatible Groups").click();
		org.testng.Assert.assertTrue(this.textbox("SearchPatternField").exists());
		this.cell("Mixed Groups").click();
		org.testng.Assert.assertTrue(this.textbox("SearchPatternField").exists());
		this.cell("Problem Groups").click();
		org.testng.Assert.assertTrue(this.textbox("SearchPatternField").exists());
		this.link("Inventory").click();
	}
		
	public void createDyanGroup(String groupName, String groupDesc){
		this.link("Inventory").click();
		this.cell("Dynagroup Definitions").click();
		this.cell("New").click();
		this.textbox("name").setValue(groupName);
		this.textarea("description").setValue(groupDesc);
		this.textarea("expression").setValue("groupby resource.trait[jboss.system:type=Server:VersionName]\nresource.type.plugin = JBossAS\nresource.type.name = JBossAS Server");
		this.cell("Save & Recalculate").click();
		org.testng.Assert.assertTrue(this.cell("You have successfully recalculated this group definition").exists());
		this.bold("Back to List").click();
		this.link(groupName).click();
		this.bold("Back to List").click();						
	}		
		
	public void deleteDynaGroup(String groupDesc){
		this.link("Inventory").click();
		this.cell("Dynagroup Definitions").click();
		this.div(groupDesc).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}
	
	// ***************************************************************************
	// Dashboard
	// ***************************************************************************
	
	public void clickDashboardTopLevelMenu () {
		this.link("Dashboard").click();
	}
	
	public void editDashboard() {
		this.cell("Edit Mode").click();
		this.cell("View Mode").click();
	}
	
	public void newDashboard() {
		this.link("Dashboard").click();     
		this.cell("New Dashboard").click();
		this.textbox("name").setValue("test");
		this.image("close.png[1]").click();
		this.cell("Yes").click();
	}
	
}
