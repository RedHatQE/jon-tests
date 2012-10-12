package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class ConfiurationHistoryTest extends SahiTestScript{
	

	@Test (groups= "configuration history")
	 public void navigationToConfigurationTab(){
		sahiTasks.navigationToConfigurationHistoryTab();
			
	}
	@Test (groups= "configuration history")
	public void deleteRowFromConfigurationHistory(){
		sahiTasks.deleteConfigurationFromList();
	}
	

}
