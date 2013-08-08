package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents condition of Event Detection type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class EventDetectionCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Event Detection";
	
	// fields
	private String eventSeverity = null;
	public static final String EVENT_SEVERITY_LABEL = "Event Severity :";
	private String regExpr = null;
	public static final String REG_EXPR_LABEL = "/Regular Expression.*/";
	
	private Editor editor = null;
	
	public EventDetectionCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
		editor = new Editor(tasks);
	}

	public EventDetectionCondition setEventSeverity(String guiVisibleText){
		addField(EVENT_SEVERITY_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		eventSeverity = guiVisibleText;
		return this;
	}
	
	public String getEventSeverity(){
		return this.eventSeverity;
	}
	
	public EventDetectionCondition setRegularExpression(String guiVisibleText){
		addField(REG_EXPR_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.TEXT);
		regExpr = guiVisibleText;
		return this;
	}
	
	public String getRegularExpression(){
		return this.regExpr;
	}
	
	public void parseValuesFromPage(){
		eventSeverity = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, EVENT_SEVERITY_LABEL);
		regExpr = editor.getTextNearCell(AlertDefinitionEditor.TEXT_FIELD_SELECTOR, REG_EXPR_LABEL);
	}
}
