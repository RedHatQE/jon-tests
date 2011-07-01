package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class CreateDeleteRoleTest  extends SahiTestScript  {


	@Test (groups="CreateDeleteRoleTest")
	public void createDeleteUserTest() {
		
       sahiTasks.createDeleteRole();        		
	}
	
}
