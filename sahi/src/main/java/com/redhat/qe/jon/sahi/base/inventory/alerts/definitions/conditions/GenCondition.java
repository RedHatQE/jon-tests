package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions;


import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents generic condition. You can define which fields 
 * this condition contains.
 * 
 * @author fbrychta
 *
 */
public class GenCondition extends Condition {

	public GenCondition(String type, SahiTasks tasks) {
		super(type, tasks);
	}
	
	
	public void parseValuesFromPage(){
	// TODO
	}

}
