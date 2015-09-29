package com.redhat.qe.jon.sahi.tasks;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.ExtendedSahi;
import com.redhat.qe.jon.sahi.base.SahiSettings;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

import net.sf.sahi.client.ElementStub;

import org.testng.annotations.Optional;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SahiTasks extends ExtendedSahi {

	public static final String ADMIN_USER		= "rhqadmin";
	public static final String ADMIN_PASSWORD	= "rhqadmin";
    private static Logger _logger = Logger.getLogger(SahiTasks.class.getName());
    private Navigator navigator;
    public SahiTasks(String browserPath, String browserName, String browserOpt, String sahiBaseDir, String sahiUserdataDir) {
        super(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
    }
    public Navigator getNavigator() {
		return navigator;
	}
    @Override
    public void open() {
    	// initialize navigator in 
    	this.navigator = new Navigator(this);
    	super.open();
    }

    // ***************************************************************************
    // Login/Logout
    // ***************************************************************************
    public boolean login(String userName, String password) {
        if(this.waitForElementExists(this, this.link("Logout"), "Link: Logout", 1000*3)){
            this.link("Logout").click();
        }

        int timeout = 240*Timing.TIME_1S;

        while (!this.textbox("inputUsername").exists() && !this.textbox("user").exists() && timeout > 0) {
            timeout -= Timing.TIME_1S;
            _logger.fine("Waiting another second for login screen to appear, remaining " + Timing.toString(timeout));
        }

        // check if a new login page is visible
        if (this.textbox("inputUsername").exists()) {
            // new login page
            this.textbox("inputUsername").setValue(userName);
            this.password("inputPassword").setValue(password);
            this.submit("Log In").click();
        } else if (this.textbox("user").exists()) {
            // old login page
            this.textbox("user").setValue(userName);
            this.password("password").setValue(password);
            this.cell("Login").click();
        } else {
            return false;
        }
        return true;
    }

    public void logout() {
        this.link("Logout").click();
        this.waitFor(5*Timing.TIME_1S);
    }

    public String getCurrentLogin(){
    	// we need to differentiate between old and new look&feel
        if(this.link("Logout").isVisible()){
    		return this.cell(1).near(this.cell("|").in(this.div("toolStrip"))).getText();
    	}else if(this.span("pficon pficon-user").isVisible()){
    	    return this.link(0).near(this.span("pficon pficon-user")).getText();
    	}else{
    		return null;
    	}		
	}
	
    public void relogin(String userName, String password) {
    	String currentLogin = this.getCurrentLogin();
		if(currentLogin == null){
			_logger.log(Level.FINE, "User not logged in, Checking login box...");
			if(this.textbox("inputUsername").exists() || this.textbox("user").exists()){
				login(userName, password);
			}
		}else if(userName.equals(currentLogin)){
			_logger.log(Level.FINE, "User["+userName+"] already logged in");
		}else{
			_logger.log(Level.FINE, "Currently logged in as ["+currentLogin+"], Changing login to ["+userName+"]");
			logout();
			login(userName, password);
		}
    }
    
    //LDAP login check with first time login
    public boolean ldapLogin(String userName, String password, String firstName, String lastName, String email, String phoneNumber, String department) {
    	if(this.login(userName, password)){
    		//check:: Is LDAP user logged in first time?
    		if(this.cell("Register User").exists()){
    			_logger.log(Level.INFO, "user["+userName+"] logged in first time, registeration required...");    			
    			//Set First Name
    			this.textbox("first").setValue(firstName);
    			//set Last Name
    			this.textbox("last").setValue(lastName);
    			//set email
    			this.textbox("email").setValue(email);
    			//set phone number
    			this.textbox("phone").setValue(phoneNumber);
    			//set Department
    			this.textbox("department").setValue(department);
    			
    			//Click OK
    			this.cell("OK").click();
    			return true;
    		}
    		return true;
    	}
    	return false;
    }
    
    //-----------------------------------------------------------------------------------------------------------
    // Register LDAP
    //-----------------------------------------------------------------------------------------------------------
    public boolean registerLdapServer(String ldapUrl, String ldapSearchBase, String ldapLoginProperty, boolean enableSSL, boolean enableLdap){
    	if(this.selectPage("Administration-->System Settings", this.cell("Server Details"), 1000*5, 3)){
    		this.selectComboBoxes("Jump to Section-->LDAP Configuration Properties");
    		
    		// Enable/Disable LDAP
    		if(enableLdap){
    			this.radio("CAM_JAAS_PROVIDER[0]").click();
    		}else{
    			this.radio("CAM_JAAS_PROVIDER[1]").click();
    		}

    		//Set Search base
    		this.textbox("CAM_LDAP_BASE_DN").setValue(ldapSearchBase);

    		//Group Search Filter
    		this.textbox("CAM_LDAP_GROUP_FILTER").setValue("");

    		//Group Member filter
    		this.textbox("CAM_LDAP_GROUP_MEMBER").setValue("");

    		//Is PosixGroup enabled/disabled
    		//Disabled
    			this.radio("CAM_LDAP_GROUP_USE_POSIX[1]").click();
    		
    		// Login Property 
    		this.textbox("CAM_LDAP_LOGIN_PROPERTY").setValue(ldapLoginProperty);
    		
    		//LDAP URL
    		this.textbox("CAM_LDAP_NAMING_PROVIDER_URL").setValue(ldapUrl);
    		
    		//Enable/Disable SSL
    		if(enableSSL){
    			this.radio("CAM_LDAP_PROTOCOL[0]").click();
    		}else{
    			this.radio("CAM_LDAP_PROTOCOL[1]").click();
    		}
    		
    		this.cell("Save").near(this.cell("Dump System Info")).click();
    		return true;
    	}else{
    		return false;
    	}
    }

    /**
     * Reloads whole browser window
     */
    public void reloadPage() {
        _logger.fine("Reloading whole page");
        this.execute("_sahi._call(window.location.reload());");
        this.waitFor(Timing.WAIT_TIME);
        if (this.image("loadingSmall.gif").isVisible()) {
            this.waitFor(Timing.WAIT_TIME);
        }
    }

    /**
     * Method which add column Last Modified Time and sort descending  the table by this column
     *
     */
    public void sortChildResources() {
        if (!this.cell("Last Modified Time").isVisible()) {
            ElementStub nameColumnHeaderElement = this.cell("Name").near(this.cell("Ancestry"));
            this.waitForElementExists(this, nameColumnHeaderElement, nameColumnHeaderElement.toString(), Timing.WAIT_TIME);
            // 1. Add column Last Modified Time
            this.xy(nameColumnHeaderElement, 3, 3).rightClick();
            this.xy(this.cell("Columns"), 3, 3).mouseOver();
            this.xy(this.cell("Last Modified Time"), 3, 3).click();
            // 2. Set Auto Fit All Columns
            this.xy(this.cell("Auto Fit All Columns"), 3, 3).click();
            // 3. Sort the table by Last Modified Time descending
            // sort by Last Modified Time
            this.xy(this.cell("Last Modified Time"), 3, 3).click();
            this.waitFor(Timing.WAIT_TIME);
            this.xy(this.cell("Last Modified Time"), 3, 3).click();
            this.waitFor(Timing.WAIT_TIME);
        }
    }

    // ***************************************************************************
    // Inventory
    // ***************************************************************************
    public void createGroup(String groupPanelName, String groupName, String groupDesc) {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        if(!this.div(groupName).in(this.div("gridBody")).exists()){
        	this.cell("New").click();
            this.textbox("name").setValue(groupName);
            this.textarea("description").setValue(groupDesc);
            this.cell("Next").click();
            this.cell("Finish").click();
        }else{
        	_logger.log(Level.WARNING, "Group["+groupName+"] already available!!");
        }
        checkInfo();
    }
    
    private boolean checkInfo(){
    	 if(this.cell("OK").under(this.cell("An empty group is always considered as mixed.")).exists()){
         	this.cell("OK").under(this.cell("An empty group is always considered as mixed.")).click();
         	return true;
         }
    	 _logger.log(Level.FINE, "Info Message is not available to click!");
    	 return false;
    }

    private void selectResourceOnGroup(String resourceName, int maxIndex){
    	for(int i=maxIndex; i>=0; i--){
    		if(this.textbox("search["+i+"]").exists()){
    			this.textbox("search["+i+"]").setValue(resourceName);
    			_logger.log(Level.INFO, "Value set on:  search["+i+"]: "+resourceName);
    			break;
    		}   			
    	}  
    	this.waitFor(5*1000);
    	if(!this.byText(resourceName, "nobr").exists()){
    		_logger.log(Level.WARNING, "Resource["+resourceName+"] not available to select..");
    	}
        this.xy(this.byText(resourceName, "nobr"), 3,3).doubleClick();
        this.waitFor(2*1000);
    }
    
    public void createGroup(String groupPanelName, String groupName, String groupDesc, String resourceName) {
    	ArrayList<String> resourceList = new ArrayList<String>();
    	resourceList.add(resourceName);
    	createGroup(groupPanelName, groupName, groupDesc, resourceList);
    }
    
    public void createGroup(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList) {
    	this.selectPage("Inventory-->Compatible Groups", this.textbox("search"), 1000*5, 3);
    	this.cell("New").click();
    	this.textbox("name").setValue(groupName);
        this.textarea("description").setValue(groupDesc);
        this.cell("Next").click();
        for (String resource : resourceList) {
        	selectResourceOnGroup(resource.trim(), 2);
        }
        this.cell("Finish").click();
        checkInfo();
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
        this.div("/Back to List/").click(); //PatternFly Change: 15-Jul-2014
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
        selectDivElement(groupName);
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
    
	public void scondLevelMandatoryMenuLinksExist() {

		Assert.assertTrue(this.cell("Summary").exists());
		Assert.assertTrue(this.cell("Inventory").exists());
		Assert.assertTrue(this.cell("Alerts").exists());
		Assert.assertTrue(this.cell("Monitoring").exists());
	}

    // ***************************************************************************
    // Users and Groups
    // ***************************************************************************
    public void createDeleteUser() {
    	selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
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
        this.image("/opener_closed.*/").click();
        this.image("/opener_opened.*/[1]").click();
        this.image("/opener_closed.*/").click();
        this.image("/opener_opened.*/").click();
    }

    //******************************************************************
    //Resource 
    //******************************************************************
    
    
    public void checkResourceBrowserAvailabilityColumnsInGroupDef() {
    	org.testng.Assert.assertTrue(selectPage("Inventory-->Dynagroup Definitions", this.cell("Expression Set"), 1000*5, 3));
        org.testng.Assert.assertTrue(this.cell("Name").exists());
        org.testng.Assert.assertTrue(this.cell("Description").exists());
        org.testng.Assert.assertTrue(this.cell("Expression Set").exists());
        org.testng.Assert.assertTrue(this.cell("Last Calculation Time").exists());
        org.testng.Assert.assertTrue(this.cell("Next Calculation Time").exists());
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
    
    private void checkSearchBox(){
    	if(this.textbox("SearchPatternField").exists()){
    		_logger.log(Level.FINE, "'SearchPatternField' is avaialble");
    	}else{
    		org.testng.Assert.assertTrue(this.textbox("search").exists());
    	}
    }

    public void checkSearchTextBoxInEachResourceBrowserGroup() {
        this.link("Inventory").click();
        this.cell("All Groups").click();
        checkSearchBox();
        this.cell("Compatible Groups").click();
        checkSearchBox();
        this.cell("Mixed Groups").click();
        checkSearchBox();
        this.cell("Problem Groups").click();
        checkSearchBox();
        this.link("Inventory").click();
    }

    public void createDyanGroup(String groupName, String groupDesc) {
        this.link("Inventory").click();
        this.cell("Dynagroup Definitions").click();
        this.cell("New").click();
        this.textbox("name").setValue(groupName);
        this.textarea("description").setValue(groupDesc);
        this.textarea("expression").setValue("" +
        		"groupby resource.trait[jboss.system:type=Server:VersionName] \n\r" +
        		"resource.type.plugin = JBossAS \n\r" +
        		"resource.type.name = JBossAS Server");
        this.cell("Save & Recalculate").click();
        String msg = "You have successfully recalculated this group definition";
        org.testng.Assert.assertTrue(this.waitForAnyElementsToBecomeVisible(this,
                new ElementStub[]{this.cell(msg),this.div(msg)},
                msg, 1000*20),"Successful message check"); //Wait 20 seconds
        this.div("/Back to List/").click();//PatternFly Change: 15-Jul-2014
        this.link(groupName).click();
        this.div("/Back to List/").click();//PatternFly Change: 15-Jul-2014
    }

    public void deleteDynaGroup(String groupDesc) {
    	selectPage("Inventory-->Dynagroup Definitions", this.cell("Name"), 1000*5, 2);
    	this.div(groupDesc).click();
        this.cell("Delete").click();
        this.cell("Yes").click();
    }

    public void resourceSearch(String searchTestuser, String password, String firstName, String secondName, String emailId, String searchRoleName, String desc, String compGroupName, String searchQueryName) throws SahiTasksException {
    	createGroup("Compatible Groups", compGroupName, "Created by Automation", "RHQ Agent");
        createUser(searchTestuser, password, firstName, secondName, emailId);
        createRoleWithoutMangeInvetntory(searchRoleName, desc, compGroupName, searchTestuser);
        //Login with new user
        relogin(searchTestuser, password);
        navigateToAllGroups();
        setSearchBox(searchQueryName+"="+compGroupName);
        Assert.assertTrue(this.div(compGroupName).exists(), "Group["+compGroupName+"] availability status...");
        // login with rhqadmin user
     	relogin(ADMIN_USER, ADMIN_PASSWORD);
     	
     	//Delete Group, Role and User
     	deleteUser(searchTestuser);
     	deleteRole(searchRoleName);
     	deleteGroup("Compatible Groups", compGroupName);
    }

    public void createRoleWithoutMangeInvetntory(String roleName, String desc, String compGroupName, String searchTestuser) {
        this.link("Administration").click();
        this.cell("Roles").click();
        this.cell("New").click();
        this.textbox("name").setValue(roleName);
        this.textbox("description").setValue(desc);
        this.cell("Resource Groups").click();
        this.div(compGroupName).click();
        this.image("/right.*/").click();
        this.cell("Users").click();
        this.div(searchTestuser).click();
        this.image("/right.*/").click();
        this.cell("Save").near(this.cell("Reset")).click(); 
    }

    public void loginNewUser(String newUser, String password) {
        if(!this.waitForElementExists(this, this.textbox("inputUsername"), "inputUsername", 1000*240)){
            this.waitForElementVisible(this, this.textbox("user"), "Login field", Timing.WAIT_TIME);
            this.textbox("user").setValue(newUser);
            this.password("password").setValue(password);
            this.cell("Login").click();
        }else{
            // new login page
            this.textbox("inputUsername").setValue(newUser);
            this.password("inputPassword").setValue(password);
            this.submit("Log In").click();
        }
    }

    public void navigateToAllGroups() {
        this.link("Inventory").click();
        this.waitFor(5000);
        this.cell("All Groups").click();
    }
    
    public void checkPlatform(){
    	this.link("Inventory").click();
    	Assert.assertTrue(this.cell("Platforms").exists());
    	int count = this.cell("Platforms").countSimilar();
    	this.cell("Platforms").collectSimilar().get(count-1).click();
    	Assert.assertTrue(this.div("Linux").exists());
    	this.div("Linux").doubleClick();
    }
    
    
    public void checkAutoGroupResourceMenues(){
    	this.link("Inventory").click();
    	Assert.assertTrue(this.cell("Platforms").exists());
    	this.cell("Platforms").click();
    	Assert.assertTrue(this.div("Linux").exists());
    	this.div("Linux").doubleClick();
    	
    	checkAutoGroupMenus("RHQ Storage Nodes");
    	checkAutoGroupMenus("JBossAS7 Standalone Servers");
    	checkAutoGroupMenus("Network Adapters");
    	checkAutoGroupMenus("File Systems");
    	checkAutoGroupMenus("CPUs");  	
    }
    
    public void checkAutoGroupMenus(String resource){
    	ElementStub resourceEl = this.image("folder_autogroup_closed.png").near(this.cell(resource));
    	Assert.assertTrue(resourceEl.exists(), "Resource["+resource+"] status..");
    	resourceEl.click();
    	Assert.assertTrue(this.row("Summary").near(this.cell("tabButtonTopSelected")).exists(), "Sub Menu["+resource+"->Summary] Status...");
    	Assert.assertTrue(this.row("Inventory").near(this.cell("tabButtonTop")).exists(), "Sub Menu["+resource+"->Inventory] Status...");
    	Assert.assertTrue(this.row("Alerts").near(this.cell("tabButtonTop")).exists(), "Sub Menu["+resource+"->Alerts] Status...");
    	Assert.assertTrue(this.row("Monitoring").near(this.cell("tabButtonTop")).exists(), "Sub Menu["+resource+"->Monitoring] Status...");
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
    	selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
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
    	selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
    	this.div(userName).click();
        this.cell("Delete").click();
        this.cell("Yes").click();
    }

    public void createRole(String roleName, String roleDesc) {
    	selectPage("Administration-->Roles", this.cell("Name"), 1000*5, 2);
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
    	selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
    	this.link(userName).click();
        for (String role : roleNames) {
            this.div(role).click();
            this.image("/right/").click(); //PatternFly Change: 15-Jul-2014
        }
        this.cell("Save").click();
    }

    public boolean verifyUserRole(String userName, String password, ArrayList<String> roleNames) {
       
    	relogin(userName, password);
        // TODO: Verification of role still needs to be done, however this 
        // hinges on whether we can create a role w/ specific permission 
        // by specifying only the permission name.
        relogin(ADMIN_USER, ADMIN_PASSWORD); // reset
        return true;
    }

    //******************************************************************
    //Help
    //******************************************************************
    public void helpAbout() {
        //Old - About dialog(if loop): Should be removed after sometime. Modified on: 18-Oct-2013
    	//RHQ Build: 4.10.0-SNAPSHOT
    	ElementStub closeIcon = this.image("close.png").near(this.image("/maximize/"));  //PatternFly Change: 15-Jul-2014
    	if(selectPage("Help-->About", this.span("DisplayLabel[0]"), 1000*5, 3)){
        	this.row("Close").click();
    	}else if(selectPage("Help-->About", closeIcon, 1000*5, 3)){ // New dialog approach: Date: 18-Oct-2013
    		closeIcon.click();
    	}    
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
    //************************************************************
    public void gotoReportRecentOperationsPage() {
    	this.selectPage("Reports-->Recent Operations", this.cell("Recent Operations"), 1000*5, 3);
    }
    
    public void gotoOperationsSchedulesPage(String resourceName, boolean selectSchedules) {
    	selectResource(resourceName);
        this.cell("Operations").click();
        if (selectSchedules) {
            this.xy(this.cell("Schedules"), 3, 3).click();
        } else {
            this.xy(this.cell("History"), 3, 3).click();
        }
    }
    
    public boolean createRecentOperationsSchedule() {
    	this.gotoOperationsSchedulesPage("Servers=RHQ Agent", true);
        this.cell("New").click();
        this.div("selectItemText").click();
        this.div("Get Info On All Plugins").click();
    	this.radio("now");
        this.cell("Schedule").click();
        this.gotoOperationsSchedulesPage("Servers=RHQ Agent", true);
        if(!this.div("Get Info On All Plugins").exists()){
        	_logger.log(Level.WARNING, "[Get Info On All Plugins] is not available!");
        	return false;
        }
        return true;
    }

    public boolean deleteRecentOperationsSchedule() {
    	this.gotoReportRecentOperationsPage();
        this.div("Get Info On All Plugins").click();
        this.cell("Delete").click();
        this.cell("Yes").click();
        if(this.div("Get Info On All Plugins").exists()){
        	_logger.log(Level.WARNING, "[Get Info On All Plugins] is available!");
        	return false;
        }
        return true;
    }

    public boolean recentOperationsForceDelete() {
        if(createRecentOperationsSchedule()){
        	this.gotoReportRecentOperationsPage();
            this.div("Get Info On All Plugins").click();
            this.cell("Force Delete").click();
            this.cell("Yes").click();
            if(this.div("Get Info On All Plugins").exists()){
            	_logger.log(Level.WARNING, "[Get Info On All Plugins] is available! Deletion failed...");
            	return false;
            }
            return true;
        }
        return false;
    }

    public boolean recentOperationsQuickLinks() {
    	this.gotoReportRecentOperationsPage();
        this.link("RHQ Agent").click();
     
        /* not sure what this should do but it's failing
        int formTitleCoint = this.cell("formTitle").countSimilar();
        HashMap<String, String> formDetail = new HashMap<String, String>();
        for(int i=0; i<formTitleCoint; i++){
        	formDetail.put(this.cell("formTitle["+i+"]").getText(), this.cell("formCell["+i+"]").getText());
        }
        _logger.log(Level.INFO, "Form Data: "+formDetail);
       */
        // make sure that we are on RHQ Agent resource page
        if (this.cell("Summary").isVisible() && this.span("RHQ Agent").isVisible())
        {
            return true;
        }else
        {
            return false;
        }
    }

    public boolean opreationsWithRefreshButtonFunctionality() {
    	this.gotoReportRecentOperationsPage();
        this.cell("Refresh").click();
        this.div("Get Info On All Plugins").click();
        if(!this.div("Get Info On All Plugins").exists()){
        	_logger.log(Level.WARNING, "[Get Info On All Plugins] is available!");
        	return false;
        }
        return true;
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
        this.image("/refresh/").click();//PatternFly Change: 15-Jul-2014
    }

    public void messagePortletMinimizeMaximize() {
        this.image("/minimize.*/").click();
        this.image("/restore.*/").click();

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
    	if(selectAgentPluginPage){
    		selectPage("Administration-->Agent Plugins", this.cell("Name"), 1000*5, 2);
    	}else{
    		selectPage("Administration-->Server Plugins", this.cell("Name"), 1000*5, 2);
    	}
    }
    
    public boolean getAgentServerPluginsStaus(String pluginsName, boolean redirectPage, boolean isAgentPlugins){
    	if(redirectPage){
    		locatePluginPage(isAgentPlugins);
    	}    	
        return this.link(pluginsName).exists();
    }
    
    //*********************************************************************************
    //* Redirect pages, main menu display page should be on span
    //*********************************************************************************   
    public boolean selectPage(String pageLocation, ElementStub elementReference, int waitTime, int retryCount){
    	String[] pageLocations = pageLocation.split("-->");    
      	for(int i=1;i<=retryCount;i++){
    		this.link(pageLocations[0].trim()).click();
        	this.waitForElementExists(this, this.span(pageLocations[0].trim()), "SPAN: "+pageLocations[0].trim(), waitTime);
        	this.cell(pageLocations[1].trim()).click();
        	if(!this.waitForElementVisible(this, elementReference, "Element: "+elementReference.toString(), waitTime)){
        		_logger.log(Level.INFO, "Filed to load : "+pageLocation+", Retry Count: ["+i+" of "+retryCount+"]");        		
        	}else{
        		_logger.log(Level.FINE, "Loaded Successfully: "+pageLocation);
        		return true;
        	}
    	}
		return false;    	
    }
    public boolean selectDivElement(String elementName){
    	//Set max div count on dynamic
    	int divMaxIndex = this.div(elementName).countSimilar();
    	for(int i=divMaxIndex; i>=0; i--){
    		if(this.div(elementName+"["+i+"]").exists()){
    			this.div(elementName+"["+i+"]").click();
    			_logger.log(Level.INFO, "Clciked on the element:  "+elementName+"["+i+"]");
    			return true;
    		}
    	}  
    	_logger.log(Level.WARNING, "There is no div element["+elementName+"] found!, Input MaxIndex:"+divMaxIndex);
		return false;    	
    }
    
    //*********************************************************************************
    //* About and build versions
    //*********************************************************************************
    public HashMap<String, String> getBuildVersion(){
		HashMap<String, String> version = new HashMap<String, String>();
    	//Old - About dialog(if loop): Should be removed after sometime. Modified on: 18-Oct-2013
    	//RHQ Build: 4.10.0-SNAPSHOT
		ElementStub closeIcon = this.image("/close.*/").near(this.image("/maximize.*/"));  //PatternFly Change: 15-Jul-2014
		
    	if(selectPage("Help-->About", this.span("DisplayLabel[0]"), 1000*5, 3)){
        	version.put("version", this.span("DisplayLabel[0]").getText());
        	version.put("build.number", this.span("DisplayLabel[1]").getText());
        	version.put("gwt.version", this.span("DisplayLabel[2]").getText());
        	version.put("smart.gwt.version", this.span("DisplayLabel[3]").getText());
        	this.row("Close").click();
    	}else if(selectPage("Help-->About", closeIcon, 1000*5, 3)){ // New dialog approach: Date: 18-Oct-2013
    		version.put("application.name", this.cell("formTitle[0]").getText()+this.div("staticTextItem[1]").getText());
    		version.put("version", this.cell("formTitle[1]").getText()+this.div("staticTextItem[2]").getText());
        	version.put("build.number", this.cell("formTitle[2]").getText()+this.div("staticTextItem[3]").getText());
        	version.put("gwt.version", this.cell("formTitle[3]").getText()+this.div("staticTextItem[4]").getText());
        	version.put("smart.gwt.version", this.cell("formTitle[4]").getText()+this.div("staticTextItem[5]").getText());
        	closeIcon.click();
    	}
    	_logger.log(Level.INFO, "Version Information: "+version);    	
    	return version;
    }
    //*********************************************************************************
    //* Alert Definition Creation
    //*********************************************************************************
    public void selectResource(String resourceName){
        String[] resourceType = resourceName.split("=");
        if (resourceType.length > 1) {
        	selectPage("Inventory-->"+resourceType[0], this.textbox("search"), 1000*5, 3);
        	setSearchBox(resourceType[1].trim());                        
        } else {
            _logger.log(Level.WARNING, "Invalid parameter passed --> "+resourceName);
            return;
        }
        this.link(resourceType[1].trim()).click();
    }
    public void gotoAlertDefinationPage(String resourceName, boolean definitionsPage) {
    	//selectResource(resourceName);
    	//Above line is not selecting specific resource if we have more than one agent imported, adding the following lines,
    	Resource agent = new Resource(this,System.getProperty("jon.agent.name"),"RHQ Agent");
	    agent.navigate();    	
        int count = this.cell("Alerts").countSimilar();
        this.cell("Alerts").collectSimilar().get(count - 1).click();
        // trying again with different locator
        this.cell("Alerts").click();
        if (definitionsPage) {
            this.xy(this.cell("Definitions"), 3, 3).click();
        } else {
            this.xy(this.cell("History"), 3, 3).click();
        }
    }

    /**
     * 
     * @param fileInputIdent indentify file input field (name,id)
     * @param path to file to be uploaded (can be either resource file or a regular file - resource file has higher priority)
     */
    public void setFileToUpload(String fileInputIdent, String path) {
    	URL resource = SahiTasks.class.getResource(path);    	
    	String fullPath = null;
    	if (resource==null) {
    	    File resFile = new File(path);	
    	    if (resFile.exists() && resFile.canRead()) {
    	    	    fullPath = resFile.getAbsolutePath();
    	    }
    	    else {
    		throw new RuntimeException("Unable to find resource/file ["+path+"] on either classpath or filesystem");
    	    }
    	}
    	else {
    	    fullPath = resource.getPath();
    	}
    	this.file(fileInputIdent).setFile(fullPath);
    	this.execute("_sahi._call(_sahi._file(\""+fileInputIdent+"\").type = \"text\");");
		this.textbox(fileInputIdent).setValue(fullPath);
    }
    
    public void selectComboBoxes(String options, String nearElement, String optionElementType){
        if (options != null) {
            if (options.trim().length() > 0) {
                String[] optionArray = this.getCommaToArray(options);
                for (String option : optionArray) {
                    String[] optionTmp = option.split("-->");
                    if(optionTmp[0].contains("|")){
                    	String[] nearDrop = optionTmp[0].split("\\|");
                    	nearElement = nearDrop[0];
                    	optionTmp[0] = nearDrop[1];
                    }
                    if(nearElement == null){
                    	 if (this.div(optionTmp[0].trim() + "[1]").exists()) {
                             _logger.info("\"" + optionTmp[0].trim() + "[1]\" is available to select");
                             optionTmp[0] = optionTmp[0].trim()+"[1]";
                             //this.selectComboBoxDivRow(this, optionTmp[0].trim() + "[1]", optionTmp[1].trim());
                         }
                    }
                    
                    //int maxCount = 1;
                   
                    if(optionElementType == null){
                    	optionElementType = "row";
                    }
                    optionElementType = optionElementType.toLowerCase();
                    if(optionElementType.equals("row")){
                    	//maxCount = this.row(optionTmp[1].trim()).countSimilar();
                    	//optionTmp[1] = optionTmp[1].trim()+"["+(maxCount-1)+"]";
                    	if(nearElement != null){
                    		this.selectComboBoxByNearCellOptionByRow(this, optionTmp[0].trim(), nearElement, optionTmp[1].trim());
                    	}else{
                    		this.selectComboBoxDivRow(this, optionTmp[0].trim(), optionTmp[1].trim());
                    	}                    	
                    }else if(optionElementType.equals("div")){
                    	//maxCount = this.div(optionTmp[1].trim()).countSimilar();
                    	//optionTmp[1] = optionTmp[1].trim()+"["+(maxCount-1)+"]";
                    	if(nearElement != null){
                    		this.selectComboBoxByNearCellOptionByDiv(this, optionTmp[0].trim(), nearElement, optionTmp[1].trim());
                    	}else{
                    		this.selectComboBoxDivDiv(this, optionTmp[0].trim(), optionTmp[1].trim());
                    	}     
                    }
                }
            }
        }
    }
    
    public void selectComboBoxes(String options) {
    	selectComboBoxes(options, null, null);
    }

    public void selectComboBoxes(String options, String nearElement) {
    	selectComboBoxes(options, null, nearElement);
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
        _logger.log(Level.INFO, "Alert Name Text Box Status: "+this.textbox("/textItem/").near(this.row("Name :")).exists());
        this.textbox("/textItem/").near(this.row("Name :")).click();
        this.textbox("/textItem/").near(this.row("Name :")).setValue(alertName);
        if (alertDescription != null) {
            this.textarea("textItem").near(this.row("Description :")).setValue(alertDescription);
        }

        //Add conditions
        this.cell("Conditions").click();
        int count = this.cell("Add").countSimilar();
        this.cell("Add").collectSimilar().get(count-1).click();

        //selectComboBoxes(conditionsDropDown); //Disabled, not stable with this
        //selectComboBoxes(conditionsDropDown, "row");
        
        //Adding Conditions
        String[] conditionsDropDowns = this.getCommaToArray(conditionsDropDown);
        for (String option : conditionsDropDowns) {
            String[] optionTmp = option.split("-->");
            ElementStub dropDown = this.div("selectItemText").near(this.cell(optionTmp[0]));
            ElementStub dropDownValue = this.div(optionTmp[1]).under(this.div("selectItemText").near(this.cell(optionTmp[0])));
            _logger.info("DropDown: "+dropDown+", Is Available? "+dropDown.exists());
            dropDown.click(); //Clicking Drop Down Box
            _logger.info("DropDown Value: "+dropDownValue+", Is Available? "+dropDownValue.exists());
            dropDownValue.click(); //Selecting value for the above drop down box
        }
        
        
        updateTextBoxValues(conditionTextBox);


        //Modified OK Button access, since JON 3.2 Alpha 53 release.
        this.cell("OK").in(this.div("OKCancel")).click();

        //Add notifications
        this.cell("Notifications").click();
        count = this.cell("Add").countSimilar();
        this.cell("Add").collectSimilar().get(count-1).click();
        //Select Notification type
        selectComboBoxes(notificationType, "row");
        if (notificationType.contains("System Users")) {
        	updateSystemUserNotification(notificationData);
        } else {
            _logger.log(Level.WARNING, "Undefined notification type: " + notificationType);
        }
        this.cell("OK").click();
        this.waitFor(1000);

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
        this.div("/Back to List/").click(); //PatternFly Change: 15-Jul-2014

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
        this.cell("Delete").near(this.cell("Disable")).click();
        //this.cell("Delete[4]").click();
        
        this.cell("Yes").near(this.cell("No")).click();
        //this.cell("Yes").click();        
        // it seems that sometimes it takes a while for definition to disappear
        this.waitFor(1000);
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
        this.cell("Delete").near(this.cell("Acknowledge").near(this.cell("Delete All"))).click();
        //this.cell("Delete[3]").click();
        
        this.cell("Yes").near(this.cell("No")).click();
        //this.cell("Yes").click();
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
    public LinkedList<HashMap<String, String>> getRHQgwtTableFullDetails(String tableName, int tableCountOffset, String columnsCSV, String replacementKeyValue) {
    	return getRHQgwtTableDetails(tableName, tableCountOffset, columnsCSV, replacementKeyValue, false, 0, false, null);    	
    }
    public LinkedList<HashMap<String, String>> getRHQgwtTableConditionalDetails(String tableName, int tableCountOffset, String columnsCSV, String replacementKeyValue, String condition) {
    	return getRHQgwtTableDetails(tableName, tableCountOffset, columnsCSV, replacementKeyValue, false, 0, true, condition);    	
    }
    public HashMap<String, String> getRHQgwtTableRowDetails(String tableName, int tableCountOffset, String columnsCSV, String replacementKeyValue, int rowNo) {
    	LinkedList<HashMap<String, String>> rowDetails = getRHQgwtTableDetails(tableName, tableCountOffset, columnsCSV, replacementKeyValue, true, rowNo, false, null);
    	if(rowDetails.size() == 1){
    		return rowDetails.get(0);  
    	}else{
    		return new HashMap<String, String>();
    	}  	
    }
    @SuppressWarnings("unchecked")
	public LinkedList<HashMap<String, String>> getRHQgwtTableDetails(String tableName, int tableCountOffset, String columnsCSV, String replacementKeyValue, boolean singleRow, int rowNo, boolean conditional, String condition) {
    	int noListTables = this.table(tableName).countSimilar()-tableCountOffset;
        _logger.finer("Number of list tables - offset ("+tableCountOffset+") = " + noListTables);
    	LinkedList<HashMap<String, String>> rows = new LinkedList<HashMap<String,String>>();
    	HashMap<String, String> row = new HashMap<String, String>();
    	String[] columns = getCommaToArray(columnsCSV);
    	HashMap<String, String> replacement = getKeyValueMap(replacementKeyValue);
    	String innerHTMLstring;
    	String textString;
    	String columnName = null;
    	String columnValue = null;
    	if(conditional){
    		String[] columnValueTmp = condition.split("=");
    		columnName = columnValueTmp[0].trim();
    		columnValue = columnValueTmp[1].trim();
    	}
    	for(int i=0; ;i++){
    		if(singleRow){
        		i=rowNo;
        	}
            try{
    			for(int c=0; c<columns.length; c++){
    				ElementStub categoryElement = cell(table(tableName+"["+(noListTables-1)+"]"),i, c);
                    _logger.finest("Processing categoryElement " + categoryElement);
    				innerHTMLstring = categoryElement.fetch("innerHTML");
                    _logger.finest("CategoryElement " + categoryElement + " innerHTML: " + innerHTMLstring);
    				textString = categoryElement.getText();
                    _logger.finest("CategoryElement " + categoryElement + " text: " + textString);
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
    			_logger.log(Level.FINER, "Known Exception: " + ex.toString());
    			break;
    		}
    		rows.addLast((HashMap<String, String>) row.clone());
    		if(singleRow){
    			return rows;
    		}
    		if(conditional){
    			if(row.get(columnName).equalsIgnoreCase(columnValue)){
    				return rows;
    			}
    		}
    		row.clear();
    	}    	
    	_logger.log(Level.FINEST, "Table Details: "+rows);
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
    
    private void byPassConfirmationBox(){
        if(this.cell("Yes").near(this.cell("No")).exists()){
        	this.cell("Yes").near(this.cell("No")).click();
        }else{
        	_logger.log(Level.FINE, "Unable to find 'Confirmation' box!!");
        	_logger.log(Level.FINE, "Trying with 'Yes' button");
        	this.cell("Yes").click();
        }
    }
    
    public boolean clickDriftDetectNowOrDelete(String driftName, int divMaxIndex, long waitTime, boolean deleteDrift) throws InterruptedException{
    	
    	for(int i=divMaxIndex; i>=0; i--){
    		if(this.div(driftName+"["+i+"]").exists()){
    			this.div(driftName+"["+i+"]").click();
    			_logger.log(Level.INFO, "Clciked on, Drift Name:  "+driftName+"["+i+"]");
    			break;
    		}
    	}
    	
    	if(deleteDrift){
    		this.cell("Delete").near(this.cell("Delete All")).click();
    		//this.row("Delete[2]").click();
    		this.cell("Yes").near(this.cell("No")).click();
    		return this.link(driftName).exists();
    	}else{
    		if(this.cell("Detect Now").near(this.cell("Delete All")).exists()){
    			this.cell("Detect Now").near(this.cell("Delete All")).click();
    		}else{
    			this.cell("DetectNow").near(this.cell("Delete All")).click();
    		}
    		//This line added as a work-around for the issue --> Bug 949471
    		byPassConfirmationBox();
        	_logger.log(Level.INFO, "Waiting "+(waitTime/1000)+" Second(s) for agent/server drift actions...");
        	Thread.sleep(waitTime); //Give X second(s) for agent/server actions
        	return true;
    	}
    	
   }
    public boolean addDrift(String baseDir, String resourceName, String templateName, String driftName, String textBoxKeyValue, String radioButtons, String fileIncludes, String fileExcludes ) throws InterruptedException, IOException {
    	//Remove old file History If any
    	DriftManagementSSH driftSSH = new DriftManagementSSH();
		driftSSH.getConnection(SahiSettings.getJonAgentName(), SahiSettings.getJonAgentSSHUser(), SahiSettings.getJonAgentSSHPassword());
		if(!driftSSH.deleteFilesDirs(baseDir)){
			return false;
		}
		if(!driftSSH.createFileDir(baseDir)){
			return false;
		}
		driftSSH.closeConnection();
		
        //Select Resource
        if (resourceName != null) {
        	gotoDriftDefinationPage(resourceName, true);
        }
        
        this.cell("New").click();
        this.waitFor(1000*1);
        //This line added as a work-around for the issue --> Bug 949471
        //Comment this line as confirm box is not found [25-Nov-2013]
        // byPassConfirmationBox();
        
        //Select Template
        if(templateName != null){
        	selectComboBoxes(templateName);
        }
        this.waitFor(3*1000);
        
        this.cell("Next").click();
        if(this.cell("Next").exists()){
        	this.cell("Next").click();
        }
        
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
        		this.image("add.png").near(this.div("Includes")).click();
        		ElementStub row = this.textbox("path").parentNode("tr");
        		ElementStub checkBox = this.image("/checked/").in(row);;
                if(checkBox.isVisible()){
                	checkBox.click();
                	_logger.log(Level.INFO, "Path Check/uncheck available to select and selected...");
                }else{
                	_logger.log(Level.INFO, "Path Check/uncheck not available...");
                }
                this.textbox("path").setValue(fileName.trim());
                _logger.log(Level.INFO, "File Name added [Includes]: "+fileName);
                this.cell("OK").click();
        	}
        }
        
        //File Excludes
        if(fileExcludes != null){
        	String[] files = this.getCommaToArray(fileExcludes);
        	for(String fileName : files){
        	    int count = this.image("add.png").countSimilar();
        	    this.image("add.png").collectSimilar().get(count - 1).click();
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
    	return getRHQgwtTableFullDetails("listTable", tableCountOffset, "CreationTime,Definition,Snapshot,Category,Path,Resource,Ancestry", "Drift_add_16.png=added,Drift_change_16.png=changed,Drift_remove_16.png=removed");
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
		driftSSH.getConnection(SahiSettings.getJonAgentName(), SahiSettings.getJonAgentSSHUser(), SahiSettings.getJonAgentSSHPassword());
		
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
		driftSSH.getConnection(SahiSettings.getJonAgentName(), SahiSettings.getJonAgentSSHUser(), SahiSettings.getJonAgentSSHPassword());
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
    
    public boolean editAndSaveConfiguration(){
    	this.link("Inventory").click();
    	this.waitFor(5000);
    	this.cell("Servers").click();
    	this.link("RHQ Agent").click();
    	this.cell("Configuration").click();
    	if(!this.waitForElementExists(this, this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs"), "rhq.agent.plugins.availability-scan.initial-delay-secs", 1000*5)){
    		return false;
    	}
    	String defaultDelay = this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").getValue();
    	this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").setValue(defaultDelay + "1");
    	this.cell("Save").click();
    	return true;
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
    //* Metric Collection Schedules For Resources [enable/disable/update]
    //************************************************************************************************
    public void selectRowOnTable(String divName){
    	//Auto fix the max count of DIV
    	int divMaxIndex = this.div("divName").countSimilar();
    	for(int i=divMaxIndex; i>=0; i--){
    		if(this.div(divName+"["+i+"]").exists()){
    			this.div(divName+"["+i+"]").click();
    			_logger.log(Level.INFO, "Clicked, DIV Name:  "+divName+"["+i+"]");
    			break;
    		}
    	}
    }
    public void selectSchedules(String resourceName){
    	//selectResource(resourceName);
    	Resource agent = new Resource(this,System.getProperty("jon.agent.name"),"RHQ Agent");
	    agent.navigate();    	
	    int count = this.cell("Monitoring").countSimilar();
	    this.cell("Monitoring").collectSimilar().get(count - 1).click();
	    this.xy(cell("Schedules"), 3,3).click();
    }
    public int getMetricTableOffset(String resourceName){
    	if(resourceName != null){
    		selectSchedules(resourceName);
    	}  
    	String tableName = "listTable";
    	int numberTableAvailable = this.table(tableName).countSimilar();
    	_logger.log(Level.FINE, "OffSet - TABLE COUNT ("+tableName+"): "+numberTableAvailable);
    	return numberTableAvailable;
    }
    public int adjustMetricTableOffset(int orgOffset, int newOffest){
    	/*if(orgOffset >= newOffest){
    		return newOffest - orgOffset;
    	}else{
    		return 0;
    	}*/
    	return 0;
    }
    public LinkedList<HashMap<String, String>> getMetricTableDetails(String resourceName, int tableOffset){
    	if(resourceName != null){
    		selectSchedules(resourceName);
    	}  
    	String tableName = "listTable";
    	String tableColumns = "Metric,Description,Type,Enabled?,Collection Interval";
    	String replaceColumnValues = "permission_enabled_11.png=Enabled,permission_disabled_11.png=Disabled";
    	int numberTableAvailable = this.table(tableName).countSimilar();
    	_logger.log(Level.FINE, "TABLE COUNT ("+tableName+"): "+numberTableAvailable);
    	int tableOffsetNew = adjustMetricTableOffset(numberTableAvailable, tableOffset);
    	LinkedList<HashMap<String, String>> metricDetails = getRHQgwtTableFullDetails(tableName, tableOffsetNew, tableColumns, replaceColumnValues);
    	_logger.log(Level.INFO,"Number of Row: "+metricDetails.size());
    	return metricDetails;
    }
    public boolean enableDisableUpdateMetric(String resourceName, String metricName, String descrition, LinkedList<HashMap<String, String>> metricDetails, boolean updateCollectionInterval, String collectionIntervalValue, boolean enable, int tableOffset){
    	if(resourceName != null){
    		selectSchedules(resourceName);
    	}    
    	String[] collectionInterval = null;
    	String collectionIntervalStr = null;
    	int rowNo=-1;
    	String tableName = "listTable";
    	String tableColumns = "Metric,Description,Type,Enabled?,Collection Interval";
    	String replaceColumnValues = "permission_enabled_11.png=Enabled,permission_disabled_11.png=Disabled";
    	int tableOffsetNew = 0;    	
    	int numberTableAvailable = 0;
    	
    	if(metricDetails == null){
    		_logger.log(Level.INFO, "Metric table Details - NULL, reading metric table...");
    		numberTableAvailable= this.table(tableName).countSimilar();
        	_logger.log(Level.FINE, "TABLE COUNT ("+tableName+"): "+numberTableAvailable);
        	tableOffsetNew = adjustMetricTableOffset(numberTableAvailable, tableOffset);
        	metricDetails = getRHQgwtTableConditionalDetails(tableName, tableOffsetNew, tableColumns, replaceColumnValues, "Metric="+metricName);
        	_logger.log(Level.INFO,"Number of Row: "+metricDetails.size());
    	}
    	
    	for(int i=0; i<metricDetails.size(); i++){
    		if(metricDetails.get(i).get("Metric").equalsIgnoreCase(metricName)){
    			_logger.log(Level.INFO, "Metric Details: Row ("+(i+1)+"): "+metricDetails.get(i));
    			rowNo = i;
    			break;
    		}
    	}
    	if(rowNo == -1){
    		_logger.log(Level.WARNING, "Metric: "+metricName+" not found on the metric table!");
    		return false;
    	}

    	numberTableAvailable = this.table(tableName).countSimilar();
    	_logger.log(Level.FINE, "TABLE COUNT ("+tableName+"): "+numberTableAvailable);
    	tableOffsetNew = adjustMetricTableOffset(numberTableAvailable, tableOffset);
    	HashMap<String, String> metricDetail = getRHQgwtTableRowDetails(tableName, tableOffsetNew, tableColumns, replaceColumnValues, rowNo);
    	_logger.log(Level.INFO, "Metric: [Old Status: "+metricDetail+"] Table Offset: "+tableOffsetNew);

    	if(updateCollectionInterval){
    		collectionInterval = collectionIntervalValue.split(" ");
    		int hours = 0;
			int minutes = 0;
			int seconds = 0;
			int rawValue = Integer.parseInt(collectionInterval[0].trim());
    		if(collectionInterval[1].equalsIgnoreCase("seconds")){
    			hours = rawValue / (60*60);
    			minutes = rawValue / 60;
    			seconds = rawValue % 60;   			
    		}else if(collectionInterval[1].equalsIgnoreCase("minutes")){
    			hours = rawValue / 60;
    			minutes = rawValue % 60;
    		}else{
    			hours = rawValue;
    		}
    		if(hours > 0){
    			collectionIntervalStr = hours +" hours";
    		}
    		if(minutes > 0){
    			if(collectionIntervalStr != null){
    				collectionIntervalStr += ", "+minutes+" minutes";
    			}else{
    				collectionIntervalStr = minutes+" minutes";
    			}
    		}
    		if(seconds > 0){
    			if(collectionIntervalStr != null){
    				collectionIntervalStr += ", "+seconds+" seconds";
    			}else{
    				collectionIntervalStr = seconds+" seconds";
    			}
    		}
    		_logger.log(Level.FINE, "Reference Collection Value: "+collectionIntervalStr);
    		if(metricDetail.get("Collection Interval").equalsIgnoreCase(collectionIntervalStr)){
        		_logger.log(Level.WARNING, "Metric: "+metricName+" collection interval already defined as "+metricDetail.get("Collection Interval")+". Nothing to do..");
    			return true;
        	}
    	}else{
    		if(metricDetail.get("Enabled?").equalsIgnoreCase("Enabled") == enable){
        		_logger.log(Level.WARNING, "Metric: "+metricName+" already in "+metricDetail.get("Enabled?")+" state. Nothing to do..");
    			return true;
        	}
    	}
    	
    	//selectRowOnTable(metricName);
    	this.div(metricName).near(this.div(descrition)).click();
    	
    	if(updateCollectionInterval){
    		this.textbox("interval").setValue(collectionInterval[0].trim());
    		if(!collectionInterval[1].trim().equalsIgnoreCase("minutes")){
    			selectComboBoxes("minutes --> "+collectionInterval[1].trim());
    		}
    		if(this.cell("Set[1]").exists()){
    			this.xy(cell("Set[1]"),3,3).click();
    		}else{
    			this.xy(cell("Set"),3,3).click();
    		}
            
            if(!collectionInterval[1].trim().equalsIgnoreCase("minutes")){
            		selectComboBoxes(collectionInterval[1].trim()+" --> minutes");            	
            }
    	}else{
    		if(enable){
        		//this.cell("Enable").under(this.label("Collection Interval")).click();
    			//this.cell("Enable").near(this.cell("/Total Rows:/")).click();
    			this.cell("Enable").in(this.div("/EnableDisable.*Refresh/")).click(); //General Fix : 18-Jul-2014
        	}else{
        		//this.cell("Disable").under(this.label("Collection Interval")).click();
    			//this.cell("Disable").near(this.cell("/Total Rows:/")).click();
    			this.cell("Disable").in(this.div("/EnableDisable.*Refresh/")).click(); //General Fix : 18-Jul-2014
        	}
    	}
    	this.waitFor(1000*2); //wait 2 seconds to get load table details
    	 numberTableAvailable = this.table(tableName).countSimilar();
     	_logger.log(Level.FINE, "TABLE COUNT ("+tableName+"): "+numberTableAvailable);
         tableOffsetNew = adjustMetricTableOffset(numberTableAvailable, tableOffset);
    	
    	metricDetail = getRHQgwtTableRowDetails(tableName, tableOffsetNew, tableColumns, replaceColumnValues, rowNo);
    	_logger.log(Level.INFO, "Metric: New Status: "+metricDetail);
    	if(metricDetail.get("Metric").equalsIgnoreCase(metricName)){
    		if(updateCollectionInterval){
        		if(metricDetail.get("Collection Interval").equalsIgnoreCase(collectionIntervalStr)){
            		return true;
            	}
        	}else{
    			if(metricDetail.get("Enabled?").equalsIgnoreCase("Enabled") == enable){
    				return true;
    			}
        	}
    	}
		return false;
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
    	this.waitForElementExists(this, this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs"), "rhq.agent.plugins.availability-scan.initial-delay-secs", 1000*5);
    	String defaultDelay = this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").getValue();
        this.textbox("rhq.agent.plugins.availability-scan.initial-delay-secs").setValue(defaultDelay + "1");
        this.cell("Save").click();
    	
    }

    //************************************************************************************************
    // Metric Collection Schedules For Groups
    //*********************************************************************************************
    
    public void scheduleDisableForGroup(String panelName, String compatibleGroup, String groupDesc, ArrayList<String> resourceList){
    	createGroup(panelName, compatibleGroup, groupDesc,  resourceList);
    	this.link(compatibleGroup).click();
    	this.cell("Monitoring").click();
        this.xy(cell("Schedules"), 3,3).click();
        this.div("JVM Total Memory[1]").click();
        this.cell("Disable").click();      
        
    }
    public void enableScheduleGroup(){
    	this.div("JVM Total Memory[1]").click();
        this.cell("Enable").click();
        
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
    //* Administration Page(s) validation [NON-JSP pages] - added on 08-Mar-2013
    //***********************************************************************************************
    
    
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
    public boolean validatePage(String page, String tableName, String tableColumns, int columnIndexFrom, int minRowCount, boolean jsp){
    	navigateToAdministrationPage(page, tableName);
    	
    	LinkedList<String> header = null;
    	LinkedList<LinkedList<String>> tableContent = null;
    	if(jsp){
    		header = getTableHeader(columnIndexFrom);
        	tableContent = getJSPtable(tableName, header.size(), columnIndexFrom);
    	}else{
    		String headerReference = "/headerButton/";
        	String cellReference = "/tallCell/";
        	header = getTableHeader(headerReference, columnIndexFrom);
        	tableContent = getTable(cellReference, header.size());
    	}
    	
    	
    	
    	
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
    
    public LinkedList<String> getTableHeader(String columnReference, int columnIndexFrom){
    	LinkedList<String> header = new LinkedList<String>();
       	int tableHeaderCount = this.cell(columnReference).countSimilar();
    	_logger.log(Level.FINE, "Table Header Count: "+tableHeaderCount);
    	for(int i=columnIndexFrom; i<tableHeaderCount;i++){
    		header.addLast((this.cell(columnReference+"["+i+"]")).getText());
    	}
    	return header;
    }
    
    public LinkedList<String> getTableHeader(int columnIndexFrom){
    	LinkedList<String> header = new LinkedList<String>();
    	String tableHeadertext = "/rich-table-subheadercell/";
    	String headerText = "headerText";
    	int tableHeaderCount = this.tableHeader(tableHeadertext).countSimilar();
    	_logger.log(Level.FINE, "Table Header Count: "+tableHeaderCount);
    	for(int i=columnIndexFrom; i<tableHeaderCount;i++){
    		header.addLast(this.span(headerText).in(this.tableHeader(tableHeadertext+"["+i+"]")).getText());
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
    
    @SuppressWarnings("unchecked")
	public LinkedList<LinkedList<String>> getTable(String cellReference, int columnSize){
    	LinkedList<LinkedList<String>> table = new LinkedList<LinkedList<String>>();
    	LinkedList<String> row = new LinkedList<String>();
    	
    	int noCell = this.cell(cellReference).countSimilar();
    	for(int cell=0;cell<noCell;cell++){
    		for(int col=0; col<columnSize; col++){
    			row.addLast(this.cell(cellReference+"["+cell+"]").getText());
    		}
    		table.addLast((LinkedList<String>) row.clone());
			row.clear();
    	}
    	return table;
    }
    
    //*************************************************************************************
    //* Get Agent status
    //*************************************************************************************
    public boolean isAgentRunning(String agentName) {
    	this.link("Inventory").click();
        this.cell("Platforms").click();
        this.setSearchBox(agentName.trim());
        LinkedList<HashMap<String, String>> agents = getRHQgwtTableFullDetails("listTable", 2, "Resource Type,Name,Ancestry,Description,Type,Version,Availability", "availability_red_16.png=Down,availability_green_16.png=Up");
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
    	long maximunWaitTime = 1000*60*10;
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
    	
    	//Modified "Starting up, please wait..." to "Starting up, please wait... (this may take several minutes)" from 03-June-2012 build.
    	if(!this.waitForElementExists(this, this.span("Starting up, please wait... (this may take several minutes)"), "Span: Starting up, please wait... (this may take several minutes)", 1000*30)){
    		//return false;
    		//Adding manual wait due to issue with sahi proxy server 
    		//Bug: https://bugzilla.redhat.com/show_bug.cgi?id=765670
    		//JBOSS AS7 Plug-in's are taking long minutes...
    		_logger.log(Level.WARNING, "[Starting up, please wait...] not found, hence will wait here "+(maximunWaitTime/1000)+" Second(s)");
    		this.waitFor(maximunWaitTime);
    	}

    	if(!this.waitForElementExists(this, this.link("Done! Click here to get started!"), "Link: Done! Click here to get started!", (int)(maximunWaitTime - (1000*60*4)))){
    		this.navigateTo("/installer/start.jsf", true);
    		if(this.heading1("The Server Is Installed!").exists()){
    			if(this.link("Click here to get started!").exists()){
    				this.link("Click here to get started!").click();
    				return true;
    			}
    		}
    		return false;
    	}

    	this.link("Done! Click here to get started!").click();
    	
    	return true;    	

    }
    
    //*************************************************************************************
    //* Import Resources
    //*************************************************************************************
    public boolean importResources(String resourceName) throws InterruptedException{
    	this.link("Inventory").click();
    	this.cell("Discovery Queue").click();
    	if(this.waitForElementRowExists(this, "No items to show", 1000*5)){
    		_logger.log(Level.INFO, "Do not find any resources in Discovery Queue, Clicking Refresh button and checking again...");
    		Thread.sleep(1000*60*5); //Hold on 5 minutes, there will be an agent and server communication may be going on..
    		this.cell("Refresh").near(this.cell("Deselect All")).click();
    		if(this.waitForElementRowExists(this, "No items to show", 1000*5)){
    			_logger.log(Level.WARNING, "No items to import!");
        		return false;
    		}    		
    	}
    	if(resourceName != null){
    		LinkedList<HashMap<String, String>> discoveryQueue = getRHQgwtTableFullDetails("listTable", 2, "Resource Name, Resource Key, Resource Type, Description, Inventory Status, Discovery Time", null);
        	_logger.log(Level.INFO, "Table Details: Number of Row(s): "+discoveryQueue.size());
        	for(int i=0; i<discoveryQueue.size(); i++){
        		if(resourceName.equalsIgnoreCase(discoveryQueue.get(i).get("Resource Name"))){
        			_logger.log(Level.INFO, "Row: ["+(i+1)+"]: "+discoveryQueue.get(i));
        			this.image("/unchecked.*/["+i+"]").click();
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
    // TEXT search for Groups
    public boolean searchComaptibilityGroupWithText(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList){
    	//'SearchPatternField' is on JBOSS ON and 'search' is on RHQ 4.5 and above
    	selectPage("Inventory-->"+groupPanelName, this.textbox("search"), 1000*5, 3);
    	setSearchBox(groupName);
    	
        if(!this.link(groupName.trim()).exists()){
        	_logger.log(Level.INFO, "Group ["+groupName+"] unavailable, Creating new one...");
        	createGroup(groupPanelName, groupName, groupDesc, resourceList);
        }else{
        	return true;
        }
        setSearchBox(groupName);
        return this.link(groupName.trim()).exists();
    }

    public boolean searchAllGroupWithText(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList){
    	return searchComaptibilityGroupWithText(groupPanelName, groupName, groupDesc, resourceList);
    }
    public boolean searchMixedGroupWithText(String groupPanelName, String groupName, String groupDesc, ArrayList<String> resourceList){
    	return searchComaptibilityGroupWithText(groupPanelName, groupName, groupDesc, resourceList);
    }

    //************************************************************************************************
    // AlertDefinitionTemplate Tests
    //***************************************************************************************************
    public void createAlertDefinitionTemplate(String groupPanelName, String templateName, String platform){
    	this.link("Administration").click();
    	this.cell(groupPanelName).click();
    	ElementStub row = this.div(platform).parentNode("tr");
        this.image("edit.png").in(row).click();
    	this.cell("New").click();
    	this.textbox("textItem").setValue(templateName);
    	this.textarea("textItem").setValue("Created by Automation");
    	this.cell("Save").click();
    }
    public boolean verifyDefinitionTemplateExists(String groupPanelName, String templateName) {
    	this.div("/Back to List/").click();
        return this.div(templateName).exists();
    }
    
    public void navigationThruConditionsTabAlertDefTemplate(String groupPanelName, String templateName){
    	this.link(templateName).click();
        this.cell("Edit").click();
        this.cell("Conditions").click();
        this.cell("Add").click();
        this.cell("OK").click();
        this.cell("Save").click();       
        this.div("/Back to List/").click(); //PatternFly Change: 15-Jul-2014

    	
    }
    public void navigationThruNotificationsTabAlertDefTemplate(String groupPanelName, String templateName){
    	/*this.cell("Administration").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        this.image("edit.png[3]");*/
        this.link(templateName).click();
        this.cell("Edit").click();
        this.cell("Notifications").click();
        this.cell("Add").click();
        // this.div("selectItemText[2]").click();
        // this.div(templateName).click();
        //this.image("right.png").click();
        this.div("/right_all.*/").click();
        this.cell("OK").click();
        this.cell("Save").click();
        this.div("/Back to List/").click();
    	
    }
    public void navigationThruRecoveryTabAlertDefTemplate(String groupPanelName, String templateName){
    	/*this.cell("Administration").click();
    	this.link("Administration").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        this.image("edit.png[3]");*/
        this.link(templateName).click();
        this.cell("Edit").click();
        this.cell("Recovery").click();
       // this.div("selectItemText[2]").click();
        this.radio("yes[1]").click();
        this.radio("no[1]").click();
        this.cell("Save").click();
        this.div("/Back to List/").click();
    	
    }
    public void navigationThruDampeningTabAlertDefTemplate(String groupPanelName, String templateName){
    	/*this.cell("Administration").click();
        this.waitFor(5000);
        this.cell(groupPanelName).click();
        this.image("edit.png[3]");*/
        this.link(templateName).click();
        this.cell("Edit").click();
        this.cell("Dampening").click();
       // this.div("selectItemText[2]").click();
        this.cell("Save").click();
        this.div("/Back to List/").click();
    	
    }
    public boolean deleteAlertDefinitionTemplate(String templateName){
    	if(!selectDivElement(templateName)){
    		return false;
    	}
    	this.cell("Delete").near(this.cell("Disable")).click();
    	this.cell("Yes").near(this.cell("No")).click(); 
    	return this.link(templateName.trim()).exists();
    }
    
	public void servicesSearchBy(String key, String value) {
		this.link("Inventory").click();
		this.waitFor(5000);
		Assert.assertTrue(this.link("Inventory").exists());
		this.cell("Services").click();
		this.waitFor(5000);
		checkSearchBox();
		setSearchBox("version==2");
		this.waitFor(5000);
	}
	
	 public void createGroupWithAllResources(String groupPanelName, String groupName, String groupDesc) {
	        this.link("Inventory").click();
	        this.waitFor(5000);
	        this.cell(groupPanelName).click();
	        this.cell("New").click();
	        this.textbox("name").setValue(groupName);
	        this.textarea("description").setValue(groupDesc);
	        this.cell("Next").click();
	        this.image("/right_all.*/").click();
	        this.cell("Finish").click();
	    }
	    public void  removeResourcesFromGroup(String groupPanelName, String groupName){
			this.link("Inventory").click();
			this.waitFor(5000);
			this.cell(groupPanelName).click();
			this.link(groupName).click();
			this.image("/Inventory_grey_16.*/").click();
			this.cell("Update Membership...").click();
			this.image("/left_all.*/").click();
			this.cell("Save").click();
	    }
	    
	    public void createRoleWithPermissions(String roleName, String desc, String compGroupName, String searchTestuser, String [] permissions) {
	        this.link("Administration").click();
	        this.cell("Roles").click();
	        this.cell("New").click();
	        this.textbox("name").setValue(roleName);
	        this.textbox("description").setValue(desc);
	        if (permissions != null){
	        	for (int i =0; i< permissions.length ;i++ ){
	        		this.div(permissions[i]).click();
	        		if(this.image("/unchecked.*/").near(this.div(permissions[i])).exists()){
	        			this.image("/unchecked.*/").near(this.div(permissions[i])).click();
	        		}
	        	}
	        }
	        this.cell("Resource Groups").click();
	        this.div(compGroupName).click();
	        this.image("/right.*/").click();//PatternFly fix: 23-Jul-2014
	        this.cell("Users").click();
	        this.div(searchTestuser).click();
	        int count = this.image("/right.*/").countSimilar();
	        this.image("/right.*/["+(count-1)+"]").click(); //PatternFly fix: 23-Jul-2014
	        this.cell("Save").click();
	    }
	    
	    public void removePermissionsFromRole(String roleName, String desc, String compGroupName, String searchTestuser, String [] permissions){
	    	this.link("Administration").click();
	        this.cell("Roles").click();
	        this.link(roleName).click();
	        this.textbox("name").setValue(roleName);
	        this.textbox("description").setValue(desc);
	        if (permissions != null){
	        	for (int i =0; i< permissions.length ;i++ ){
	        		this.div(permissions[i]).click();
	        	}
	        }
	        this.image("/checked.*/").click();
	        this.cell("Save").click();
	    }

	public boolean isUserAvailable(String userName){
		selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
		if(this.link(userName).in(this.div("gridBody")).exists()){
			return true;
		}
		_logger.log(Level.FINE, "User["+userName+"] is not available");
		return false;
	}
	
	public void preConfigPermissionTest(String userName, String password, String firstName, String lastName, String email, String groupPanelName, String groupName, String groupDesc, String roleName, String roleDesc, String[] permissions){
		if(!this.getCurrentLogin().equals(ADMIN_USER)){
			relogin(ADMIN_USER, ADMIN_PASSWORD);
		}
		if(!this.isUserAvailable(userName)){
				createUser(userName, password, firstName, lastName, email);
		}		
		createGroup(groupPanelName, groupName, groupDesc, "RHQ Agent");
		createRoleWithPermissions(roleName, roleDesc, groupName, userName, permissions);
		//relogin(userName, password);	
	}
	
	public void postConfigPermissionTest(String userName, String role, String groupPanelName, String groupName) throws SahiTasksException{
		relogin(ADMIN_USER, ADMIN_PASSWORD);
		deleteUser(userName);
		deleteRole(role);
		deleteGroup(groupPanelName, groupName);
	}
	    
	public void checkManageSecurity(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		
		String [] permissions = new String [] {"Manage Security"};
		preConfigPermissionTest(searchTestuser, password, firstName, secondName, emailId, "Compatible Groups", compTestGroup, desc, roleName, desc, permissions);
		
		// login with created user
		relogin(searchTestuser, password);
		// go to Administration-->Users
		this.link("Administration").click();
		this.cell("Users").click();
		this.link("rhqadmin").click();
		this.waitFor(5000);
		Assert.assertTrue(this.cell("Cancel").exists());
		Assert.assertTrue(this.password("password").exists());
		// login with rhqadmin user
		relogin(ADMIN_USER, ADMIN_PASSWORD);
		// remove manage security permission
		removePermissionsFromRole(roleName, desc, compTestGroup,
				searchTestuser, permissions);
		// login with created user
		relogin(searchTestuser, password);
		this.link("Administration").click();
		this.cell("Users").click();
		this.link("rhqadmin").click();
		this.waitFor(5000);
		Assert.assertFalse(this.cell("Cancel").exists());
		Assert.assertFalse(this.password("password").exists());
		
		postConfigPermissionTest(searchTestuser, roleName, "All Groups", compTestGroup);
	}
	
	public void checkManageInventory(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {

		String [] permissions = new String [] {"Manage Inventory"};
		preConfigPermissionTest(searchTestuser, password, firstName, secondName, emailId, "Compatible Groups", compTestGroup, desc, roleName, desc, permissions);
		
		// login with created user
		relogin(searchTestuser, password);
		// go to Administration-->Users
		this.link("Inventory").click();
		this.waitFor(2000);
		this.cell("Discovery Queue").click();
		Assert.assertTrue(this.span("Discovery Queue").exists());
		this.cell("Servers").click();
		this.cell("RHQ Agent").click();
		this.cell("Disable").click();
		this.cell("Yes").click();
		this.cell("RHQ Agent").click();
		this.cell("Enable").click();
		this.cell("Yes").click();
//		// login with rhqadmin user
		relogin(ADMIN_USER, ADMIN_PASSWORD);
//		// remove manage security permission
		removePermissionsFromRole(roleName, desc, compTestGroup,
				searchTestuser, permissions);
		// login with created user
		relogin(searchTestuser, password);
		this.link("Inventory").click();
		this.waitFor(2000);
		this.cell("Discovery Queue").click();
		Assert.assertFalse(this.span("Discovery Queue").exists());
		postConfigPermissionTest(searchTestuser, roleName, "All Groups", compTestGroup);
	}
	
	public void createTestRepo(String testRepoName){
		this.link("Administration").click();
		this.cell("Repositories").click();
		this.submit("CREATE NEW").click();
		this.textbox("createRepoDetailsForm:name").setValue(testRepoName);
	//	this.checkbox("createRepoDetailsForm:isPrivate").uncheck();
		this.select("createRepoDetailsForm:j_id13").click();
		this.option("--None--").click();
		this.submit("SAVE").click();
	}
	public void deleteTestRepo(String testRepoName){
		this.link("Administration").click();
		this.cell("Repositories").click();
		this.checkbox("selectedRepos").near(this.link("testRepo")).click();
		this.submit("DELETE SELECTED").click();
	}
	
	public void checkManageRespository(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		
		String [] permissions = new String [] {"Manage Repositories"};
		preConfigPermissionTest(searchTestuser, password, firstName, secondName, emailId, "Compatible Groups", compTestGroup, desc, roleName, desc, permissions);
		
		//create test repo
		String testReponame="testRepo";
		createTestRepo(testReponame);
		
		// login with created user
		relogin(searchTestuser, password);
		
		// go to Administration-->Users
		this.link("Administration").click();
		this.waitFor(2000);
		this.cell("Content Sources").click();
		this.waitFor(2000);
		Assert.assertTrue(this.submit("CREATE NEW").exists());
		this.link("Administration").click();
		this.waitFor(2000);
		this.cell("Repositories").click();
		this.waitFor(2000);
		Assert.assertTrue(this.link(testReponame).exists());
		// login with rhqadmin user
		relogin(ADMIN_USER, ADMIN_PASSWORD);
		// remove manage security permission
		removePermissionsFromRole(roleName, desc, compTestGroup,
				searchTestuser, permissions);
		// login with created user
		relogin(searchTestuser, password);
		this.link("Administration").click();
		this.waitFor(2000);
		this.cell("Content Sources").click();
		this.waitFor(2000);
		Assert.assertFalse(this.button("CREATE NEW").exists());
		this.cell("Repositories").click();
		this.waitFor(2000);
		Assert.assertFalse(this.link(testReponame).exists());
				
		postConfigPermissionTest(searchTestuser, roleName, "All Groups", compTestGroup);
	}

	public void checkViewUsers(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		
		String [] permissions = new String [] {"View Users"};
		preConfigPermissionTest(searchTestuser, password, firstName, secondName, emailId, "Compatible Groups", compTestGroup, desc, roleName, desc, permissions);
		
		// login with created user
		relogin(searchTestuser, password);
		
		// go to Administration-->Users
		selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
		Assert.assertTrue(this.waitForElementVisible(this, this.link(searchTestuser),
		        "Tested user link", Timing.WAIT_TIME),"User exists");
		Assert.assertTrue(this.link("rhqadmin").exists());
		this.link(searchTestuser).under(this.cell("Username")).click();
		Assert.assertTrue(this.waitForElementVisible(this, this.password("password"),
                "Tested user pasword field", Timing.WAIT_TIME),"pasword field exists");
		Assert.assertTrue(this.span("Edit User ["+searchTestuser+"]").exists());
		selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
		this.link("rhqadmin").under(this.cell("Username")).click();
		Assert.assertFalse(this.waitForElementVisible(this, this.password("password"),
                "Tested user pasword field", Timing.WAIT_TIME),"pasword field exists");
		Assert.assertTrue(this.span("View User [rhqadmin]").exists());
		// login with rhqadmin user
		relogin(ADMIN_USER, ADMIN_PASSWORD);
		// remove manage security permission
		removePermissionsFromRole(roleName, desc, compTestGroup,
				searchTestuser, permissions);
		// login with created user
		relogin(searchTestuser, password);
		
		selectPage("Administration-->Users", this.cell("Username"), 1000*5, 2);
		Assert.assertTrue(this.waitForElementVisible(this, this.link(searchTestuser),
                "Tested user link", Timing.WAIT_TIME),"User exists");
		Assert.assertFalse(this.link("rhqadmin").exists());
		this.link(searchTestuser).under(this.cell("Username")).click();
		Assert.assertTrue(this.waitForElementVisible(this, this.password("password"),
                "Tested user pasword field", Timing.WAIT_TIME),"pasword field exists");
		Assert.assertTrue(this.span("Edit User ["+searchTestuser+"]").exists());
		
		postConfigPermissionTest(searchTestuser, roleName, "All Groups", compTestGroup);
	}
	public void checkManageSettings(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
	
		String [] permissions = new String [] {"Manage Settings"};
		preConfigPermissionTest(searchTestuser, password, firstName, secondName, emailId, "Compatible Groups", compTestGroup, desc, roleName, desc, permissions);
		
		// login with created user
		relogin(searchTestuser, password);
		
		// go to Administration-->Users
		this.link("Administration").click();
		Assert.assertTrue(this.cell("System Settings").exists());
		this.cell("System Settings").click();
		Assert.assertTrue(this.cell("Server Local Time :").exists());
		// login with rhqadmin user
		relogin(ADMIN_USER, ADMIN_PASSWORD);
		// remove manage security permission
		removePermissionsFromRole(roleName, desc, compTestGroup,
				searchTestuser, permissions);
		// login with created user
		relogin(searchTestuser, password);
		this.link("Administration").click();
		Assert.assertTrue(this.cell("System Settings").exists());
		this.cell("System Settings").click();
		Assert.assertFalse(this.cell("Server Local Time :").exists());
		
		postConfigPermissionTest(searchTestuser, roleName, "All Groups", compTestGroup);
	}

	public void checkManageBundles(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
	
		String [] permissions = new String [] {"Manage Bundles"};
		preConfigPermissionTest(searchTestuser, password, firstName, secondName, emailId, "Compatible Groups", compTestGroup, desc, roleName, desc, permissions);
		
		// login with created user
		relogin(searchTestuser, password);
		
		// go to Bundles
		this.link("Bundles").click();
		this.cell("New").near(this.cell("Deploy")).click();
		this.waitFor(3000);
		Assert.assertTrue(this.cell("Next").exists());
		this.cell("Cancel").click();
				
		// login with rhqadmin user
		relogin(ADMIN_USER, ADMIN_PASSWORD);
		// remove manage security permission
		removePermissionsFromRole(roleName, desc, compTestGroup, searchTestuser, permissions);
		// login with created user
		relogin(searchTestuser, password);
		
		// go to Bundles
		this.link("Bundles").click();
		this.cell("New").near(this.cell("Deploy")).click();
		this.waitFor(3000);
		Assert.assertTrue(this.cell("Next").exists());
		this.cell("Cancel").click();
		
		postConfigPermissionTest(searchTestuser, roleName, "All Groups", compTestGroup);
	}

	public void checkGroupsPermission(String searchTestuser, String password, String firstName, String secondName, String emailId, String roleName, String desc, String compTestGroup, String searchQueryName) throws SahiTasksException {
		
		preConfigPermissionTest(searchTestuser, password, firstName, secondName, emailId, "Compatible Groups", compTestGroup, desc, roleName, desc, null); //this is the default role creation which has View Users checked
		
		// login with created user
		relogin(searchTestuser, password);
		
		// go to Administration-->Users
		this.link("Inventory").click();
		this.waitFor(2000);
		this.cell("Servers").click();
		this.waitFor(2000);
		Assert.assertTrue(this.div("RHQ Agent").exists());
				
		// login with rhqadmin user
		relogin(ADMIN_USER, ADMIN_PASSWORD);
		// remove resources from group
		removeResourcesFromGroup("All Groups", compTestGroup);
		// login with created user
		relogin(searchTestuser, password);

		this.link("Inventory").click();
		this.waitFor(2000);
		this.cell("Servers").click();
		this.waitFor(2000);
		Assert.assertFalse(this.div("RHQ Agent").exists());
		
		postConfigPermissionTest(searchTestuser, roleName, "All Groups", compTestGroup);
	}
	
	//*************************************************************************************
    //* Drift Definition Template
    //*************************************************************************************	
	
	public void createDriftDefinitionTemplate(String name) {
		this.link("Administration").click();
		Assert.assertTrue(this.cell("Drift Definition Templates").exists());
		this.cell("Drift Definition Templates").click();
		this.image("edit.png").near(this.div("Linux")).click();
		this.cell("New").click();
		this.cell("Next").click();
		this.textbox("name").setValue(name);
		this.cell("Finish").click();
	}
	
	public void editDriftDefinitionTemplate(String name) {
		this.link("Administration").click();
		Assert.assertTrue(this.cell("Drift Definition Templates").exists());
		this.cell("Drift Definition Templates").click();
		this.image("edit.png").near(this.div("Linux")).click();
		this.link(name).click();
		this.textbox("interval").setValue("123456");
		this.cell("Save").click();
		String msg = "Drift template updated and changes pushed to attached definitions.";
		Assert.assertTrue(this.waitForAnyElementsToBecomeVisible(this,
                new ElementStub[]{this.cell(msg),this.div(msg)},
                "Successful message", Timing.WAIT_TIME),msg);
		
	}
	
	public void deleteDriftDefinitionTemplate(String name) {
		this.link("Administration").click();
		Assert.assertTrue(this.cell("Drift Definition Templates").exists());
		this.cell("Drift Definition Templates").click();
		this.image("edit.png").near(this.div("Linux")).click();
		this.image("availability_green_16.png").near(this.cell(name)).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
		Assert.assertFalse(this.cell(name).exists());
		
	}
	
	//Changed the search field name from the version : 4.5.0-SNAPSHOT, Build Number: 1704544. Still we have the field in JBOSS ON, Hence dealing with both search fields
	public void setSearchBox(String boxValue){
		if(this.textbox("SearchPatternField").exists()){
    		this.textbox("SearchPatternField").setValue(boxValue);
    		this.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);"); //13 - Enter key
    	}else{
    		this.textbox("search").click();
    		this.textbox("search").setValue(boxValue);
    		this.waitFor(2000);
    		this.execute("_sahi._keyPress(_sahi._textbox('search'), 13);"); //13 - Enter key
    	}       
	}	

}
