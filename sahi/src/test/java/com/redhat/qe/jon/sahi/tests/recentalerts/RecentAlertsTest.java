package com.redhat.qe.jon.sahi.tests.recentalerts;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
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
	
	@BeforeTest
	public void navigateToSummaryDefaultView() {
	     ra.navigateToDashboardDefaultView();
	}
	
	@Test
	public void verifyDashboardRecentAlertsPortlet() {
		Assert.assertTrue(ra.addRecentAlertsPortlet(), "Add Recent Alerts Portlet!");
		Assert.assertTrue(ra.recentAlertsNameSearch(), "Name Search!");
	}
}
