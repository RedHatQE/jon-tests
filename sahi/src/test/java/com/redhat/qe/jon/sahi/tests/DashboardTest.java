package com.redhat.qe.jon.sahi.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class DashboardTest  extends SahiTestScript  {	

	@Test (groups="DashboardTestJON")
	public void viewDashboard() {
		sahiTasks.clickDashboardTopLevelMenu();             		
	}

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void editDashboard() {		
		sahiTasks.editDashboard();             		
	}

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void newDashboard() {
		sahiTasks.newDashboard();              		
	}	

	// no longer available on default installation
	//@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void messagePortletExists() {
		Assert.assertTrue(sahiTasks.messagePortletExists());             		
	}

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void inventorySummaryPortletExists() {		 
		Assert.assertTrue(sahiTasks.inventorySummaryPortletExists());             		
	}

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void mashupPortletExists() {
		Assert.assertTrue(sahiTasks.mashupPortletExists());              		
	}

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void recentAlertsPortletExists() {
		Assert.assertTrue(sahiTasks.recentAlertsPortletExists());             		
	}	

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void alertedOrUnavailableResourcesPortlet() {
		Assert.assertTrue(sahiTasks.alertedOrUnavailableResourcesPortletExists());             		
	}

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void recentOperationsPortlet() {
		Assert.assertTrue(sahiTasks.recentOperationsPortletExists());             		
	}	

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void portletRefresh() {   
		sahiTasks.messagePortletRefresh();   		
	}		

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void verifyInventorySummaryPortlet() {   
		sahiTasks.verifyInventorySummaryPortlet();
	}

	@Test (groups="DashboardTestJON", dependsOnMethods={"viewDashboard"})
	public void verifyTabName() {   
		sahiTasks.verifyDefaultTabName();
	}	

}
