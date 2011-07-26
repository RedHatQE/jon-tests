package com.redhat.qe.jon.sahi.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class DashboardTest  extends SahiTestScript  {
	
	
	@Test (groups="DashboardTest")
	public void viewDashboard() {
		
		sahiTasks.clickDashboardTopLevelMenu(); 
             		
	}
	
	@Test (groups="DashboardTest")
	public void editDashboard() {
		
		//sahiTasks.editDashboard();
             		
	}
	
	@Test (groups="DashboardTest")
	public void newDashboard() {
		sahiTasks.newDashboard(); 
             		
	}
	
	
	@Test (groups="DashboardTest")
	public void messagePortletExists() {
		
		Assert.assertTrue(sahiTasks.messagePortletExists()); 
             		
	}
	
	@Test (groups="DashboardTest")
	public void inventorySummaryPortletExists() {
		 
		Assert.assertTrue(sahiTasks.inventorySummaryPortletExists()); 
             		
	}

	@Test (groups="DashboardTest")
	public void mashupPortletExists() {
		Assert.assertTrue(sahiTasks.mashupPortletExists()); 
             		
	}
	
	@Test (groups="DashboardTest")
	public void recentAlertsPortletExists() {
		Assert.assertTrue(sahiTasks.recentAlertsPortletExists());  
             		
	}
	
	
	@Test (groups="DashboardTest")
	public void alertedOrUnavailableResourcesPortlet() {
		Assert.assertTrue(sahiTasks.alertedOrUnavailableResourcesPortletExists());  
             		
	}
	
	@Test (groups="DashboardTest")
	public void recentOperationsPortlet() {
		Assert.assertTrue(sahiTasks.recentOperationsPortletExists());   
             		
	}
	
	
	@Test (groups="DashboardTest")
	public void portletRefresh() {   
          sahiTasks.messagePortletRefresh();   		
	}
	
	
	@Test (groups="DashboardTest")
	public void portletSettings() {   
           //todo  		
	}
	
	/*  todo:  test fails intermittently
	@Test (groups="DashboardTest")
	public void portletMinimizeMaximize() {   
           sahiTasks.messagePortletMinimizeMaximize();
	}
	*/
	
	@Test (groups="DashboardTest")
	public void verifyInventorySummaryPortlet() {   
           sahiTasks.verifyInventorySummaryPortlet();
	}
	
	@Test (groups="DashboardTest")
	public void verifyTabName() {   
           sahiTasks.verifyDefaultTabName();
	}
	
	
}
