package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class SchedulingForResourcesTest extends SahiTestScript {
	
	
	@ Test (groups= "scheduleResources")
	public void enableScheduleforResource(){
		sahiTasks.scheduleEnableForResource();
		
	}
	
	@ Test (groups= "scheduleResources")
	public void disableScheduleforResource(){
		sahiTasks.disableScheduleResource();
		
	}
	
	@ Test (groups= "scheduleResources")
	public void refreshScheduleforResource(){
		sahiTasks.refreshScheduledResource();
		
	}
	@ Test (groups= "scheduleResources")
	public void scheduleCollectionIntervalforResource(){
		sahiTasks.setCollectionIntervalForScheduledResource(2);
		
	}
	
	@ Test (groups= "scheduleResources", dependsOnMethods={"scheduleCollectionIntervalforResource"})
	
	public void rescheduleDefaultCollectionIntervalforResource(){
		sahiTasks.setCollectionIntervalForScheduledResource(10);
		
	}

	@AfterClass
	public void changingToDefaScheduleforResource(){
		sahiTasks.scheduleEnableForResource();
		
	}
	
	
	

}
