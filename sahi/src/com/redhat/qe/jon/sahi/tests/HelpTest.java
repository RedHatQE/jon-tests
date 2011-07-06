package com.redhat.qe.jon.sahi.tests;


import org.testng.annotations.Test;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class HelpTest  extends SahiTestScript  {
	
	
	@Test (groups="HelpTest")
	public void helpAboutTest() {
		   
		sahiTasks.helpAbout();
		 
	}
	
	@Test (groups="HelpTest")
	public void helpFAQTest() {
		        		
		sahiTasks.helpFAQ();
		
		
	}
	
	@Test (groups="HelpTest")
	public void helpDocumentation() {
		  
		sahiTasks.helpDocumentation();
		
	}
	
	@Test (groups="HelpTest")
	public void helpDemoAllDemos() {
		
		sahiTasks.helpDemoAllDemos();
		
	}
	
	@Test (groups="HelpTest")
	public void helpDemosBundleProvisioning() {
		
		sahiTasks.helpDemoBundles();
		        		
	}
	
	@Test (groups="HelpTest")
	public void helpHowToGroupDefinitions() {
		
		sahiTasks.helpHowToGroupDefinitions();
		        		
	}
	
	@Test (groups="HelpTest")
	public void helpHowToSearchBar() {
		
		sahiTasks.helpHowToSearchBar();
		        		
	}

}
