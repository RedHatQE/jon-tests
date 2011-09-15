package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class IndividualConfigTest extends SahiTestScript{
	
	public static String origianlConfigValue="1000";
	
	@Test (groups= "individualconfig")
	 public void navigationToConfigTab(){
		sahiTasks.navigationToConfiguration();
		
	}
	
	@Test (groups= "individualconfig",  dependsOnMethods= {"navigationToConfigTab"})
	 public void navigationToConfigSubTabs(){
		sahiTasks.navigationToConfigurationSubtabs();
	}
	
	@Test (groups= "individualconfig")
	 public void editAndSaveConfigTabValues(){
		sahiTasks.editAndSaveConfiguration();
	}
	@Test (groups= "individualconfig", dependsOnMethods= {"editAndSaveConfigTabValues"})
	 public void settingOriginalValue(){
		sahiTasks.settingToOriginalValues(origianlConfigValue);
	}
	/*
	@ Test (groups= "individualconfig")
	 public void editconfigTabValueAndCancel(){
		
	}*/
	
}
