package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

import java.util.ArrayList;

/**
 * This class represents a condition which is used during creation of alert definition.
 *
 * This class is generic so you can use it to define all possible conditions.
 * Each Field contained in this class defines a form field which
 * is filled by Sahi during the creation of condition.
 *
 * Example: To define Availability Change condition
 *
 * <code>
 * Condition cond = new GenCondition("Availability Change");
 * cond.addField("Availability :","Goes down",AlertDefinitionCreator.Field.FieldType.COMBO);
 * </code>
 *
 * You can see that a label next to the combo box was used as a near cell locator.
 * @see com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionEditor.Field
 *
 */
public abstract class Condition {

	public enum Comparator{
		LESS_THEN("< (Less than)"),
		EQUAL("= (Equal to)"),
		GREATHER_THEN("> (Greater Than)");
		
		
		private final String displayName;

		Comparator(String displayName) {
	        this.displayName = displayName;
	    }
		public String getDisplayName() {
	        return displayName;
	    }
		public static Comparator fromString(String text) {
		    if (text != null) {
		      for (Comparator b : Comparator.values()) {
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
	protected final SahiTasks tasks;
	
	/**
	 * GUI visible condition type name.
	 */
	private final String type;
	protected ArrayList<AlertDefinitionEditor.Field> fields = new ArrayList<AlertDefinitionEditor.Field>();
	/**
	 * Defines values for "Fire Alert when:" combo box.
	 */
	public enum Operator{ANY,ALL}
	
	/**
	 * Creates a new instance with defined type.
	 * @param type value from the "Condition type: " combo box
	 */
	public Condition(String type,SahiTasks tasks){
		this.type = type;
		this.tasks = tasks;
	}
	
	public String getType(){
		return this.type;
	}
	
	public abstract void parseValuesFromPage();
	
	public ArrayList<AlertDefinitionEditor.Field> getFields(){
		return this.fields;
	}
	
	/**
	 * Adds field which is part of this condition.
	 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
	 * @param value value which will be set
	 * @param fieldType defines type of the field (COMBO, TEXT,...)
	 * @return this object
	 */
	public Condition addField(String nearLocator,String value,AlertDefinitionEditor.Field.FieldType fieldType){
		fields.add(new AlertDefinitionEditor.Field(nearLocator,value,fieldType));
		
		return this;
	}
	/**
	 * Adds field which is part of this condition.
	 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
	 * @param value value which will be set
	 * @param fieldType defines type of the field (COMBO, TEXT,...)
	 * @param fieldLocator if you want to provide your own filed locator (i.e. id of the field)
	 * @return this object
	 */
	public Condition addField(String nearLocator,String value,AlertDefinitionEditor.Field.FieldType fieldType,String fieldLocator){
		fields.add(new AlertDefinitionEditor.Field(nearLocator,value,fieldType,fieldLocator));
		
		return this;
	}
	/**
	 * Adds field which is part of this condition.
	 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
	 * @param fieldType defines type of the field (COMBO, TEXT,...)
	 * @param checked used for RADIO and CHECKBOX
	 * @return this object
	 */
	public Condition addField(String nearLocator,AlertDefinitionEditor.Field.FieldType fieldType, boolean checked){
		fields.add(new AlertDefinitionEditor.Field(nearLocator,fieldType,checked));
		
		return this;
	}
	/**
	 * Adds field which is part of this condition.
	 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
	 * @param fieldType defines type of the field (COMBO, TEXT,...)
	 * @param checked used for RADIO and CHECKBOX
	 * @param fieldLocator if you want to provide your own filed locator (i.e. id of the field)
	 * @return this object
	 */
	public Condition addField(String nearLocator,AlertDefinitionEditor.Field.FieldType fieldType, boolean checked,String fieldLocator){
		fields.add(new AlertDefinitionEditor.Field(nearLocator,fieldType,checked,fieldLocator));
		
		return this;
	}
}
