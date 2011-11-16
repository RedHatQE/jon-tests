package com.redhat.qe.jon.sahi.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Nov 03, 2011
 */
public class ImportResources extends SahiTestScript{
	
	@Test (groups="importResourceTest", dataProvider="importResourceData")
	public void importResource(String resourceName) throws InterruptedException, IOException{
		sahiTasks.importResources(resourceName);
	}
	
	@DataProvider(name="importResourceData")
	public Object[][] getImportResourceData() {
		ArrayList<List<Object>> importResourceData = new ArrayList<List<Object>>();
		importResourceData.add(Arrays.asList(new Object[]{null})); //put null to import all the resources, else put resource name as a string
		return TestNGUtils.convertListOfListsTo2dArray(importResourceData);
	}
}
