package com.redhat.qe.jon.sahi.tests.recentalerts;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.recentalerts.GroupRecentAlertsTasks;

/**
 * @author mmahoney
 */

public class GroupRecentAlertsTest extends  SahiTestScript {

	private GroupRecentAlertsTasks ga = null;
	
	@BeforeSuite
	public void setUp() {
		ga = new GroupRecentAlertsTasks(sahiTasks);
	}
	
	@BeforeMethod
	public void navigateToCompatabilityGroups() {
		ga.navigateToCompatibleGroups();
	}
	
	@Test
	public void createCompatibilityGroupAndAddRecentAlerts() {
		ga.createNewCompatibleGroup();
		Assert.assertTrue(ga.addGroupRecentAlertsPortlet(), "Group Recent Alerts!");
		Assert.assertTrue(ga.recentAlertsNameFilter(), "Name Search!");
	}
	
	@Test
	public void createCompatibilityGroupAndAddRecentEvents() {
		ga.createNewCompatibleGroup();
		Assert.assertTrue(ga.addGroupRecentEventsPortlet(), "Group Recent Events!");
		Assert.assertTrue(ga.recentEventResourceFilter(), "Resource Search!");
	}
}
