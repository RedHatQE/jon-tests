package com.redhat.qe.jon.sahi.base.recentalerts;

import org.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

public class DashboardRecentAlertsTasks extends RecentAlertsBase {
	
	public DashboardRecentAlertsTasks(SahiTasks sahiTasks) {
		super(sahiTasks);
		tasks = sahiTasks;
	}
	
	public void navigateToDashboardDefaultView() {
		tasks.waitForElementVisible(tasks, tasks.link("Inventory"), "Inventory.", Timing.TIME_5S);
		tasks.link("Inventory").click();
		tasks.waitForElementVisible(tasks, tasks.link("Dashboard"), "Dashboard.", Timing.TIME_5S);
		tasks.link("Dashboard").click();
		tasks.waitForElementVisible(tasks, tasks.cell("Default"), "Default", Timing.TIME_10S);
		tasks.cell("Default").click();
	}
	
	public boolean addRecentAlertsPortlet() {
		return addPortlet("Recent Alerts");
	}
	
	public boolean addRecentEventsPortlet() {
		return addPortlet("Recent Events");
	}
	
	private boolean addPortlet(String portletName) {
		
		// Add the Portlet if it does not already exist
		if (!tasks.cell(portletName).exists()) {
			_logger.fine("Adding portlet [" + portletName + "]");

			this.putIntoEditMode("New Dashboard");
			tasks.waitForElementVisible(tasks, tasks.cell("Add Portlet"), "Add Portlet.", Timing.TIME_5S);

			selectMenu("Add Portlet", portletName);
			Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.cell(portletName), portletName, Timing.TIME_5S));
		}
		
		return true;
	}
	
}
