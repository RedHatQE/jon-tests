package com.redhat.qe.jon.sahi.tests.postgresplugin;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginDefinitions;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginHistory;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginInventory;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginOperations;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginOperations.OPERATION;

/**
 * @author mmahoney
 */

public class PostgresDatabaseTableTest extends SahiTestScript {
	PostgresPluginOperations ppo = null;
	PostgresPluginHistory pph = null;
	PostgresPluginInventory ppi = null;
	
	@BeforeSuite
	public void setUp() {
		ppo = new PostgresPluginOperations(sahiTasks);
		pph = new PostgresPluginHistory(sahiTasks);
		ppi = new PostgresPluginInventory(sahiTasks);
	}
	
	@BeforeTest
	public void navigateToPostgresPluginBase() {
	     ppo.navigateToPostgresBase();
	}
	
	@Test
	public void postgresTableScheduleOperation() {
		PostgresPluginDefinitions definitions = new PostgresPluginDefinitions();
		
		ppo.navigateToDatabaseTable(definitions);
		
		// Navigate to Operations Tab
		ppo.navigateToOptionsTabHistoryPage();
		
		//Get last row reference of history
		pph.updateOperationHistoryReferenceForDatabases();
		
		//Navigate to Operations "Schedules" page
		ppo.navigateToOptionsTabSchedulesPage();
		
	    // Create Schedule Operation
		Assert.assertTrue(ppo.createScheduleNow(OPERATION.VACUUM));
		
		// Validate Scheduled Operation (Success / Failure)
		Assert.assertTrue(pph.wasDatabasesScheduledOperationSuccess(OPERATION.VACUUM), "Scheduled Database Table Operation!");
	}
	
	@Test
	public void postgresTableCreateChildTable() {
		PostgresPluginDefinitions definitions = new PostgresPluginDefinitions();
		
		// Navigate to Inventory Tab
		ppi.navigateToDatabase(definitions);
		ppi.navigateToInventoryTab();
		
		// Create Child Table
		ppi.createDatabaseChildTable();
		
		Assert.assertTrue(ppi.wasCreateDatabaseChildTableSuccess(), "Table Resource!");
	}
}
