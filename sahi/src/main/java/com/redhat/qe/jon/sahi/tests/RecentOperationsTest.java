package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class RecentOperationsTest extends SahiTestScript{

	@Test (groups="recentOperations")
	public void createRecentOperationsSchedule(){		
		 Assert.assertTrue(sahiTasks.createRecentOperationsSchedule(), "Operation schedule status...");
	}
	@Test (groups="recentOperations", dependsOnMethods={"recentOperationsWithRefreshButtonFunctionality"})
	public void deleteRecentOperationsSchedule(){		
		 Assert.assertTrue(sahiTasks.deleteRecentOperationsSchedule(), "Operation Deletion Status");
	}
	@Test (groups="recentOperations")
	public void recentOperationsForceDelete(){
		Assert.assertTrue(sahiTasks.recentOperationsForceDelete(), "Operation Force Deletion Status");
	}
	@Test (groups="recentOperations", dependsOnMethods={"createRecentOperationsSchedule"})
	public void recentOperationsWithRefreshButtonFunctionality(){		
		Assert.assertTrue(sahiTasks.opreationsWithRefreshButtonFunctionality(), "Operation Refresh Status");
	}
	@Test (groups="recentOperations")
	public void recentOperationsQuickLinks(){		
		sahiTasks.recentOperationsQuickLinks();
		
	}
	
}
	
