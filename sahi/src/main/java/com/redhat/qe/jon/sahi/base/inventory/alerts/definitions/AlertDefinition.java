package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions;


/**
 * This class encapsulates data for one alert definition 
 * parsed from alert definitions page.
 * @author fbrychta
 *
 */
public class AlertDefinition {
	public enum Priority{Low,Medium,High}
	private String name;
	private String description = null;
	private String creationTime;
	private String modifiedTime;
	private boolean isEnabled;
	private Priority priority;
	private String parent = null;
	private String protectedField = null;
	 
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Priority getPriority() {
		return priority;
	}


	public void setPriority(Priority priority) {
		this.priority = priority;
	}


	public boolean isEnabled() {
		return isEnabled;
	}


	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getCreationTime() {
		return creationTime;
	}


	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}


	public String getModifiedTime() {
		return modifiedTime;
	}


	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}


	public String getParent() {
		return parent;
	}


	public void setParent(String parent) {
		this.parent = parent;
	}


	public String getProtectedField() {
		return protectedField;
	}


	public void setProtectedField(String protectedField) {
		this.protectedField = protectedField;
	}
}



