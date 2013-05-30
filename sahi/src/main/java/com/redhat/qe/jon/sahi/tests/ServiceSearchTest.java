package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class ServiceSearchTest extends SahiTestScript{
	@BeforeMethod(groups="inventoryTest")
	public void nap() {
		sahiTasks.waitFor(5000);
	}


	
	@Test (groups="servicesTest")
	public void checkServicesSearchByVersion(){
		sahiTasks.servicesSearchBy("version","2");
		
	}
}
