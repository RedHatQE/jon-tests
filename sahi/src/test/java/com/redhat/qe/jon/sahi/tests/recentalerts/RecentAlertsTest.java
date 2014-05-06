package com.redhat.qe.jon.sahi.tests.recentalerts;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.recentalerts.DashboardRecentAlertsTasks;

/**
 * @author mmahoney
 */

public class RecentAlertsTest extends  SahiTestScript {

	private DashboardRecentAlertsTasks ra = null;
	
	@BeforeSuite
	public void setUp() {
		ra = new DashboardRecentAlertsTasks(sahiTasks);
	}
	
	@BeforeMethod
	public void navigateToSummaryDefaultView() {
	     ra.navigateToDashboardDefaultView();
	}
	
	@Test
	public void verifyDashboardRecentAlertsPortlet() {
		Assert.assertTrue(ra.addRecentAlertsPortlet(), "Add Recent Alerts Portlet!");
		Assert.assertTrue(ra.recentAlertsNameFilter(), "Name Search!");
	}
	
	@Test
	public void verifyDashboardRecentEventsPortlet() {
		Assert.assertTrue(ra.addRecentEventsPortlet(), "Add Recent Events Portlet!");
		Assert.assertTrue(ra.recentEventResourceFilter(), "Resource Search!");
	}
}
