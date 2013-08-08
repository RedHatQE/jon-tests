package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents condition of Operation Execution type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class OperationExecutionCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Operation Execution";
	
	// fields
	private String value = null;
	public static final String VALUE_LABEL = "Value :";
	private String status = null;
	public static final String STATUS_LABEL = "Status :";
	
	
	public OperationExecutionCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
	}
	
	public OperationExecutionCondition setValue(String guiVisibleText){
		addField(VALUE_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		value = guiVisibleText;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	
	public OperationExecutionCondition setStatus(String guiVisibleText){
		addField(STATUS_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		status = guiVisibleText;
		return this;
	}
	
	public String getStatus() {
		return status;
	}

	@Override
	public void parseValuesFromPage() {
		value = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, VALUE_LABEL);
		status = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, STATUS_LABEL);
		
	}

	
}
