package com.redhat.qe.jon.sahi.tasks;

import com.redhat.qe.auto.sahi.ExtendedSahi;
import com.redhat.qe.auto.testng.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ExecutionException;
import org.testng.annotations.Optional;

public class SahiTasks extends ExtendedSahi {

    private static Logger _logger = Logger.getLogger(ExtendedSahi.class.getName());

    public SahiTasks(String browserPath, String browserName, String browserOpt, String sahiBaseDir, String sahiUserdataDir) {
        super(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
    }

    // ***************************************************************************
    // Login/Logout
    // ***************************************************************************
    public void login(String userName, String password) {
        // sometimes this method is called before JON login screen appears, this snippet will ensure that JON will have enough time to load
        BrowserCondition condition = new BrowserCondition(this) {

            @Override
            public boolean test() throws ExecutionException {
                return browser.textbox("user").exists();
            }
        };
        this.waitFor(condition, 15000);
        this.textbox("user").setValue(userName);
        this.password("password").setValue(password);
        this.cell("Login").click();
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
            this.div(resource).click();

            this.waitFor(5000);
            this.image("right.png").click();
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
        org.testng.Assert.assertTrue(this.cell("You have successfully recalculated this group definition").exists());
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

    public void deleteRole(String roleName) {
        this.link("Administration").click();
        this.cell("Roles").click();
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
        this.cell("Recent Operations").click();
        this.div("Get Plugin Info").click();
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
        this.link("Inventory").click();
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.image("Favorite_24.png").click();

    }

    public void removeFavourite() {
        this.link("Inventory").click();
        this.cell("Servers").click();
        this.link("RHQ Agent").click();
        this.image("Favorite_24_Selected.png").click();

    }

    public boolean checkFavouriteBadgeForAgent() {
        return this.image("Favorite_24_Selected.png").isVisible();
    }

    public boolean checkBadgeAfterRmovingFavourite() {
        return this.image("Favorite_24.png").isVisible();

    }

    //********************************************************
    // Plugin Verification
    //********************************************************
    public void AntBundlePlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("Ant Bundle Plugin");

    }

    public void ApacheHTTPPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("Apache HTTP Server");
    }

    public void AbstractAugeasPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("Abstract Augeas Plugin");
    }

    public void AbstractDatabasePlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("Abstract Database").exists();
    }

    public void IISPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("IIS").exists();
    }

    public void GenericJMXPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("Generic JMX").exists();
    }

    public void OperatingSystemPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("Operating Systems Platform").exists();
    }

    public void PostGreSQLPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("PostgreSQL Database");
    }

    public void RHQAgentPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("RHQ Agent");
    }

    public void ScriptPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.link("Script");
    }

    public void AntBundleProcessorPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Ant Bundle Processor");
    }

    public void PerspectiveCorePlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Perspective Core");
    }

    public void DiskContentPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Disk Content");
    }

    public void FileTemplateBundleProcessorPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("File Template Bundle Processor");
    }

    public void JBossCSPContentPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("JBoss CSP Content");
    }

    public void URLContentPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("URL Content");
    }

    public void YumContentPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Yum Content");
    }

    public void AlertCLIPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:CLI");
    }

    public void AlertEmailPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:Email");
    }

    public void AlertIRCPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:IRC");
    }

    public void AlertMicroBlogPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:Microblog");
    }

    public void AlertMobicentsPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:Mobicents");
    }

    public void AlertOperationsPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:Operations");
    }

    public void AlertRolesPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:Roles");
    }

    public void AlertSNMPPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:SNMP");
    }

    public void AlertSubjectPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Alert:Subject");
    }

    public void DriftJPAPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("Drift:JPA (RHQ Default)");
    }

    public void PackageTypeCLIPlugin() {

        this.link("Administration").click();
        this.row("Plugins").click();
        this.row("Server Plugins");
        this.link("PackageType:CLI");
    }

    //*********************************************************************************
    //* Alert Definition Creation
    //*********************************************************************************
    public void gotoAlertDefinationPage(String resourceName, boolean definitionsPage) {
        this.link("Inventory").click();
        String[] resourceType = resourceName.split("=");
        if (resourceType.length > 1) {
            this.cell(resourceType[0].trim()).click();
            this.textbox("SearchPatternField").setValue(resourceType[1].trim());
            this.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);"); //13 - Enter key
        } else {
            this.cell("Servers").click();
        }
        this.link(resourceType[1].trim()).click();
        this.cell("Alerts").click();
        if (definitionsPage) {
            this.xy(this.cell("Definitions"), 3, 3).click();
        } else {
            this.xy(this.cell("History"), 3, 3).click();
        }
    }

    public void selectConditionComboBoxes(String options) {
        /*	String comboBoxIdentifier = "selectItemText";
        int indexStartFrom = 3;
        String[] optionArray = Common.getCommaToArray(options);
        int totalComboBox = this.div(comboBoxIdentifier).countSimilar();
        _logger.finer("ComboBoxIdentifier Count: "+totalComboBox);
        for(int i=0; i<totalComboBox; i++){
        _logger.finer("ComboBoxIdentifier Name: "+comboBoxIdentifier+"["+i+"] --> "+sahiTasks.div(comboBoxIdentifier+"["+i+"]").getText());
        }
        if(optionArray.length > 2){
        Wait.waitForElementDivExists(this,  comboBoxIdentifier+"["+(optionArray.length+indexStartFrom)+"]", 1000*10);
        }
        for(int i=0;i<optionArray.length;i++){
        ComboBox.selectComboBoxDivRow(this,  comboBoxIdentifier+"["+(i+indexStartFrom)+"]", optionArray[i].trim());
        }*/
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
                    this.textbox(key).setValue(keyValueMap.get(key));
                }
            }
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

        selectConditionComboBoxes(conditionsDropDown);
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
        selectConditionComboBoxes(recoveryAlertDropDown);
        selectYesNoradioButtons(disableWhenFired);


        //Dampening
        this.cell("Dampening").click();
        selectConditionComboBoxes(dampeningDropDown);
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
}
