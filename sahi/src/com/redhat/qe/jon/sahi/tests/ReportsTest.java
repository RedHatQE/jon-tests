package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class ReportsTest  extends SahiTestScript  {

	@Test (groups="ReportsTest")
	public void inventoryReportTest() {
		
       sahiTasks.inventoryReport();        		
	}
	
	@Test (groups="ReportsTest")
	public void platformUtilizationReportTest() {
		
       sahiTasks.platformUtilization();        		
	}
	
	
}
