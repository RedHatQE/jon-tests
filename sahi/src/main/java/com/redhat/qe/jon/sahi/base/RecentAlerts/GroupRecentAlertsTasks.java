package com.redhat.qe.jon.sahi.base.RecentAlerts;

import java.util.logging.Level;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

public class GroupRecentAlertsTasks extends RecentAlertsBase {
	final String UniqueID = "QETEST" + System.currentTimeMillis();
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
		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.label("Search"), "Label Search", Timing.TIME_5S), "Label Search");
	}
	
	public void createNewCompatibleGroup() {
		String resource = System.getProperty("jon.agent.name");
		
		tasks.cell("New").near(tasks.cell("Delete")).click();
		tasks.waitForElementVisible(tasks, tasks.cell("Create Group"), "Create Group.", Timing.TIME_5S);
		tasks.textbox("name").setValue(UniqueID);
		tasks.textarea("description").setValue(Description);
		tasks.cell("Next").near(tasks.cell("Cancel")).click();
		tasks.waitForElementVisible(tasks, tasks.div(resource), "Resource not present [" + resource + "].", Timing.TIME_5S);
		for (int x = 1; x <= 2; x++) {
			tasks.div(resource).click();
		}
		
		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.image("right.png"), "Right Arrow", Timing.TIME_5S));
		tasks.image("right.png").click();
		tasks.cell("Finish").near(tasks.cell("Previous")).click();
    	Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.link(UniqueID), UniqueID, Timing.TIME_5S), "Group Alert!");
    	_logger.log(Level.INFO, "Created Group Alert [" + UniqueID + "].");
	}
	
	public boolean addGroupRecentAlertsPortlet() {
		String groupName = UniqueID;
		
		tasks.link(groupName).click();
		
		if (!tasks.cell("Recent Alerts").exists()) {
			this.putIntoEditMode("Reset");
			selectMenu("Add Portlet", "Recent Alerts");
			Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.cell("Recent Alerts"), "Recent Alerts.", Timing.TIME_10S));
		}
		
		return true;
	}

}
