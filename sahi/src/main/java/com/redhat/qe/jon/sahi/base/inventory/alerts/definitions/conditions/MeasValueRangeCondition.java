package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents condition of Measurement Value Range type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * @author fbrychta
 *
 */
public class MeasValueRangeCondition extends Condition {
	
	public enum RangeComparator{
		IN_EXCLUSIVE("Inside, exclusive"),
		IN_INCLUSIVE("Inside, inclusive"),
		OUT_EXCLUSIVE("Outside, exclusive"),
		OUT_INCLUSIVE("Outside, exclusive");
		
		private final String displayName;

		RangeComparator(String displayName) {
	        this.displayName = displayName;
	    }
		public String getDisplayName() {
	        return displayName;
	    }
		public static RangeComparator fromString(String text) {
		    if (text != null) {
		      for (RangeComparator b : RangeComparator.values()) {
		        if (text.equalsIgnoreCase(b.displayName)) {
		          return b;
		        }
		      }
		    }
		    throw new IllegalArgumentException("No constant with text " + text + " found");
		}
	    @Override
	    public String toString() {
	        return this.displayName;
	    }
	}
	
	
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Measurement Value Range";
	
	// fields
	private String metric = null;
	public static final String METRIC_LABEL = "Metric :";
	private RangeComparator comp = null;
	public static final String COMPARATOR_LABEL = "Comparator :";
	private String lowValue = null;
	public static final String LOW_VALUE_LABEL = "Low Value :";
	private String highValue = null;
	public static final String HIGH_VALUE_LABEL = "High Value :";
	
	private Editor editor = null;

	
	public MeasValueRangeCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
		editor = new Editor(tasks);
	}
	
	public MeasValueRangeCondition setMetric(String guiVisibleText){
		addField(METRIC_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		metric = guiVisibleText;
		return this;
	}
	
	public String getMetric(){
		return this.metric;
	}
	
	public MeasValueRangeCondition setComparator(RangeComparator comparator){
		addField(COMPARATOR_LABEL, comparator.getDisplayName(), AlertDefinitionEditor.Field.FieldType.COMBO);
		comp = comparator;
		return this;
	}
	
	public RangeComparator getComparator(){
		return this.comp;
	}
	
	public MeasValueRangeCondition setLowValue(String lowValue){
		addField(LOW_VALUE_LABEL, lowValue, AlertDefinitionEditor.Field.FieldType.TEXT);
		this.lowValue = lowValue;
		return this;
	}
	
	public String getLowValue(){
		return this.lowValue;
	}
	
	public MeasValueRangeCondition setHighValue(String highValue){
		addField(HIGH_VALUE_LABEL, highValue, AlertDefinitionEditor.Field.FieldType.TEXT);
		this.highValue = highValue;
		return this;
	}
	
	public String getHighValue(){
		return this.highValue;
	}

	/**
	 * Parses values from GUI dialog.
	 */
	@Override
	public void parseValuesFromPage() {
		metric = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, METRIC_LABEL);
		String compStr = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, COMPARATOR_LABEL);
		comp = RangeComparator.fromString(compStr);
		lowValue = editor.getTextNearCell(AlertDefinitionEditor.TEXT_FIELD_SELECTOR, LOW_VALUE_LABEL);
		highValue = editor.getTextNearCell(AlertDefinitionEditor.TEXT_FIELD_SELECTOR, HIGH_VALUE_LABEL);
	}
}
