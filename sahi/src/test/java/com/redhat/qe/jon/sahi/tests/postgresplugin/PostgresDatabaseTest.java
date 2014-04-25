package com.redhat.qe.jon.sahi.tests.postgresplugin;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginDefinitions;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginHistory;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginOperations;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginOperations.OPERATION;

/**
 * @author mmahoney
 */

public class PostgresDatabaseTest extends SahiTestScript {

	PostgresPluginOperations ppo = null;
	PostgresPluginHistory pph = null;

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
	public void postgresDatabaseScheduleOperation() {
		OPERATION operation = OPERATION.INVOKE_SQL;
		PostgresPluginDefinitions definitions = new PostgresPluginDefinitions();

		ppo.navigateToDatabase(definitions);
		
		// Navigate to Operations Tab History Page
		ppo.navigateToOptionsTabHistoryPage();
		pph.updateOperationHistoryReferenceForDatabase();

		// Navigate to Operations Tab Schedules Page
		ppo.navigateToOptionsTabSchedulesPage();


		// Create Schedule Operation
		Assert.assertTrue(ppo.createScheduleNow(operation));

		// Validate Scheduled Operation (Success / Failure)
		Assert.assertTrue(pph.wasDatabaseScheduledOperationSuccess(operation), "Scheduled Database Operation!");
	}
}
