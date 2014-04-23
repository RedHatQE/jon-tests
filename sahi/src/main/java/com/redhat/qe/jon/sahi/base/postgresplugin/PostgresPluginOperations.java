package com.redhat.qe.jon.sahi.base.postgresplugin;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

public class PostgresPluginOperations extends PostgresPluginBase {

	private SahiTasks tasks = null;

	private final String UniqueIdentifier = "Unique ID: " + System.currentTimeMillis();
	private final String SQLToInvoke = "select * from RHQ_SERVER";

	public enum ScheduleUsing {
		NOW ("Now"),
		NOW_AND_REPEAT ("Now & Repeat"),
		LATER ("Later"),
		LATER_AND_REPEAT ("Later & Repeat");

		private String scheduleType = null;

		ScheduleUsing(String value){
			this.scheduleType=value;
		}

		public String get(){
			return this.scheduleType;
		}
	}

	public enum OPERATION {
		INVOKE_SQL ("Invoke Sql"),
		LIST_PROCESS_STATISTICS ("List Process Statistics"),
		VACUUM ("Vacuum");

		private String operationType = null;

		OPERATION(String value){
			this.operationType=value;
		}		
		public String get(){
			return this.operationType;
		}
	}

	public PostgresPluginOperations(SahiTasks sahiTasks) {
		super(sahiTasks);
		tasks = sahiTasks;
	}
	public void navigateToOptionsTabSchedulesPage() {
		navigateToOptionsTab(true);
	}

	public void navigateToOptionsTabHistoryPage() {
		navigateToOptionsTab(false);
	}

	private void navigateToOptionsTab(boolean isSchedulePage) {
		tasks.cell("Operations").click();
		if(isSchedulePage){
			tasks.cell("Schedules").near(tasks.cell("History")).click();
		}else{
			tasks.cell("History").near(tasks.cell("Schedules")).click();
		}
	}

	public boolean createScheduleNow(OPERATION operation) {
		return createSchedule(operation, ScheduleUsing.NOW);
	}

	private boolean createSchedule(OPERATION op, ScheduleUsing schedule) {
		String scheduleFrequency = schedule.get();
		String operation = op.get().toString();

		if (!scheduleFrequency.equals(ScheduleUsing.NOW.get().toString())) {
			_logger.warning("Schedule type ["+ schedule.get().toString() +"] not supported at this time!");
			return false;
		}

		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.cell("New"), "New", Timing.TIME_5S));
		tasks.cell("New").click();
		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.bold("Operation * :"), "Bold: Operation * :", Timing.TIME_5S));
		tasks.div("selectItemText").near(tasks.bold("Operation * :")).click();
		tasks.byText(operation, "nobr").click();

		if (operation.equals(OPERATION.INVOKE_SQL.get().toString())) {
			tasks.textbox("sql").setValue(SQLToInvoke);
			tasks.radio("").near(tasks.label("query")).click();
		}

		tasks.radio("").near(tasks.label(scheduleFrequency)).click();
		tasks.textarea("description").setValue(UniqueIdentifier);
		tasks.cell("Schedule").near(tasks.cell("Reset")).click();

		return true;
	}
}
