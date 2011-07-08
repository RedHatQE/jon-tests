package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class RecentOperationsTest extends SahiTestScript{

	@Test (groups="recentOperations")
	public void createRecentOperationsSchedule(){		
		sahiTasks.createRecentOperationsSchedule();
	}
	@Test (groups="recentOperations", dependsOnMethods={"createRecentOperationsSchedule"})
	public void deleteRecentOperationsSchedule(){		
		sahiTasks.deleteRecentOperationsSchedule();
	}
	@Test (groups="recentOperations")
	public void recentOperationsForceDelete(){
		sahiTasks.recentOperationsForceDelete();
	}
	@Test (groups="recentOperations")
	public void recentOperationsWithRefreshButtonFunctionality(){		
		sahiTasks.opreationsWithRefreshButtonFunctionality();
	}
	@Test (groups="recentOperations")
	public void recentOperationsQuickLinks(){		
		sahiTasks.recentOperationsQuickLinks();
		
	}
	
}
	
