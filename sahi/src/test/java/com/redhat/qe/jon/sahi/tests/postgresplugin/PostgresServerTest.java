package com.redhat.qe.jon.sahi.tests.postgresplugin;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginHistory;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginOperations;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginOperations.OPERATION;

/**
 * @author mmahoney
 */

public class PostgresServerTest extends SahiTestScript {
	private PostgresPluginOperations ppo = null;
	private PostgresPluginHistory pph = null;
	
	@BeforeSuite
	public void setUp() {
		ppo = new PostgresPluginOperations(sahiTasks);
		pph = new PostgresPluginHistory(sahiTasks);
	}
	
	@BeforeTest
	public void navigateToPostgresPluginBase() {
	     ppo.navigateToPostgresBase();
	}
	
	@Test
	public void postgresServerScheduleOperation() {
		OPERATION operation = OPERATION.LIST_PROCESS_STATISTICS;

		// Navigate to Operations Tab
		ppo.navigateToOptionsTabSchedulesPage();
	    
	    // Create Schedule Operation
		ppo.createScheduleNow(operation);
		
		// Validate Scheduled Operation (Success / Failure)
		Assert.assertTrue(pph.wasServerScheduledOperationSuccess(operation), "Scheduled Database Server Operation!");
	}
}
