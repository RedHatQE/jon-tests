package com.redhat.qe.jon.sahi.base.postgresplugin;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.ExtendedSahi;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

abstract class PostgresPluginBase {
	SahiTasks tasks = null;
	static Logger _logger = Logger.getLogger(ExtendedSahi.class.getName());

	PostgresPluginBase(SahiTasks sahiTasks) {
		tasks = sahiTasks;
	}
	
	public void navigateToPostgresBase() {
	    Resource agent = new Resource(tasks,System.getProperty("jon.agent.name"),"Postgres");
	    agent.navigate();
	    tasks.waitForElementVisible(tasks, tasks.cell("/Databases/"), "Databases Visible",Timing.TIME_5S);
	}
	
	public void navigateToDatabase(PostgresPluginDefinitions definitions) {
		tasks.cell("Databases").click();
		tasks.waitForElementVisible(tasks, tasks.cell(definitions.getDatabaseName()), "Database [" + definitions.getDatabaseName() + "] not visible.", Timing.TIME_10S);
		tasks.cell(definitions.getDatabaseName()).click();
	}
	
	public void navigateToDatabaseTable(PostgresPluginDefinitions definitions) {
		navigateToDatabase(definitions);
		tasks.cell("Tables").click();
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
}
