package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.redhat.qe.auto.testng.TestNGUtils;

import com.redhat.qe.Assert;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

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
	
	@Test (groups="driftDefinitionTemplate")
	public void editDriftDefinitionTemplate( ){
		sahiTasks.editDriftDefinitionTemplate(testDriftDefTmpName);
		
	}
	
	@Test (groups="driftDefinitionTemplate")
	public void deleteDriftDefinitionTemplate( ){
		sahiTasks.deleteDriftDefinitionTemplate(testDriftDefTmpName);
		
	}
}
