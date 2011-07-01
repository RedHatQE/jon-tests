package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class CreateDeleteUserTest  extends SahiTestScript  {


	@Test (groups="CreateDeleteUserTest")
	public void createDeleteUserTest() {
		
       sahiTasks.createDeleteUser();        		
	}
	
}
