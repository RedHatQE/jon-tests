package com.redhat.qe.jon.sahi.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class DashboardTest  extends SahiTestScript  {	

	@Test (groups="DashboardTest")
	public void viewDashboard() {
		sahiTasks.clickDashboardTopLevelMenu();             		
	}

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void editDashboard() {		
		sahiTasks.editDashboard();             		
	}

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void newDashboard() {
		sahiTasks.newDashboard();              		
	}	

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void messagePortletExists() {
		Assert.assertTrue(sahiTasks.messagePortletExists());             		
	}

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void inventorySummaryPortletExists() {		 
		Assert.assertTrue(sahiTasks.inventorySummaryPortletExists());             		
	}

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void mashupPortletExists() {
		Assert.assertTrue(sahiTasks.mashupPortletExists());              		
	}

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void recentAlertsPortletExists() {
		Assert.assertTrue(sahiTasks.recentAlertsPortletExists());             		
	}	

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void alertedOrUnavailableResourcesPortlet() {
		Assert.assertTrue(sahiTasks.alertedOrUnavailableResourcesPortletExists());             		
	}

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void recentOperationsPortlet() {
		Assert.assertTrue(sahiTasks.recentOperationsPortletExists());             		
	}	

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void portletRefresh() {   
		sahiTasks.messagePortletRefresh();   		
	}		

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void verifyInventorySummaryPortlet() {   
		sahiTasks.verifyInventorySummaryPortlet();
	}

	@Test (groups="DashboardTest", dependsOnMethods={"viewDashboard"})
	public void verifyTabName() {   
		sahiTasks.verifyDefaultTabName();
	}	

}
