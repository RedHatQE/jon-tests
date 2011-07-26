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
	
	
	
	@Test (groups="ReportsTest")
	public void suspectMetricsReportTest() {
		
       sahiTasks.suspectMetrics();        		
	}
	
	@Test (groups="ReportsTest")
	public void configurationHistoryReportTest() {
		
       sahiTasks.configurationHistory();        		
	}
	
	@Test (groups="ReportsTest")
	public void recentOperationsReportTest() {
		
       sahiTasks.recentOperations();       		
	}
	
	@Test (groups="ReportsTest")
	public void recentAlertsReportTest() {
		
       sahiTasks.recentAlerts();        		
	}
	
	@Test (groups="ReportsTest")
	public void alertDefinitionsReportTest() {
		
       sahiTasks.alertDefinitions();        		
	}
	
	/* todo: test fails intermittently.
	@Test (groups="ReportsTest")
	public void expandAndCollapseSectionsReportTest() {
		
       sahiTasks.expandCollapseSections();            		
	}
	*/
	
}
