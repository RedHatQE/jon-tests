package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;


/**
 * This class represents condition of Measurement Absolute Value Threshold type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class MeasAbsValTresholdCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Measurement Absolute Value Threshold";
	
	// fields
	private String metric = null;
	public static final String METRIC_LABEL = "Metric :";
	private Comparator comp = null;
	public static final String COMPARATOR_LABEL = "Comparator :";
	private String metricValue = null;
	public static final String METRIC_VALUE_LABEL = "Metric Value :";
	
	private Editor editor = null;
	
	public MeasAbsValTresholdCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
		editor = new Editor(tasks);
	}
	
	public MeasAbsValTresholdCondition setMetric(String guiVisibleText){
		addField(METRIC_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		metric = guiVisibleText;
		return this;
	}
	
	public String getMetric(){
		return this.metric;
	}
	
	public MeasAbsValTresholdCondition setComparator(Comparator comparator){
		addField(COMPARATOR_LABEL, comparator.getDisplayName(), AlertDefinitionEditor.Field.FieldType.COMBO);
		comp = comparator;
		return this;
	}
	
	public Comparator getComparator(){
		return this.comp;
	}
	
	public MeasAbsValTresholdCondition setMetricValue(String value){
		addField(METRIC_VALUE_LABEL, value, AlertDefinitionEditor.Field.FieldType.TEXT);
		metricValue = value;
		return this;
	}
	
	public String getMetricValue(){
		return this.metricValue;
	}
	
	public void parseValuesFromPage(){
		metric = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, METRIC_LABEL);
		String compStr = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, COMPARATOR_LABEL);
		comp = Comparator.fromString(compStr);
		metricValue = editor.getTextNearCell(AlertDefinitionEditor.TEXT_FIELD_SELECTOR, METRIC_VALUE_LABEL);
	}
}
