package com.redhat.qe.jon.sahi.base.postgresplugin;

import java.util.HashMap;

/**
 * This class represents postgres history table.
 * @author jkandasa
 *
 */
public class History {
	public static String tableName = "listTable";
	public static String historyTableColumnsServer    = "Date Submitted,Operation,Requestor,Status";
	public static String historyTableColumnsDatabase  = "Date Submitted,Operation,Requestor,Status";
	public static String historyTableColumnsDatabases = "Operation Name,Requester,Status,Started Time";
	public static String statusImageToString = "Operation_ok_16.png=Success,Operation_failed_16.png=Failed,Operation_inprogress_16.png=In Progress";

	public enum STATUS {
		SUCCESS ("Success"),
		FAILED ("Failed"),
		IN_PROGRESS ("In Progress");
		private String status = null;
		STATUS(String value){
			this.status=value;
		}
		public String toString(){
			return this.status;
		}
	}

	/** Constructor. 
	 *  The default behavior of this object is 
	 *  Converts HashMap<String, String> values in to History.java object
	 *  @param columns
	 *  @param HashMap<String, String>
	 */  
	public History(String columns, HashMap<String, String> map){
		super();
		for(String column : columns.split(",")){
			if(column.equals("Date Submitted")){
				this.setDateSubmitted(map.get(column));
			} else if(column.equals("Operation")){
				this.setOperation(map.get(column));
			}else if(column.equals("Requestor")){
				this.setRequester(map.get(column));
			}else if(column.equals("Status")){
				this.setStatus(map.get(column));
			}else if(column.equals("Operation Name")){
				this.setOperationName(map.get(column));
			}else if(column.equals("Started Time")){
				this.setStartedTime(map.get(column));
			}
		}
	}

	/** Default Constructor. 
	 *    
	 */  
	public History(){
		super();
	}

	private String dateSubmitted;
	private String operation;
	private String requester;
	private String status;
	private String operationName;
	private String startedTime;

	public String getDateSubmitted() {
		return dateSubmitted;
	}
	public void setDateSubmitted(String dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getRequester() {
		return requester;
	}
	public void setRequester(String requester) {
		this.requester = requester;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public String getStartedTime() {
		return startedTime;
	}
	public void setStartedTime(String startedTime) {
		this.startedTime = startedTime;
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Date Submitted: ").append(this.dateSubmitted).append(", ");
		builder.append("Operation: ").append(this.operation).append(", ");
		builder.append("Requestor: ").append(this.requester).append(", ");
		builder.append("Status: ").append(this.status).append(", ");
		builder.append("Operation Name: ").append(this.operationName).append(", ");
		builder.append("Start Time: ").append(this.startedTime);

		return builder.toString();
	}
}
