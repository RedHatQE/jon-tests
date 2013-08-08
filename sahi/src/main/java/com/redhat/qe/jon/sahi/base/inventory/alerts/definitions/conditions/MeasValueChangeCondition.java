package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;


/**
 * This class represents condition of Measurement Value Change type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class MeasValueChangeCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Measurement Value Change";
	
	// fields
	private String metric = null;
	public static final String METRIC_LABEL = "Metric :";
	
	public MeasValueChangeCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
	}
	
	public void setMetric(String guiVisibleText){
		addField(METRIC_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		metric = guiVisibleText;
	}

	public String getMetric(){
		return this.metric;
	}
	
	@Override
	public void parseValuesFromPage() {
		metric = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, METRIC_LABEL);
		
	}
}
