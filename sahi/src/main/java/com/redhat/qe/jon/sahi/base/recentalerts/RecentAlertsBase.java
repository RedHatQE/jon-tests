package com.redhat.qe.jon.sahi.base.recentalerts;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
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

	public boolean recentAlertsNameFilter() {
		return applyFilter("Recent Alerts", "ALERT_NAME");
	}
	
	public boolean recentEventResourceFilter() {
		return applyFilter("Recent Events", "EVENT_RESOURCE");
	}
	
	private boolean applyFilter(String portletName, String filterName) {
		String waitFor = portletName + " Settings";
		
		_logger.fine("Apply filter [" + filterName + "] on portlet [" + portletName + "]");
		Assert.assertTrue(tasks.cell(portletName).exists(), portletName + "!");
		tasks.execute("_sahi._keyPress(_sahi._image('/settings/',_sahi._near(_sahi._div('"+portletName+"'))), [13,13]);");
		if (!tasks.div(waitFor).exists()) {
			_logger.fine("Attempting filter on portlet [" + portletName + "]");
			tasks.image("/settings/").near(tasks.div(portletName)).doubleClick();
		}
		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.cell(waitFor), waitFor, Timing.TIME_5S), waitFor);
		
		if (!tasks.textbox("/" + filterName + "/").exists()) {
			tasks.cell("Cancel").near(tasks.cell("Save")).click(); // Close the Settings dialog
			_logger.warning("Filter not available [" + filterName + "]!");
			return false;
		}
		
		tasks.textbox("/" + filterName + "/").setValue("abc");
		tasks.cell("Save").click();

        return tasks.waitForElementVisible(tasks, tasks.cell("/No results found using specified criteria/").near(tasks.div(portletName)), "Text", Timing.TIME_10S);
	}
}