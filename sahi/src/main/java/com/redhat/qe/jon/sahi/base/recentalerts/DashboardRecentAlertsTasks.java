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
		tasks.waitForElementVisible(tasks, tasks.link("Dashboard"), "Dashboard.", Timing.TIME_5S);
		tasks.link("Dashboard").click();
		tasks.waitForElementVisible(tasks, tasks.cell("Default"), "Default", Timing.TIME_10S);
		tasks.cell("Default").click();
	}
	
	public boolean addRecentAlertsPortlet() {
		
		// Add the Recent Alerts Portlet if it does not already exist
		if (!tasks.cell("Recent Alerts").exists()) {
				
			this.putIntoEditMode("New Dashboard");
			tasks.waitForElementVisible(tasks, tasks.cell("Add Portlet"), "Add Portlet.", Timing.TIME_5S);

			selectMenu("Add Portlet", "Recent Alerts");
			Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.cell("Recent Alerts"), "Recent Alerts.", Timing.TIME_10S));
		}
		
		return true;
	}
}
