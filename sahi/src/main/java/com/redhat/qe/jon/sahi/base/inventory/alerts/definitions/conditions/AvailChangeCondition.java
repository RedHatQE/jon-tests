package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents condition of Availability Change type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class AvailChangeCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Availability Change";
	
	// fields
	public static final String AVAIL_CHANGE_TYPE_LABEL = "Availability :";
	private String availChangeType = null;

	public AvailChangeCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
	}
	
	public void setAvailChangeType(String guiVisibleText){
		availChangeType = guiVisibleText;
		addField(AVAIL_CHANGE_TYPE_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
	}
	
	public String getAvailChangeType(){
		return this.availChangeType;
	}
	
	public void parseValuesFromPage(){
		availChangeType = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, AVAIL_CHANGE_TYPE_LABEL);
	}
}
