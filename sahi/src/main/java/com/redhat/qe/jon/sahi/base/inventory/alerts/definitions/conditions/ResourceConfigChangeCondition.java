package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents condition of Resource Configuration Change type.
 * Setters methods define which fields in GUI dialog will be filled 
 * during creation of condition. This class can used for parsing values from 
 * GUI dialog as well.
 * 
 * @author fbrychta
 *
 */
public class ResourceConfigChangeCondition extends Condition {
	/**
	 * GUI visible condition type name.
	 */
	public static final String TYPE_NAME="Resource Configuration Change";
	
	public ResourceConfigChangeCondition(SahiTasks tasks) {
		super(TYPE_NAME,tasks);
	}

	@Override
	public void parseValuesFromPage() {
		// TODO Auto-generated method stub
		
	}
}
