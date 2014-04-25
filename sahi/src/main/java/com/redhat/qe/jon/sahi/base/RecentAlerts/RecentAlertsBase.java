package com.redhat.qe.jon.sahi.base.RecentAlerts;

import java.util.logging.Logger;

import org.testng.Assert;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.ExtendedSahi;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

abstract class RecentAlertsBase {
	SahiTasks tasks = null;
	static Logger _logger = Logger.getLogger(ExtendedSahi.class.getName());
	
	RecentAlertsBase(SahiTasks sahiTasks) {
		tasks = sahiTasks;
	}
	
	public void selectMenu(String menu, String item) {
		tasks.cell(menu).click();
		tasks.waitFor(Timing.WAIT_TIME);
		for (ElementStub es : tasks.table("menuTable").collectSimilar()) {
		    if (es.isVisible() &&  tasks.cell(item).isVisible()) {
		    	tasks.xy(tasks.cell(item).in(es), 3, 3).click();
		    }
		}
	}
	
	public void putIntoEditMode(String near) {
		if (tasks.cell("Edit Mode").near(tasks.cell(near)).exists()) {
			tasks.cell("Edit Mode").near(tasks.cell(near)).click();
			tasks.waitForElementVisible(tasks, tasks.cell("View Mode").near(tasks.cell(near)), "View Mode.", Timing.TIME_5S);
		}
	}

	public boolean recentAlertsNameSearch() {
		Assert.assertTrue(tasks.cell("Recent Alerts").exists(), "Recent Alerts");
		tasks.image("/settings/").near(tasks.div("Recent Alerts")).click();
		tasks.waitForElementVisible(tasks, tasks.cell("Recent Alerts Settings"), "Recent Alerts Setting", Timing.TIME_10S);
		tasks.textbox("/ALERT_NAME/").setValue("abc");
		tasks.cell("Save").click();

        return tasks.waitForElementVisible(tasks, tasks.cell("/No results found using specified criteria/").near(tasks.div("Recent Alerts")), "Text", Timing.TIME_10S);
	}
}