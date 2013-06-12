package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class DriftDefinitionTemplateTest extends SahiTestScript{
	@BeforeMethod(groups="inventoryTest")
	public void nap() {
		sahiTasks.waitFor(5000);
	}

	String testDriftDefTmpName = "testDriftDefTmp";
	
	@Test (groups="driftDefinitionTemplate")
	public void createDriftDefinitionTemplate( ){
		sahiTasks.createDriftDefinitionTemplate(testDriftDefTmpName);
		
	}
	
	@Test (groups="driftDefinitionTemplate", dependsOnMethods={"createDriftDefinitionTemplate"})
	public void editDriftDefinitionTemplate( ){
		sahiTasks.editDriftDefinitionTemplate(testDriftDefTmpName);
		
	}
	
	@Test (groups="driftDefinitionTemplate", dependsOnMethods={"editDriftDefinitionTemplate"})
	public void deleteDriftDefinitionTemplate( ){
		sahiTasks.deleteDriftDefinitionTemplate(testDriftDefTmpName);
		
	}
}
