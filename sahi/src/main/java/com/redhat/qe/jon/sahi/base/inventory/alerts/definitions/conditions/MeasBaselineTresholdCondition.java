package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;


/**
 * This class represents condition of Measurement Baseline Threshold type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class MeasBaselineTresholdCondition extends Condition {
	public enum Baseline{Minimum,Average,Maximum}
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Measurement Baseline Threshold";
	
	// fields
	private String metric = null;
	public static final String METRIC_LABEL = "Metric :";
	private Comparator comp = null;
	public static final String COMPARATOR_LABEL = "Comparator :";
	private String  baselinePerc = null;
	public static final String BASELINE_PERC_LABEL = "Baseline Percentage :";
	private Baseline baseline = null;
	public static final String BASELINE_LABEL = "Baseline :";
	
	private Editor editor = null;
	
	public MeasBaselineTresholdCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
		editor = new Editor(tasks);
	}

	public MeasBaselineTresholdCondition setMetric(String guiVisibleText){
		addField(METRIC_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		metric = guiVisibleText;
		return this;
	}
	
	public String getMetric(){
		return this.metric;
	}
	
	public MeasBaselineTresholdCondition setComparator(Comparator comparator){
		addField(COMPARATOR_LABEL, comparator.getDisplayName(), AlertDefinitionEditor.Field.FieldType.COMBO);
		comp = comparator;
		return this;
	}
	
	public Comparator getComparator(){
		return this.comp;
	}
	
	public MeasBaselineTresholdCondition setBaselinePercentage(String percentage){
		addField(BASELINE_PERC_LABEL, percentage, AlertDefinitionEditor.Field.FieldType.TEXT);
		baselinePerc = percentage;
		return this;
	}
	
	public String getBaselinePercentage(){
		return this.baselinePerc;
	}
	
	public MeasBaselineTresholdCondition setBaseline(Baseline baseline){
		addField(BASELINE_LABEL, baseline.toString(), AlertDefinitionEditor.Field.FieldType.COMBO);
		this.baseline = baseline;
		return this;
	}
	public Baseline getBaseline(){
		return this.baseline;
	}
	

	@Override
	public void parseValuesFromPage() {
		metric = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, METRIC_LABEL);
		String compStr = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, COMPARATOR_LABEL);
		comp = Comparator.fromString(compStr);
		baselinePerc = editor.getTextNearCell(AlertDefinitionEditor.TEXT_FIELD_SELECTOR, BASELINE_PERC_LABEL);
		String baselineStr = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, BASELINE_LABEL);
		baseline = Baseline.valueOf(baselineStr);
	}
}
