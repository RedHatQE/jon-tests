package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class DashboardTest  extends SahiTestScript  {
	
	
	@Test (groups="DashboardTest")
	public void viewDashboard() {
		
		sahiTasks.clickDashboardTopLevelMenu(); 
             		
	}
	
	@Test (groups="DashboardTest")
	public void editDashboard() {
		
		sahiTasks.editDashboard();
             		
	}
	
	@Test (groups="DashboardTest")
	public void newDashboard() {
		//todo
             		
	}
	
	

}
