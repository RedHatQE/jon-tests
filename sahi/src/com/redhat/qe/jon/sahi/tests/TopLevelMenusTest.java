package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.testng.annotations.Test;

public class TopLevelMenusTest extends SahiTestScript {
	
	@Test (groups="TopLevelMenusTest")
	public void topLevelMenusExist() {
		
       sahiTasks.topLevelMenusExist();        		
	}
	
	@Test (groups="TopLevelMenusTest")
	public void topLevelMenusAreClickable() {
		
	}
	
	
	
}   