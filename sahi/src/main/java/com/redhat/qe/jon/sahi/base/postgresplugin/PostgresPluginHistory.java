package com.redhat.qe.jon.sahi.base.postgresplugin;

import com.redhat.qe.jon.sahi.base.postgresplugin.History;
import com.redhat.qe.jon.sahi.base.postgresplugin.PostgresPluginOperations.OPERATION;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

public class PostgresPluginHistory extends PostgresPluginBase {

	private SahiTasks tasks = null;
	public static History historyReference = null;

	public PostgresPluginHistory(SahiTasks sahiTasks) {
		super(sahiTasks);
		tasks = sahiTasks;
	}

	public boolean wasDatabaseScheduledOperationSuccess(OPERATION op) {
		return wasPostgresScheduledOperationSuccess(op, History.historyTableColumnsDatabase);
	}

	public boolean wasDatabasesScheduledOperationSuccess(OPERATION op) {
		return wasPostgresScheduledOperationSuccess(op, History.historyTableColumnsDatabases);
	}
	public boolean wasServerScheduledOperationSuccess(OPERATION op) {
		return wasPostgresScheduledOperationSuccess(op, History.historyTableColumnsServer);
	}

	public void updateOperationHistoryReferenceForDatabase(){
		updateOperationHistoryReference(History.historyTableColumnsDatabase);
	}
	
	public void updateOperationHistoryReferenceForDatabases(){
		updateOperationHistoryReference(History.historyTableColumnsDatabases);
	}
	
	public void updateOperationHistoryReferenceForServer(){
		updateOperationHistoryReference(History.historyTableColumnsServer);
	}
	
	private void updateOperationHistoryReference(String columns){
		historyReference = getHistoryByRow(0, columns);
		
		// If there is no rows in history then null is returned
		if(historyReference != null){
			if((historyReference.getDateSubmitted() == null) && (historyReference.getStartedTime() ==null) ){ //If it is not null either one should be available.
				historyReference = null;
			}
		}
		_logger.info("History Table Reference Updated: "+historyReference);
	}

	private boolean wasPostgresScheduledOperationSuccess(OPERATION op, String columns) {
		String operation = op.get().toString();
		History row = null;
		
		_logger.info("Operation: " + operation);
		tasks.waitForElementVisible(tasks, tasks.cell("/" + operation + "/" ), "Operations Tab Visible.", Timing.TIME_5S);

		long timeOut = System.currentTimeMillis() + (Timing.TIME_1M * 3);
		boolean isComplete = false;
		
		do {
			tasks.cell("Refresh").near(tasks.cell("New Schedule")).click();
			
			row = getPostgresOperationHistoryByRow(0, columns);

			if(historyReference != null){
				if(columns.equals(History.historyTableColumnsDatabase) || columns.equals(History.historyTableColumnsServer)){
					if(! historyReference.getDateSubmitted().equals(row.getDateSubmitted())) {
						isComplete = true;
					}
				}else if(columns.equals(History.historyTableColumnsDatabases)){
					if(! historyReference.getStartedTime().equals(row.getStartedTime())) {
						isComplete = true;
					}
				}
			}else{
				if (row != null) {
					isComplete = true;
				}else{
					tasks.waitFor(Timing.TIME_5S);
				}
			}
			
			_logger.info("Status: " + row.getStatus());
			if(row.getStatus().equals(History.STATUS.IN_PROGRESS.toString())){
				isComplete = false;
				tasks.waitFor(Timing.TIME_5S);
			}
		} while ( (!isComplete) && (System.currentTimeMillis() < timeOut) );

		_logger.info("Status [" + row.getStatus() + "], Detailed [" + row.toString() + "]");
		
		return row.getStatus().equals(History.STATUS.SUCCESS.toString());
	}

	/**
	 * 
	 * @param rowNumber
	 * @return returns selected row from the table, if it is available
	 */
	public History getHistoryByRow(int rowNumber, String columns){
		int tableCountOffset = 0;
		return new History(columns, tasks.getRHQgwtTableRowDetails(History.tableName, tableCountOffset, columns, History.statusImageToString, rowNumber));
	}
	
	public History getPostgresOperationHistoryByRow(int rowNumber, String columns){
		int tableCountOffset = 2;
		return new History(columns, tasks.getRHQgwtTableRowDetails(History.tableName, tableCountOffset, columns, History.statusImageToString, rowNumber));
	}
}
