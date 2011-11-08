package com.redhat.qe.jon.sahi.tasks;

import com.redhat.qe.auto.sahi.ExtendedSahi;
import com.redhat.qe.auto.testng.Assert;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.annotations.Optional;

public class SahiTasks extends ExtendedSahi {

    private static Logger _logger = Logger.getLogger(SahiTasks.class.getName());

    public SahiTasks(String browserPath, String browserName, String browserOpt, String sahiBaseDir, String sahiUserdataDir) {
        super(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
    }

    // ***************************************************************************
    // Login/Logout
    // ***************************************************************************
    public boolean login(String userName, String password) {
    	if(!this.waitForElementExists(this, this.textbox("user"), "user", 1000*180)){
    		this.topLevelMenuDashboardExist();
    	}
        this.textbox("user").setValue(userName);
        this.password("password").setValue(password);
        this.cell("Login").click();
        return true;
    }

    public void logout() {
        this.link("Logout").click();
    }

    public void relogin(String userName, String password) {
        logout();
        login(userName, password);
    }

    // ***************************************************************************
    // Inventory
    // ***************************************************************************
    public void createGroup(String groupPanelName, String groupName, String groupDesc) {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        this.cell("New").click();
        this.textbox("name").setValue(groupName);
        this.textarea("description").setValue(groupDesc);
        this.cell("Next").click();
        this.cell("Finish").click();
    }

    public void createGroup(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList) {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        this.cell("New").click();
        this.textbox("name").setValue(groupName);
        this.textarea("description").setValue(groupDesc);
        this.cell("Next").click();
        for (String resource : resourceList) {
            this.textbox("search").setValue(resource);
            this.waitFor(5000);
            this.xy(this.byText(resource.trim(), "nobr"), 3,3).doubleClick();
            //this.div(resource).doubleClick();
            //this.waitFor(1000);
            //this.image("right.png").click();
            // TODO: Verification of item actually being added, if not wrap ^
            // in a loop until it's added.
            // i.e. System.out.println(this.div(resource).parentNode("sectionStack").style("position"));
        }
        this.cell("Finish").click();
    }

    public void createDynaGroup(String groupName, String groupDesc, ArrayList<String> preloadExpressions, String otherExpressions) {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell("Dynagroup Definitions").click();
        this.cell("New").click();
        this.textbox("name").setValue(groupName);
        this.textarea("description").setValue(groupDesc);
        this.textarea("expression").setValue(otherExpressions);
        // TODO: Commented out below because we can't locate the elements via. sahi atm. 
		/*
        for (String exp : preloadExpressions) {
        //this.image("isc_HU").click();
        this.image("Saved Expression").click();
        this.cell(exp).click();
        }
        this.cell("isc_I7").click(); // recursive
        this.image("isc_I9").click(); // refresh timer set to 1m
         */
        this.cell("Save").click();
        this.cell("Back to List").click();
    }

    public boolean verifyGroup(String groupPanelName, String groupName) {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        return this.div(groupName).exists();
    }

    public void deleteGroup(String groupPanelName, String groupName) {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        this.div(groupName).click();
        this.cell("Delete").click();
        this.cell("Yes").click();
    }

    /*
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
     */
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
        this.textbox("firstName").setValue("testfirstName");
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
    public void checkResourceBrowserAvailabilityColumnsInGroupDef() {

        this.link("Inventory").click();
        this.cell("Dynagroup Definitions").click();
        this.cell("Name").exists();
        org.testng.Assert.assertTrue(this.cell("Description").exists());
        org.testng.Assert.assertTrue(this.cell("Description").exists());
        org.testng.Assert.assertTrue(this.cell("Expression Set").exists());
        org.testng.Assert.assertTrue(this.cell("Last Calculation Time").exists());
    }

    public void checkResourceBrowserAvailabilityColumnsInEachGroup() {
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

    public void checkSearchTextBoxInEachResourceBrowserGroup() {
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

    public void createDyanGroup(String groupName, String groupDesc) {
        this.link("Inventory").click();
        this.cell("Dynagroup Definitions").click();
        this.cell("New").click();
        this.textbox("name").setValue(groupName);
        this.textarea("description").setValue(groupDesc);
        this.textarea("expression").setValue("groupby resource.trait[jboss.system:type=Server:VersionName]\nresource.type.plugin = JBossAS\nresource.type.name = JBossAS Server");
        this.cell("Save & Recalculate").click();
        org.testng.Assert.assertTrue(this.waitForElementExists(this, this.cell("You have successfully recalculated this group definition"), "Cell: You have successfully recalculated this group definition", 1000*20), "Successful message check"); //Wait 20 seconds
        this.bold("Back to List").click();
        this.link(groupName).click();
        this.bold("Back to List").click();
    }

    public void deleteDynaGroup(String groupDesc) {
        this.link("Inventory").click();
        this.cell("Dynagroup Definitions").click();
        this.div(groupDesc).click();
        this.cell("Delete").click();
        this.cell("Yes").click();
    }

    public void resourceSearch(String searchTestuser, String password, String firstName, String secondName, String emailId, String searchRoleName, String desc, String compGroupName, String searchQueryName) {
        //createCompatibleGroup(compGroupName, desc);
        createUser(searchTestuser, password, firstName, secondName, emailId);
        createRoleWithoutMangeInvetntory(searchRoleName, desc, compGroupName, searchTestuser);
        loginNewUser(searchTestuser, password);
        navigateToAllGroups();
        enterValueToSerachTextBox(compGroupName, searchQueryName);

    }

    public void createRoleWithoutMangeInvetntory(String roleName, String desc, String compGroupName, String searchTestuser) {
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

    public void loginNewUser(String newUser, String password) {
        this.textbox("user").setValue(newUser);
        this.password("password").setValue(password);
        this.cell("Login").click();

    }

    public void navigateToAllGroups() {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell("All Groups").click();
    }

    public void enterValueToSerachTextBox(String compGroupName, String searchName) {
        this.textbox("SearchPatternField").setValue(searchName + "=" + compGroupName);
        this.cell("name").click();
    }

    // ***************************************************************************
    // Dashboard
    // ***************************************************************************
    public void clickDashboardTopLevelMenu() {
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
    public void createUser(String userName, String password, String firstName, String lastName, String email) {
        this.link("Administration").click();
        this.cell("Users").click();
        this.cell("New").click();
        this.textbox("name").setValue(userName);
        this.password("password").setValue(password);
        this.password("passwordVerify").setValue(password);
        this.textbox("firstName").setValue(firstName);
        this.textbox("lastName").setValue(lastName);
        this.textbox("emailAddress").setValue(email);
        this.cell("Save").click();
    }

    public void deleteUser(String userName) {
        this.link("Administration").click();
        this.cell("Users").click();
        this.div(userName).click();
        this.cell("Delete").click();
        this.cell("Yes").click();
    }

    public void createRole(String roleName, String roleDesc) {
        this.link("Administration").click();
        this.cell("Roles").click();
        this.cell("New").click();
        this.textbox("name").setValue(roleName);
        this.textbox("description").setValue("Description");

        // TODO: Can't automate permission because we have no good 
        // way to associate the permission name and the checkbox 
        // that's next to it.
        // One workaround is to hard code the permission list.

        this.cell("Save").click();
    }

    public void deleteRole(String roleName) throws SahiTasksException  {
        this.link("Administration").click();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        /*if(!this.waitForElementExists(this, this.cell("Roles"), "Roles", 1000*30)){ //wait time up to 30 seconds
        	throw new SahiTasksException("Element is not available [element name: Roles]");
        }*/
        this.cell("Roles").click();
        if(this.span("Administration").exists()){
        	this.cell("Roles").click();
        }
        this.div(roleName).click();
        this.cell("Delete").click();
        this.cell("Yes").click();
    }

    public void addRolesToUser(String userName, ArrayList<String> roleNames) {
        this.link("Administration").click();
        this.cell("Users").click();
        this.link(userName).click();
        for (String role : roleNames) {
            this.div(role).click();
            this.image("right.png").click();
        }
        this.cell("Save").click();
    }

    public boolean verifyUserRole(String userName, String password, ArrayList<String> roleNames) {
        relogin(userName, password);

        // TODO: Verification of role still needs to be done, however this 
        // hinges on whether we can create a role w/ specific permission 
        // by specifying only the permission name.

        relogin("rhqadmin", "rhqadmin"); // reset
        return true;
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
    public void createRecentOperationsSchedule() {

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

    public void deleteRecentOperationsSchedule() {
        this.link("Reports").click();
        this.cell("Recent Operations").click(); 
        this.div("RHQ Agent").click();
        this.cell("Delete").click();
        this.cell("Yes").click();
        this.link("Inventory").click();

    }

    public void recentOperationsForceDelete() {
        createRecentOperationsSchedule();
        this.link("Reports").click();
        this.cell("Recent Operations").click();
        this.div("RHQ Agent").click();
        this.cell("Force Delete").click();
        this.cell("Yes").click();
        this.link("Inventory").click();

    }

    public void recentOperationsQuickLinks() {
        createRecentOperationsSchedule();
        this.link("Reports").click();
        this.cell("Recent Operations").click();
        this.link("RHQ Agent").click();
        this.image("row_collapsed.png").click();
        this.link("Reports").click();
        this.cell("Recent Operations").click();
        this.div("Get Info On All Plugins").click();
        this.cell("Delete").click();
        this.cell("Yes").click();
        this.link("Inventory").click();

    }

    public void opreationsWithRefreshButtonFunctionality() {
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
    public void configure() {
    }

    //************************************************************
    // Dashboard 
    //*************************************************************
    public boolean messagePortletExists() {

        return this.cell("Message").isVisible();

    }

    public boolean inventorySummaryPortletExists() {

        return this.cell("Inventory Summary").isVisible();

    }

    public boolean mashupPortletExists() {

        return this.cell("Mashup").isVisible();

    }

    public boolean recentAlertsPortletExists() {

        return this.cell("Recent Alerts").isVisible();

    }

    public boolean alertedOrUnavailableResourcesPortletExists() {

        return this.cell("Alerted or Unavailable Resources").isVisible();

    }

    public boolean recentOperationsPortletExists() {

        return this.cell("Recent Operations").isVisible();

    }

    public void messagePortletRefresh() {
        this.image("refresh.png").click();
    }

    public void messagePortletMinimizeMaximize() {
        this.image("cascade_Disabled.png").click();
        this.image("minimize_Disabled.png").click();

    }

    public boolean verifyInventorySummaryPortlet() {

        return this.cell("Platform Total :").isVisible()
                && this.cell("Server Total :").isVisible()
                && this.cell("Service Total :").isVisible()
                && this.cell("Compatible Group Total :").isVisible()
                && this.cell("Mixed Group Total :").isVisible()
                && this.cell("Group Definition Total :").isVisible()
                && this.cell("Average Metrics per Minute :").isVisible();

    }

    public boolean verifyDefaultTabName() {
        return this.cell("Default").isVisible();

    }

    //************************************
    //* Favourite
    //*************************************
    public void createFavourite() {
    	selectResource("Servers=RHQ Agent");
        this.image("Favorite_24.png").click();
    }

    public void removeFavourite() {
    	selectResource("Servers=RHQ Agent");
        this.image("Favorite_24_Selected.png").click();
    }

    public boolean checkFavouriteBadgeForAgent() {
        return this.image("Favorite_24_Selected.png").isVisible();
    }

    public boolean checkBadgeAfterRmovingFavourite() {
        return this.image("Favorite_24.png").isVisible();

    }

    //********************************************************
    // Plugins Verification
    //********************************************************
    public void locatePluginPage(boolean selectAgentPluginPage){
    	this.link("Administration").click();
    	this.waitForElementExists(this, this.span("Administration"), "Administration", 1000*5);
    	if(selectAgentPluginPage){
    		this.cell("Agent Plugins").click();
    		this.waitForElementExists(this, this.cell("Name"), "Name", 1000*3);
    	}else{
    		this.cell("Server Plugins").click();
    		this.waitForElementExists(this, this.cell("Name"), "Name", 1000*3);
    	}
    }
    
    public boolean getAgentServerPluginsStaus(String pluginsName, boolean redirectPage, boolean isAgentPlugins){
    	if(redirectPage){
    		locatePluginPage(isAgentPlugins);
    	}    	
        return this.link(pluginsName).exists();
    }
    
    //*********************************************************************************
    //* Alert Definition Creation
    //*********************************************************************************
    public void selectResource(String resourceName){
    	this.link("Inventory").click();
    	String searchCategory = null;
        String[] resourceType = resourceName.split("=");
        if (resourceType.length > 1) {
            this.cell(resourceType[0].trim()).click();
            if(resourceType[0].equalsIgnoreCase("Platforms")){
            	searchCategory = "category=platform ";
            }else if(resourceType[0].equalsIgnoreCase("Servers")){
            	searchCategory = "category=server ";
            }else if(resourceType[0].equalsIgnoreCase("Services")){
            	searchCategory = "category=service ";
            }else if(resourceType[0].equalsIgnoreCase("Unavailable Servers")){
            	searchCategory = "category=server availability=down ";
            }
            this.textbox("SearchPatternField").setValue(searchCategory+resourceType[1].trim());
            this.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);"); //13 - Enter key
        } else {
            _logger.log(Level.WARNING, "Invalid parameter passed --> "+resourceName);
            //throw new SahiTasksException("Invalid parameter passed --> "+resourceName);
            return;
        }
        this.link(resourceType[1].trim()).click();
    }
    public void gotoAlertDefinationPage(String resourceName, boolean definitionsPage) {
    	selectResource(resourceName);
        this.cell("Alerts").click();
        if (definitionsPage) {
            this.xy(this.cell("Definitions"), 3, 3).click();
        } else {
            this.xy(this.cell("History"), 3, 3).click();
        }
    }

    public void selectComboBoxes(String options) {
        if (options != null) {
            if (options.trim().length() > 0) {
                String[] optionArray = this.getCommaToArray(options);
                for (String option : optionArray) {
                    String[] optionTmp = option.split("-->");
                    if (this.div(optionTmp[0].trim() + "[1]").exists()) {
                        _logger.info("\"" + optionTmp[0].trim() + "[1]\" is available to select");
                        this.selectComboBoxDivRow(this, optionTmp[0].trim() + "[1]", optionTmp[1].trim());
                    } else {
                        this.selectComboBoxDivRow(this, optionTmp[0].trim(), optionTmp[1].trim());
                    }
                }
            }
        }
    }

    private void updateSystemUserNotification(String users) {
        String[] usersArray = this.getCommaToArray(users);
        for (String user : usersArray) {
            this.byText(user.trim(), "nobr").doubleClick();
        }
    }

    private int getNumberAlert(String alertName) {
        return this.link(alertName).countSimilar();
    }

    private void updateTextBoxValues(String textBoxKeyValue) {
        if (textBoxKeyValue != null) {
            if (textBoxKeyValue.trim().length() > 0) {
                HashMap<String, String> keyValueMap = this.getKeyValueMap(textBoxKeyValue);
                Set<String> keys = keyValueMap.keySet();
                for (String key : keys) {
                	try{
                		this.textbox(Integer.parseInt(key)).setValue(keyValueMap.get(key));
                	}catch(Exception ex){
                		this.textbox(key).setValue(keyValueMap.get(key));
                	}
                    
                    _logger.log(Level.INFO, "Updated textbox:["+key+"="+keyValueMap.get(key)+"]");
                }
            }
        }
    }
    
    private void updateRadioButtons(String radioButtons){
    	String[] radioButtonsArray = this.getCommaToArray(radioButtons);
    	for(int i=0; radioButtonsArray.length > i; i++){
        	this.radio(radioButtonsArray[i].trim()).check();
        	_logger.log(Level.INFO, "Radio Button \""+radioButtonsArray[i].trim()+"\" selected");
        }
    }    

    private void selectYesNoradioButtons(String buttonKeyValue) {
        if (buttonKeyValue != null) {
            if (buttonKeyValue.trim().length() > 0) {
                HashMap<String, String> keyValueMap = this.getKeyValueMap(buttonKeyValue);
                Set<String> keys = keyValueMap.keySet();
                for (String key : keys) {
                    if (keyValueMap.get(key).equalsIgnoreCase("yes")) {
                        this.radio(key).check();
                    } else {
                        this.radio(key + "[1]").check();
                    }
                }
            }
        }
    }

    public int createAlert(@Optional String resourceName, String alertName, @Optional String alertDescription, String conditionsDropDown, @Optional String conditionTextBox, String notificationType, String notificationData, @Optional String dampeningDropDown, @Optional String dampeningTextBoxData, @Optional String recoveryAlertDropDown, @Optional String disableWhenFired) {

        //Select Resource to define alert
        if (resourceName != null) {
            gotoAlertDefinationPage(resourceName, true);
        }

        //Take current status
        int similarAlert = getNumberAlert(alertName);
        _logger.info("pre-status of Alert definition [" + alertName + "]: " + similarAlert + " definition(s)");

        //Define new alert name and Description(if any)
        this.cell("New").click();
        this.textbox(0).setValue(alertName);
        if (alertDescription != null) {
            this.textarea(0).setValue(alertDescription);
        }

        //Add conditions
        this.cell("Conditions").click();
        this.cell("Add").click();

        selectComboBoxes(conditionsDropDown);
        updateTextBoxValues(conditionTextBox);


        this.cell("OK").click();

        //Add notifications
        this.cell("Notifications").click();
        this.cell("Add[1]").click();
        //Select Notification type
        if (notificationType.equalsIgnoreCase("System Users")) {
            updateSystemUserNotification(notificationData);
        } else {
            _logger.log(Level.WARNING, "Undefined notification type: " + notificationType);
        }
        this.cell("OK").click();

        //Recovery
        this.cell("Recovery").click();
        selectComboBoxes(recoveryAlertDropDown);
        selectYesNoradioButtons(disableWhenFired);


        //Dampening
        this.cell("Dampening").click();
        selectComboBoxes(dampeningDropDown);
        updateTextBoxValues(dampeningTextBoxData);

        //Final step
        this.xy(this.cell("Save"), 3, 3).click();
        this.bold("Back to List").click();

        return getNumberAlert(alertName) - similarAlert;
    }

    //*********************************************************************************
    //* Alert History Validation 
    //*********************************************************************************
    public int validateAlertHistory(@Optional String resourceName, String alertName) {

        //Select Resource to define alert
        if (resourceName != null) {
            gotoAlertDefinationPage(resourceName, false);
        }

        //Get Number count from Alert History history
        return this.link(alertName).countSimilar();
    }
    
    //*********************************************************************************
    //* Alert Definition Deletion 
    //*********************************************************************************
    public boolean deleteAlertDefinition(@Optional String resourceName, String alertName) {
        if (resourceName != null) {
            gotoAlertDefinationPage(resourceName, true);
        }        
        int numberOfDefinitions = this.link(alertName).countSimilar();
        _logger.log(Level.INFO, "[Before Deletion] \""+alertName+"\" definition count: "+numberOfDefinitions);
        if(this.div(alertName+"[1]").exists()){	
        	this.div(alertName+"[1]").click(); //Selecting the definition
        }else{
        	this.div(alertName).click(); //Selecting the definition
        }        
        this.cell("Delete[4]").click();
        this.cell("Yes").click();        
        int numberOfDefinitionsUpdated = this.link(alertName).countSimilar();
        _logger.log(Level.INFO, "[After Deletion] \""+alertName+"\" definition count: "+numberOfDefinitionsUpdated);
        if((numberOfDefinitions - numberOfDefinitionsUpdated) == 1){
        	return true;
        }else{
        	return false;
        }
    }
 
    //*********************************************************************************
    //* Alert History Deletion
    //*********************************************************************************
    public boolean deleteAlertHistory(@Optional String resourceName, String alertName) {
        if (resourceName != null) {
            gotoAlertDefinationPage(resourceName, false);
        }        
        int numberOfHistory = this.link(alertName).countSimilar();
        _logger.log(Level.INFO, "[Before Deletion] \""+alertName+"\" history count: "+numberOfHistory);
        if(this.div(alertName+"[1]").exists()){
        	this.div(alertName+"[1]").click(); //Selecting the history
        }else{
        	this.div(alertName).click(); //Selecting the history
        }    
        this.cell("Delete[3]").click();
        this.cell("Yes").click();
        int numberOfHistoryUpdated = this.link(alertName).countSimilar();
        _logger.log(Level.INFO, "[After Deletion] \""+alertName+"\" history count: "+numberOfHistoryUpdated);
        if((numberOfHistory - numberOfHistoryUpdated) == 1){
        	return true;
        }else{
        	return false;
        }
    }
   
    //**************************************************************************************************
    //* Get GWT table information 
    //**************************************************************************************************
    @SuppressWarnings("unchecked")
	public LinkedList<HashMap<String, String>> getRHQgwtTableDetails(String tableName, int tableCountOffset, String columnsCSV, String replacementKeyValue) {
    	int noListTables = this.table(tableName).countSimilar()-tableCountOffset;
    	LinkedList<HashMap<String, String>> rows = new LinkedList<HashMap<String,String>>();
    	HashMap<String, String> row = new HashMap<String, String>();
    	String[] columns = getCommaToArray(columnsCSV);
    	HashMap<String, String> replacement = getKeyValueMap(replacementKeyValue);
    	String innerHTMLstring;
    	String textString;
    	for(int i=0; ;i++){
    		try{
    			for(int c=0; c<columns.length; c++){
    				ElementStub categoryElement = cell(table(tableName+"["+(noListTables-1)+"]"),i, c);
    				innerHTMLstring = categoryElement.fetch("innerHTML");
    				textString = categoryElement.getText();
    				if(innerHTMLstring.contains("src=") && (textString.length() == 0)){
    					innerHTMLstring = innerHTMLstring.substring(innerHTMLstring.indexOf("src=\"")+5, innerHTMLstring.indexOf('"', innerHTMLstring.indexOf("src=\"")+5));
    					row.put(columns[c], innerHTMLstring.substring(innerHTMLstring.lastIndexOf('/')+1));
    				}else{
    					row.put(columns[c], textString);
    				}
      				if(replacement.get(row.get(columns[c])) != null){
    					row.put(columns[c], replacement.get(row.get(columns[c])));
    				}
    			}    			
    		}catch (Exception ex){
    			_logger.log(Level.FINER, "Known Exception: "+ex.getMessage());
    			break;
    		}
    		rows.addLast((HashMap<String, String>) row.clone());
    		row.clear();
    	}    	
    	_logger.log(Level.FINER, "Table Details: "+rows);
		return rows;    	
    }
    
    //*********************************************************************************
    //* Drift Management Add Drift on GUI
    //*********************************************************************************
    public void gotoDriftDefinationPage(String resourceName, boolean definitionsPage) {    	
        if (definitionsPage) {
        	selectResource(resourceName);
            this.cell("Drift").click();
            //this.xy(this.cell("Definitions"), 3, 3).click();
        } else {
        	this.link("Reports").click();
        	this.cell("Recent Drift").click();
            //this.xy(this.cell("History"), 3, 3).click();
        }
    }
    
    public boolean clickDriftDetectNowOrDelete(String driftName, int divMaxIndex, long waitTime, boolean deleteDrift) throws InterruptedException{
    	
    	String driftRealName = "";
    	for(int i=divMaxIndex; i>=0; i--){
    		if(this.div(driftName+"["+i+"]").exists()){
    			this.div(driftName+"["+i+"]").click();
    			_logger.log(Level.INFO, "Clciked on, Drift Name:  "+driftName+"["+i+"]");
    			driftName = driftRealName+"["+i+"]";
    			break;
    		}
    	}
    	//this.div(driftName, this.link(driftName)).click();
    	
    	if(deleteDrift){
    		this.cell("Delete").near(this.cell("Delete All")).click();
    		//this.row("Delete[2]").click();
    		this.cell("Yes").click();
    		return this.link(driftName).exists();
    	}else{
    		this.cell("Detect Now").near(this.cell("Delete All")).click();
        	_logger.log(Level.FINER, "Waiting "+(waitTime/1000)+" Second(s) for agent/server actions...");
        	Thread.sleep(waitTime); //Give X second(s) for agent/server actions
        	return true;
    	}
    	
    }
    public boolean addDrift(String baseDir, String resourceName, String templateName, String driftName, String textBoxKeyValue, String radioButtons, String fileIncludes, String fileExcludes ) throws InterruptedException, IOException {
    	//Remove old file History If any
    	DriftManagementSSH driftSSH = new DriftManagementSSH();
		driftSSH.getConnection(System.getenv().get("AGENT_NAME"), System.getenv().get("AGENT_HOST_USER"), System.getenv().get("AGENT_HOST_PASSWORD"));
		if(!driftSSH.deleteFilesDirs(baseDir)){
			return false;
		}
		driftSSH.closeConnection();
		
        //Select Resource
        if (resourceName != null) {
        	gotoDriftDefinationPage(resourceName, true);
        }
        
        this.cell("New").click();
        
        //Select Template
        if(templateName != null){
        	selectComboBoxes(templateName);
        }
        
        this.cell("Next").click();
        
        //Update Drift Name
        this.textbox("name").setValue(driftName.trim());
        
        //Update text Boxes
        updateTextBoxValues(textBoxKeyValue);
        
        //Select Radio Buttons
        updateRadioButtons(radioButtons);
        
        //File name Includes
        if(fileIncludes != null){
        	String[] files = this.getCommaToArray(fileIncludes);
        	for(String fileName : files){
        		this.image("add.png[1]").focus();
                this.execute("_sahi._keyPress(_sahi._image('add.png[1]'), 32);"); //32 - Space bar
                this.textbox("path").setValue(fileName.trim());
                _logger.log(Level.INFO, "File Name added [Includes]: "+fileName);
                this.cell("OK").click();   
        	}        	     	
        }        
        
        //File Excludes
        if(fileExcludes != null){
        	String[] files = this.getCommaToArray(fileExcludes);
        	for(String fileName : files){
        		this.image("add.png[2]").focus();
                this.execute("_sahi._keyPress(_sahi._image('add.png[2]'), 32);"); //32 - Space bar
                this.textbox("path").setValue(fileName.trim());
                _logger.log(Level.INFO, "File Name added [Excludes]: "+fileName);
                this.cell("OK").click();
        	}    	
        } 
        this.cell("Finish").click();
        
        if(this.link(driftName.trim()).exists()){
        	_logger.log(Level.INFO, "Drift Name ["+driftName.trim()+"] added successfully.");
            //Do Manual 'Detect Now'
            clickDriftDetectNowOrDelete(driftName, 2, 1000*65, false);
        	return true;
        }        
        return false;
    }

    //***************************************************************************************
    //* Get Drift History tables
    //***************************************************************************************
	public LinkedList<HashMap<String, String>> getDriftManagementHistory(String resource, int tableCountOffset) throws InterruptedException{
    	if(resource != null){
    		gotoDriftDefinationPage(resource, false);
    	}    	
    	Thread.sleep(1000);
    	return getRHQgwtTableDetails("listTable", tableCountOffset, "CreationTime,Definition,Snapshot,Category,Path,Resource,Ancestry", "Drift_add_16.png=added,Drift_change_16.png=changed,Drift_remove_16.png=removed");
    }
    
    //*********************************************************************************
    //* Drift Management add/change/remove Files
    //*********************************************************************************
       
    private boolean fileAdditionDeletionChangeOnDrift(String baseDir, HashMap<String, String>files, Set<String> fileKeys, String fileAction, DriftManagementSSH driftSSH){
    	for (String key : fileKeys) {
    		_logger.info("File: "+key);
    		if(fileAction.equalsIgnoreCase("added")){
    			if(!driftSSH.createFileDir(baseDir+key.substring(0,key.lastIndexOf("/")))){
    				return false;
    			}
    			if(!driftSSH.addLineOnFile(baseDir+key, files.get(key), false)){
    				return false;
    			}
    		}else if(fileAction.equalsIgnoreCase("changed")){
    			if(!driftSSH.createFileDir(baseDir+key.substring(0,key.lastIndexOf("/")))){
    				return false;
    			}
    			if(!driftSSH.addLineOnFile(baseDir+key, files.get(key), true)){
    				return false;
    			}
    		}else if(fileAction.equalsIgnoreCase("removed")){
    			if(!driftSSH.deleteFilesDirs(baseDir+key)){
    				return false;
    			}
    		}
    	}
		return true;
    }
    
    private boolean checkAvailabilityOnDriftHistory(Set<String> fileKeys, LinkedList<HashMap<String, String>> driftHistory, String category){
        for(String key : fileKeys){
        	boolean status = false;
        	for(HashMap<String, String> singleRow : driftHistory){
        		if(singleRow.get("Path").equals(key)){
        			if(singleRow.get("Category").equalsIgnoreCase(category)){
        				_logger.log(Level.INFO, "Drift Change available on History[include File: "+key+"]: "+singleRow.get("Path"));  
        				status = true;
        				break;
        			}
        		}
        	}
        	if(!status){
    			return false;
    		}
        }  	
        return true;
    }
    public boolean addChangeRemoveDriftFile(String resourceName, String driftName, String baseDir, String fileIncludes, String fileExcludes, String fileAction ) throws InterruptedException, IOException {
    	if(!baseDir.endsWith("/")){
    		baseDir += "/";
    	}
    	//Add files on back-end
		DriftManagementSSH driftSSH = new DriftManagementSSH();
		driftSSH.getConnection(System.getenv().get("AGENT_NAME"), System.getenv().get("AGENT_HOST_USER"), System.getenv().get("AGENT_HOST_PASSWORD"));
		
		// Include files action
		HashMap<String, String>filesInclude = new HashMap<String,String>();
		filesInclude = getKeyValueMap(fileIncludes);
		Set<String> includeFileKeys = filesInclude.keySet();
		
		if(!fileAdditionDeletionChangeOnDrift(baseDir, filesInclude, includeFileKeys, fileAction, driftSSH)){
			return false;
		}
		
		// Exclude files action
		HashMap<String, String>filesExclude = new HashMap<String,String>();
		filesExclude = getKeyValueMap(fileExcludes);
		Set<String> excludeFileKeys = filesExclude.keySet();
		
		if(!fileAdditionDeletionChangeOnDrift(baseDir, filesExclude, excludeFileKeys, fileAction, driftSSH)){
			return false;
		}
		
				
		driftSSH.closeConnection();
		
		
		//Select Resource
        if (resourceName != null) {
        	gotoDriftDefinationPage(resourceName, true);
        }else{
        	this.xy(this.cell("Definitions"), 3, 3).click();
        }
        
        //Do Manual 'Detect Now'
        clickDriftDetectNowOrDelete(driftName, 3, 1000*65, false);
       
        // Redirect to history Page
        //this.xy(this.cell("History"), 3, 3).click();
        
        LinkedList<HashMap<String, String>> driftHistory = getDriftManagementHistory(resourceName, 2);
        
       
        //IncludeFile Test
        if(!checkAvailabilityOnDriftHistory(includeFileKeys, driftHistory, fileAction)){
        	return false;
        }
              
        //ExcludeFile Test
        if(checkAvailabilityOnDriftHistory(excludeFileKeys, driftHistory, fileAction)){
        	return false;
        }     
       
        return true;
    }

    //***************************************************************************************************
    //* Delete Drift
    //***************************************************************************************************
    public boolean deleteDrift(String resourceName, String driftName, String baseDir) throws InterruptedException, IOException{
    	//Select Resource
        if (resourceName != null) {
        	gotoDriftDefinationPage(resourceName, true);
        }else{
        	this.xy(this.cell("Definitions"), 3, 3).click();
        }
    	//Delete files on back-end
		DriftManagementSSH driftSSH = new DriftManagementSSH();
		driftSSH.getConnection(System.getenv().get("AGENT_NAME"), System.getenv().get("AGENT_HOST_USER"), System.getenv().get("AGENT_HOST_PASSWORD"));
		if(!driftSSH.deleteFilesDirs(baseDir)){
			return false;
		}
		driftSSH.closeConnection();
		return clickDriftDetectNowOrDelete(driftName, 7, 0, true);
    }
    
    
    //***********************************************************************
    // Individual Config
    //***************************************************************************
    
    public void navigationToConfiguration(){
    	 this.link("Inventory").click();
         this.waitFor(5000);
         this.cell("Servers").click();
         this.link("RHQ Agent").click();
         this.cell("Configuration").click();
         
    }
    
    public void navigationToConfigurationSubtabs(){
    	navigationToConfiguration();
        this.xy(cell("History"), 3,3).click();
        this.xy(cell("Current"),3,3).click();
    }
    
    public void editAndSaveConfiguration(){
    	this.link("Inventory").click();
        this.waitFor(5000);
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.cell("Configuration").click();
        String defaultDelay = this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").getValue();
        this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").setValue(defaultDelay + "1");
        this.cell("Save").click();
                	
        }
    public void settingToOriginalValues(String defaultValue){
    	this.link("Inventory").click();
        this.waitFor(5000);
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.cell("Configuration").click();
        this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").setValue(defaultValue);
        this.cell("Save").click();
    	
    }

	    
    //******************************************************************************
    //Configuration History
    //*******************************************************************************
    
    public void navigationToConfigurationHistoryTab(){
    	this.link("Reports").click();
        this.waitFor(5000);
        this.cell("Configuration History").click();
    	
    }
    public void deleteConfigurationFromList(){
    	this.link("Reports").click();
        this.waitFor(5000);
        this.cell("Configuration History").click();
        this.div("Individual").click();
        this.cell("Delete").click();
        this.cell("Yes").click();
    }

 
    //************************************************************************************************
    // Metric Collection Schedules For Reousrces
    //*********************************************************************************************
    
    public void scheduleEnableForResource(){
        this.link("Inventory").click();
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.cell("Monitoring").click();
        this.xy(cell("Schedules"), 3,3).click();
        this.div("JVM Total Memory[1]").click();
        this.cell("Enable").click();
        
    }
    public void disableScheduleResource(){
        this.link("Inventory").click();
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.cell("Monitoring").click();
        this.xy(cell("Schedules"), 3,3).click();
        this.div("JVM Total Memory[1]").click();
        this.cell("Disable").click();
        
    }
    public void refreshScheduledResource(){
        this.link("Inventory").click();
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.cell("Monitoring").click();
        this.xy(cell("Schedules"), 3,3).click();
        this.div("JVM Total Memory[1]").click();
        this.cell("Refresh").click();
        
    }
    public void setCollectionIntervalForScheduledResource(int value){
        this.link("Inventory").click();
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.cell("Monitoring").click();
        this.xy(cell("Schedules"), 3,3).click();
        this.div("JVM Total Memory[1]").click();
        this.textbox("interval").setValue(Integer.toString(value));
        this.cell("Set").click();
    }

    //************************************************************************************************
    // Group Configuration
    //***************************************************************************************************
    
    public void navigationToGroupConfigurtion(String panelName, String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
    	createGroup(panelName, compatibleGroup, groupDesc,  resourceList);
    	this.link(compatibleGroup).click();
        this.cell("Configuration").click();
        
    }
    public void navigationToGroupConfigurationSubtabs(){
    	this.xy(cell("History"), 3,3).click();
    	this.xy(cell("Current"),3,3).click();
    }
    public void editAndSaveGroupConfiguration(){
    	String defaultDelay = this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").getValue();
        this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").setValue(defaultDelay + "1");
        this.cell("Save").click();
    	
    }

    //************************************************************************************************
    // Metric Collection Schedules For Groups
    //*********************************************************************************************
    
    public void scheduleEnableForGroup(String panelName, String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
    	createGroup(panelName, compatibleGroup, groupDesc,  resourceList);
    	this.link(compatibleGroup).click();
    	this.cell("Monitoring").click();
        this.xy(cell("Schedules"), 3,3).click();
        this.div("JVM Total Memory[1]").click();
        this.cell("Enable").click();      
        
    }
    public void disableScheduleGroup(){
    	this.div("JVM Total Memory[1]").click();
        this.cell("Disable").click();
        
    }
    public void refreshScheduledGroup(){
    	this.div("JVM Total Memory[1]").click();
        this.cell("Refresh").click();
        
    }
    public void setCollectionIntervalForScheduledGroup(int value){
        this.div("JVM Total Memory[1]").click();
        this.textbox("interval").setValue(Integer.toString(value));
        this.cell("Set").click();
    }
    
    public void deleteCompatibilityGroup(String panelName, String groupName){
   	 deleteGroup(panelName, groupName);
   	
   }
    
    //***********************************************************************************************
    //* Administration Page(s) validation [JSP pages]
    //***********************************************************************************************
    
    public void navigateToAdministrationPage(String pageLocation, String tableName){
    	this.link("Administration").click();
    	this.waitForElementExists(this, this.span("Administration"), "Administration", 1000*20);
        this.cell(pageLocation).click();
        this.waitForElementExists(this, this.table(tableName), tableName, 1000*20); //JSP pages are not a AJAX pages, hence waiting for an element (table name) on JSP page
        _logger.log(Level.FINE, "Page redirected to: Administration-->"+pageLocation);
    }
    public boolean validatePage(String page, String tableName, String tableColumns, int columnIndexFrom, int minRowCount){
    	navigateToAdministrationPage(page, tableName);
    	LinkedList<String> header = getTableHeader(null);
    	LinkedList<LinkedList<String>> tableContent = getJSPtable(tableName, header.size(), columnIndexFrom);
    	
    	StringBuffer tableDetails = new StringBuffer("***********Friendly Tabele Details***************\n");
    	tableDetails.append("Table Name: ").append(tableName).append("\n");
    	tableDetails.append("Header Count: ").append(header.size()).append("\n");
    	tableDetails.append("Row Count: ").append(tableContent.size()).append("\n");
    	tableDetails.append("Header(s): ").append(header).append("\n");
    	for(int i=0;i<tableContent.size();i++){
        		tableDetails.append("Row [").append((i+1)).append(" of ").append(tableContent.size()).append("]: ").append(tableContent.get(i)).append("\n");
    	}
    	tableDetails.append("*********************************************************");    	
    	_logger.log(Level.INFO, tableDetails.toString());
    	String[] columns = getCommaToArray(tableColumns);
    	for(String column: columns){
    		header.remove(column.trim());
    	}    	
    	return ((minRowCount <= tableContent.size()) && (header.size() == 0));
    }
    public LinkedList<String> getTableHeader(String tableName){
    	LinkedList<String> header = new LinkedList<String>();
    	String headerText = "headerText";
    	int headerCount = this.span(headerText).countSimilar();
    	_logger.log(Level.FINE, "Count: "+headerCount);
    	for(int i=0; i<headerCount;i++){
    		header.addLast(this.span(headerText+"["+i+"]").getText());
    	}
    	return header;
    }
    
    @SuppressWarnings("unchecked")
	public LinkedList<LinkedList<String>> getJSPtable(String tableName, int headerSize, int columnIndexFrom){
    	LinkedList<LinkedList<String>> table = new LinkedList<LinkedList<String>>();
    	LinkedList<String> row = new LinkedList<String>();
    	for(int i=1; ;i++){
    		try{
    			if(columnIndexFrom == 0){
    				headerSize -=1;
    			}
    			for(int h=columnIndexFrom; h<=headerSize; h++){
    				row.addLast(cell(table(tableName), i, h).getText());
    			}
    			table.addLast((LinkedList<String>) row.clone());
    			row.clear();
    		}catch (Exception ex){
    			_logger.log(Level.FINER, "Known Exception: "+ex.getMessage());
    			break;
    		}
    	}      
    	return table;
    }
    
    //*************************************************************************************
    //* Get Agent status
    //*************************************************************************************
    public boolean isAgentRunning(String agentName) {
    	this.link("Inventory").click();
        this.cell("Platforms").click();
        this.textbox("SearchPatternField").setValue(agentName.trim());
        this.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);"); //13 - Enter key
        LinkedList<HashMap<String, String>> agents = getRHQgwtTableDetails("listTable", 2, "Resource Type,Name,Ancestry,Description,Type,Version,Availability", "availability_red_16.png=Down,availability_green_16.png=Up");
        if(agents.size() != 1){
        	if(agents.get(0).get("Availability").equalsIgnoreCase("Up")){
        		return true;
        	}
        }else{
        	_logger.log(Level.INFO, "Agent Status: "+agents);
        }        
		return false;    	
    }
	
    //*************************************************************************************
    //* GUI installation - RHQ/JON
    //*************************************************************************************
    public boolean guiInstallationRHQ(String dataBaseType, String databaseDetails, String databasePassword, String databaseMaintanceType, String registeredServerSelection, String serverDetails, String embeddedAgentEnabled){
    	//Check the installation status, return true if already installed
    	if(this.heading1("The Server Is Installed!").exists()){
    		if(this.link("Click here to get started!").exists()){
    			_logger.log(Level.INFO, "Installation process completed already!, Nothing to do!!");
    			return true;
    		}
    	}
    	
    	if(!this.waitForElementExists(this, this.link("Click here to continue the installation"), "Link: Click here to continue the installation", 1000*30)){
    		return false;
    	}
    	this.link("Click here to continue the installation").click();

    	//Select Database Type    	
    	this.select("propForm:databasetype").choose(dataBaseType);

    	//Update text fields on database [DB Connection URL, DB JDBC Driver class, DB XA dataStore Class, DB User Name]
    	updateTextBoxValues(databaseDetails);

    	//Update database password
    	this.password("propForm:databasepassword").setValue(databasePassword);
    	
    	//Test Connection
    	this.submit("Test Connection").click();
    	
    	//validate Database connection works correctly
    	if(!this.waitForElementExists(this, this.image("OK"), "Image: OK, database validatition check", 1000*30)){
    		if(this.waitForElementExists(this, this.image("Error"), "Image: Error, database validatition check", 1000*30)){
    			return false;
    		}
    		return false;
    	}
    	
    	if(databaseMaintanceType != null){
    		if(this.span("A database schema already exists. What do you want to do?").exists()){
    			this.select(1).choose(databaseMaintanceType);
    		}else{
    			_logger.log(Level.WARNING, "There no Database exists! Not able to select '"+databaseMaintanceType+"'. Continuing fresh DB installation...");
    		}
    	}

    	//Registered server Selection
    	if(registeredServerSelection != null){
    		this.select(2).choose(registeredServerSelection);
    	}

    	//Update Server details
    	updateTextBoxValues(serverDetails);

    	//Embedded Agent Enabled
    	if(embeddedAgentEnabled.equalsIgnoreCase("true")){
    		this.radio("true").click();
    	}else{
    		this.radio("false").click();
    	}
    	
    	//Update database password
    	this.password("propForm:databasepassword").setValue(databasePassword);
    	
    	
    	this.submit("Install Server!").click();
    	//Starting up, please wait...

    	if(!this.waitForElementExists(this, this.span("Starting up, please wait..."), "Span: Starting up, please wait...", 1000*30)){
    		return false;
    	}

    	if(!this.waitForElementExists(this, this.link("Done! Click here to get started!"), "Link: Done! Click here to get started!", 1000*60*2)){
    		return false;
    	}

    	this.link("Done! Click here to get started!").click();
    	
    	return true;    	

    }
    
    //*************************************************************************************
    //* Import Resources
    //*************************************************************************************
    public boolean importResources(String resourceName){
    	this.link("Inventory").click();
    	this.cell("Discovery Queue").click();
    	if(this.waitForElementRowExists(this, "No items to show", 1000*5)){
    		_logger.log(Level.WARNING, "No items to import!");
    		return false;
    	}
    	if(resourceName != null){
    		LinkedList<HashMap<String, String>> discoveryQueue = getRHQgwtTableDetails("listTable", 2, "Resource Name, Resource Key, Resource Type, Description, Inventory Status, Discovery Time", null);
        	_logger.log(Level.INFO, "Table Details: Number of Row(s): "+discoveryQueue.size());
        	for(int i=0; i<discoveryQueue.size(); i++){
        		if(resourceName.equalsIgnoreCase(discoveryQueue.get(i).get("Resource Name"))){
        			_logger.log(Level.INFO, "Row: ["+(i+1)+"]: "+discoveryQueue.get(i));
        			this.image("unchecked.png["+i+"]").click();
        		}     		
        	}
    	}else{
    		this.cell("Select All").click();
    	}
    	this.cell("Yes").click();
    	this.cell("Import").click();
    	if(!this.waitForElementExists(this, this.cell("You have successfully imported the selected Resources."), "Cell: You have successfully imported the selected Resources.", 1000*10)){
    		return false;
    	}
		return true;   	
    }

 
    //************************************************************************************************
    // Search Tests
    //***************************************************************************************************
    // TEXT serach for Groups
    public boolean searchComaptibilityGroupWithText(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList){
    	this.link("Inventory").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        this.textbox("SearchPatternField").setValue("compatible");
        
        this.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);"); //13 - Enter key
        if (this.cell("No items to show.").exists()){
        	_logger.log(Level.WARNING, "No  "+ groupPanelName +"  is available" );
        }
        createGroup(groupPanelName, groupName, groupDesc, resourceList);
        this.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);");       
        return this.div("compatibleGroup").exists();
    }

    public boolean searchAllGroupWithText(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList){
    	return searchComaptibilityGroupWithText(groupPanelName, groupName, groupDesc, resourceList);
    }
    public boolean searchMixedGroupWithText(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList){
    	return searchComaptibilityGroupWithText(groupPanelName, groupName, groupDesc, resourceList);
    }


}
