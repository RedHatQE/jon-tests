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

	public void uninventoryResourcethroughGroup(String groupName, String groupDesc){
		createCompatibleGroup(groupName, groupDesc);
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("Compatible Groups").click();
		this.link(groupName).click();
		this.image("Inventory_grey_16.png").click();
		this.div("RHQ Agent").click();
		this.cell("Uninventory").click();
		this.cell("Yes").click();
		this.link("Inventory").click();
			
	}
	public void inventoryResource(){
		this.link("Inventory").click();
		this.cell("Discovery Queue").click();
		this.cell("Refresh").click();
		this.image("opener_closed.png").click();
		this.image("unchecked.png").click();
		this.cell("Import").click();
		this.link("Inventory").click();
		
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
	public void topLevelMenuDashboardExist() {
		
		Assert.assertTrue(this.link("Dashboard").exists());
		Assert.assertTrue(this.link("Inventory").exists());
		Assert.assertTrue(this.link("Reports").exists());
		Assert.assertTrue(this.link("Bundles").exists());
		Assert.assertTrue(this.link("Administration").exists());
		Assert.assertTrue(this.link("Help").exists());
		Assert.assertTrue(this.link("Logout").exists());
	}
	
	public void topLevelMenuInventoryExist() {
		
		Assert.assertTrue(this.link("Dashboard").exists());
		Assert.assertTrue(this.link("Inventory").exists());
		Assert.assertTrue(this.link("Reports").exists());
		Assert.assertTrue(this.link("Bundles").exists());
		Assert.assertTrue(this.link("Administration").exists());
		Assert.assertTrue(this.link("Help").exists());
		Assert.assertTrue(this.link("Logout").exists());
	}
	
	public void topLevelMenuReportExist() {
		
		Assert.assertTrue(this.link("Dashboard").exists());
		Assert.assertTrue(this.link("Inventory").exists());
		Assert.assertTrue(this.link("Reports").exists());
		Assert.assertTrue(this.link("Bundles").exists());
		Assert.assertTrue(this.link("Administration").exists());
		Assert.assertTrue(this.link("Help").exists());
		Assert.assertTrue(this.link("Logout").exists());
	}

	public void topLevelMenuBundlesExist() {
		
		Assert.assertTrue(this.link("Dashboard").exists());
		Assert.assertTrue(this.link("Inventory").exists());
		Assert.assertTrue(this.link("Reports").exists());
		Assert.assertTrue(this.link("Bundles").exists());
		Assert.assertTrue(this.link("Administration").exists());
		Assert.assertTrue(this.link("Help").exists());
		Assert.assertTrue(this.link("Logout").exists());
	}
	
	public void topLevelMenuAdministrationExist() {
		
		Assert.assertTrue(this.link("Dashboard").exists());
		Assert.assertTrue(this.link("Inventory").exists());
		Assert.assertTrue(this.link("Reports").exists());
		Assert.assertTrue(this.link("Bundles").exists());
		Assert.assertTrue(this.link("Administration").exists());
		Assert.assertTrue(this.link("Help").exists());
		Assert.assertTrue(this.link("Logout").exists());
	}
	
	public void topLevelMenuHelpExist() {
		
		Assert.assertTrue(this.link("Dashboard").exists());
		Assert.assertTrue(this.link("Inventory").exists());
		Assert.assertTrue(this.link("Reports").exists());
		Assert.assertTrue(this.link("Bundles").exists());
		Assert.assertTrue(this.link("Administration").exists());
		Assert.assertTrue(this.link("Help").exists());
		Assert.assertTrue(this.link("Logout").exists());
	}
	
	public void topLevelMenuLogoutExist() {
		
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
	
	public void expandCollapseSections() {
		this.image("opener_closed.png").click();
		this.image("opener_opened.png[1]").click();
		this.image("opener_closed.png").click();
		this.image("opener_opened.png").click();
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

	public void resourceSearch(String searchTestuser, String password, String firstName, String secondName, String emailId, String searchRoleName, 						String desc, String compGroupName, String searchQueryName){
		createCompatibleGroup(compGroupName, desc);
		createUser(searchTestuser,password,firstName,secondName,emailId);
		createRoleWithoutMangeInvetntory(searchRoleName, desc, compGroupName, searchTestuser);
		loginNewUser(searchTestuser, password);
		navigateToAllGroups();
		enterValueToSerachTextBox(compGroupName, searchQueryName);
						
	}
	public void createRoleWithoutMangeInvetntory(String roleName, String desc, String compGroupName, String searchTestuser){
		this.link("Administration").click();
		this.cell("Roles").click();
		this.cell("New").click();
		this.textbox("name").setValue(roleName);
		this.textbox("description").setValue(desc);
		this.cell("Resource Groups").click();
		this.div("compGroupName").click();
		this.image("right.png").click();
		this.cell("users").click();
		this.div(searchTestuser).click();
		this.image("right.png").click();
		this.cell("Save").click();			
	}
	public void loginNewUser(String newUser, String password){
		this.textbox("user").setValue(newUser);
		this.password("password").setValue(password);
		this.cell("Login").click();
		
	}
	public void navigateToAllGroups(){
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("All Groups").click();
	}
	public void enterValueToSerachTextBox(String compGroupName, String searchName){
		this.textbox("SearchPatternField").setValue(searchName+"="+compGroupName);
		this.cell("name").click();
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

	// ***************************************************************************
	// Administration 
	// ***************************************************************************
	
	public void createUser(String userName, String password, String firstname, String secondname, String email){
		this.link("Administration").click();
		this.cell("Users").click();
		this.cell("New").click();
		this.textbox("name").setValue(userName);
		this.password("password").setValue(password);
		this.password("passwordVerify").setValue(password);
		this.textbox("firstName").setValue(firstname);
		this.textbox("lastName").setValue(secondname);
		this.textbox("emailAddress").setValue(email);
		this.div("Super User Role").click();
		this.image("right.png").click();
		this.cell("Save").click();
	}
	
	public void deleteUser(String userName){
		this.link("Administration").click();
		this.cell("Users").click();
		this.div(userName).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}
	
	public void createRoleWithValidations(String roleName, String roleDesc){
		this.link("Administration").click();
		this.cell("Roles").click();
		this.cell("New").click();
		this.textbox("name").setValue(roleName);
		this.textbox("description").setValue("Description");
		this.image("permission_disabled_11.png").click();
		this.image("unchecked.png").click();
		org.testng.Assert.assertTrue(this.image("permission_enabled_11.png").exists());
		this.cell("Resource Groups").click();
		this.cell("Users").click();
		this.cell("LDAP Groups").click();
		org.testng.Assert.assertTrue(this.cell("NOTE: The LDAP security integration is not configured. To configure LDAP, go to System Settings.").exists());
		this.cell("Save").click();

	}
	
	public void deleteRole(String roleName){
		this.link("Administration").click();
		this.cell("Roles").click();
		this.div(roleName).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
		
	}
	
	
	public void userCreationWithRoleVerification(String userName, String password, String firstname, String secondname, String email, String roleName, String roleDesc){
		createUser(userName, password,firstname,secondname, email);
		createRoleWithValidations(roleName,roleDesc);
		this.link("Administration").click();
		this.cell("Roles").click();
		this.cell("Users").click();
		org.testng.Assert.assertTrue(this.div(userName).exists());
//			this.div(userName).click();
//			this.image("right.png").click();
		this.div("testuser").doubleClick();
		this.cell("Save").click();
		deleteRole(roleName);
		deleteUser(userName);
		
		
	}
	
	
	//******************************************************************
	//Help
	//******************************************************************
	
	public void helpAbout() {
		this.link("Help").click();
		this.cell("About").click();
		this.cell("Close").click();
	}
	
    public void helpFAQ() {
    	this.link("Help").click();
    	this.cell("Frequently Asked Questions (FAQ)").click();
	}
    
    public void helpDocumentation() {
    	this.link("Help").click();
    	this.cell("Documentation Set").click();
	}
	
    
    public void helpAPI() {
    	this.link("Help").click();
    	this.cell("API Javadoc").click();
	}
    
    
    public void helpDemoAllDemos() {
    	this.link("Help").click();
    	this.cell("Demo: All Demos").click();
    	
	}
	
    public void helpDemoBundles() {
    	this.link("Help").click();
    	this.cell("Demo: Bundle Provisioning").click();
    	
	}
    
    public void helpHowToGroupDefinitions() {
    	this.link("Help").click();
    	this.cell("How to build Group Definitions").click();
	}
	
    
    public void helpHowToSearchBar() {
    	this.link("Help").click();
    	this.cell("How to use the Search Bar").click(); 
	}
    
    public void collapseExpandProduct() {
    	this.cell("Product").click();
    	this.cell("Product").click();
    }
    
    public void collapseExpandDocumentation() {
    	this.cell("Documentation").click();
    	this.cell("Documentation").click();
    }
    
    public void collapseExpandTutorial() {
    	this.cell("Tutorial").click();
    	this.cell("Tutorial").click();
    }


	//************************************************************
	// Recent Operations
	//*************************************************************
		
	public void createRecentOperationsSchedule(){
			
		this.link("Inventory").click();
		this.cell("Servers").click();
		this.link("RHQ Agent").click();
		this.cell("Operations").click();
		this.cell("New").click();
		this.div("selectItemText").setValue("g");
		this.waitFor(5000);
		this.div("selectItemText").setValue("g");			
		this.cell("Schedule").click();
		
	}
	
	public void deleteRecentOperationsSchedule(){
		this.link("Reports").click();
		this.cell("Recent Operations").click();
		this.div("RHQ Agent").click();
		this.cell("Delete").click();
		this.cell("Yes").click();
		this.link("Inventory").click();
		
	}
	
	public void recentOperationsForceDelete(){
		createRecentOperationsSchedule();
		this.link("Reports").click();
		this.cell("Recent Operations").click();
		this.div("RHQ Agent").click();
		this.cell("Force Delete").click();
		this.cell("Yes").click();
		this.link("Inventory").click();
				
	}
	
	public void recentOperationsQuickLinks(){
		createRecentOperationsSchedule();
		this.link("Reports").click();
		this.cell("Recent Operations").click();
		this.link("RHQ Agent").click();
		this.image("row_collapsed.png").click();
		this.cell("Recent Operations").click();
		this.div("Get Plugin Info").click();
		this.cell("Delete").click();
		this.cell("Yes").click();
		this.link("Inventory").click();
		
	}
	public void opreationsWithRefreshButtonFunctionality(){
		createRecentOperationsSchedule();
		this.link("Reports").click();
		this.cell("Recent Operations").click();
		this.cell("Refresh").click();
		this.div("Get Info On All Plugins").click();
		this.cell("Delete").click();
		this.cell("Yes").click();
		this.cell("Inventory").click();
		
		
	}
    
	
	//************************************************************
	// Drift Management
	//*************************************************************
	
	public void configure()  {
		
	}
	
	
	
	
	//************************************************************
	// Dashboard 
	//*************************************************************
	
	public boolean messagePortletExists()  {
		
		return this.cell("Message").isVisible();
		
	}
	
	public boolean inventorySummaryPortletExists()  {
		
		return this.cell("Inventory Summary").isVisible();
		
	}
	
	public boolean mashupPortletExists()  {
		
		return this.cell("Mashup").isVisible();
		
	}
	
	public boolean recentAlertsPortletExists()  {
		
		return this.cell("Recent Alerts").isVisible();
		
	}
	
	public boolean alertedOrUnavailableResourcesPortletExists()  {
		
		return this.cell("Alerted or Unavailable Resources").isVisible();
		
	}
	
	public boolean recentOperationsPortletExists()  {
		
		return this.cell("Recent Operations").isVisible();
		
	}
	
	public void messagePortletRefresh() {
		this.image("refresh.png").click();
	}
	
	public void messagePortletMinimizeMaximize () {
		this.image("cascade_Disabled.png").click();
		this.image("minimize_Disabled.png").click();
		
	}
	
	public boolean verifyInventorySummaryPortlet () {
		
		return this.cell("Platform Total :").isVisible() &&
		       this.cell("Server Total :").isVisible()   &&
		       this.cell("Service Total :").isVisible()  &&
		       this.cell("Compatible Group Total :").isVisible() &&
		       this.cell("Mixed Group Total :").isVisible()  &&
		       this.cell("Group Definition Total :").isVisible() &&
		       this.cell("Average Metrics per Minute :").isVisible()
		       ; 
		
	}
    
	public boolean verifyDefaultTabName () {
		return this.cell("Default").isVisible();
		
	}
	
	
	
}
