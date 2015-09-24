package com.redhat.qe.jon.sahi.base.recentalerts;

import java.util.logging.Level;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

public class GroupRecentAlertsTasks extends RecentAlertsBase {
	String uniqueID = null;
	final String Description = "QE Test";
	
	public GroupRecentAlertsTasks(SahiTasks sahiTasks) {
		super(sahiTasks);
		tasks = sahiTasks;
	}
	
	public void navigateToCompatibleGroups() {
		tasks.waitForElementVisible(tasks, tasks.link("Inventory"), "Inventory.", Timing.TIME_5S);
		tasks.link("Inventory").click();
		tasks.waitForElementVisible(tasks, tasks.cell("Compatible Groups"), "Compatible Groups", Timing.TIME_10S);
		tasks.cell("Compatible Groups").click();
		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.textbox("search"), "Label Search", Timing.TIME_5S), "Label Search");
	}
	
	public void createNewCompatibleGroup() {
		uniqueID = "QETEST" + System.currentTimeMillis();
		
		String resource = System.getProperty("jon.agent.name");
		
		tasks.cell("New").near(tasks.cell("Delete")).click();
		tasks.waitForElementVisible(tasks, tasks.cell("Create Group"), "Create Group.", Timing.TIME_5S);
		tasks.textbox("name").setValue(uniqueID);
		tasks.textarea("description").setValue(Description);
		tasks.cell("Next").near(tasks.cell("Cancel")).click();
		tasks.waitForElementVisible(tasks, tasks.div(resource), "Resource not present [" + resource + "].", Timing.TIME_5S);
		tasks.div(resource).doubleClick();
		tasks.waitFor(Timing.WAIT_TIME);
		tasks.cell("Finish").near(tasks.cell("Previous")).click();
    	Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.link(uniqueID), uniqueID, Timing.TIME_5S), "Group Alert!");
    	_logger.log(Level.INFO, "Created Group Alert [" + uniqueID + "].");
	}
	
	public boolean addGroupRecentAlertsPortlet() {
		return addGroupRecentPortlet("Recent Alerts");
	}

	public boolean addGroupRecentEventsPortlet() {
		return addGroupRecentPortlet("Recent Events");
	}
	
	private boolean addGroupRecentPortlet(String portletName) {
		String groupName = uniqueID;
		
		tasks.link(groupName).click();
		
		if (!tasks.cell(portletName).exists()) {
			this.putIntoEditMode("Reset");
			selectMenu("Add Portlet", portletName);
			Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.cell(portletName), portletName, Timing.TIME_10S));
		}
		
		return true;
	}
	
}
