package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents condition of Availability Duration type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class AvailDurationCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Availability Duration";
	
	private String availState = null;
	public static final String AVAIL_STATE_LABEL = "Availability State :";
	private String availDuration = null;
	public static final String AVAIL_DURATION_LABEL = "Duration :";
	
	private Editor editor = null;
	
	public AvailDurationCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
		editor = new Editor(tasks);
	}
	
	public AvailDurationCondition setAvailState(String guiVisibleText){
		availState = guiVisibleText;
		addField(AVAIL_STATE_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		
		return this;
	}	
	
	public String getAvailState(){
		return this.availState;
	}
	
	public AvailDurationCondition setDuration(String durationMinutes){
		this.availDuration = durationMinutes;
		addField(AVAIL_DURATION_LABEL, durationMinutes, AlertDefinitionEditor.Field.FieldType.TEXT);
		
		return this;
	}
	
	public String getDuration(){
		return this.availDuration;
	}
	
	public void parseValuesFromPage(){
		availState = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, AVAIL_STATE_LABEL);
		availDuration = editor.getTextNearCell(AlertDefinitionEditor.TEXT_FIELD_SELECTOR, AVAIL_DURATION_LABEL);
	}

}
