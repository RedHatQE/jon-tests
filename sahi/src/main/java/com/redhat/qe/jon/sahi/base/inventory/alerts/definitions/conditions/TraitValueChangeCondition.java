package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;


/**
 * This class represents condition of Trait Value Change type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class TraitValueChangeCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Trait Value Change";
	
	// fields
	private String trait = null;
	public static final String TRAIT_LABEL = "Trait :";
	private String regExpr = null;
	public static final String REG_EXPR_LABEL = "/Regular Expression.*/";
	
	private Editor editor = null;
	
	
	public TraitValueChangeCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
		editor = new Editor(tasks);
	}
	
	
	public TraitValueChangeCondition setTrait(String guiVisibleText){
		addField(TRAIT_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.COMBO);
		trait = guiVisibleText;
		return this;
	}
	
	public String getTrait(){
		return trait;
	}
	
	public TraitValueChangeCondition setRegularExpression(String guiVisibleText){
		addField(REG_EXPR_LABEL, guiVisibleText, AlertDefinitionEditor.Field.FieldType.TEXT);
		regExpr = guiVisibleText;
		return this;
	}
	
	public String getRegularExpression(){
		return regExpr;
	}

	@Override
	public void parseValuesFromPage() {
		trait = tasks.getSelectedTextFromComboNearCell(tasks, 
				AlertDefinitionEditor.COMBOBOX_SELECTOR, TRAIT_LABEL);
		regExpr = editor.getTextNearCell(AlertDefinitionEditor.TEXT_FIELD_SELECTOR, REG_EXPR_LABEL);
		
	}
}
