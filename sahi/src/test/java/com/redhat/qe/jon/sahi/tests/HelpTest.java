package com.redhat.qe.jon.sahi.tests;


import org.testng.annotations.Test;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class HelpTest  extends SahiTestScript  {
	
	
	@Test (groups="HelpTestJON")
	public void helpAboutTest() {
		   
		sahiTasks.helpAbout();
		 
	}
	
	@Test (groups="HelpTestJON")
	public void helpFAQTest() {
		        		
		sahiTasks.helpFAQ();
		
		
	}
	
	@Test (groups="HelpTestJON")
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
	
	@Test (groups="HelpTestJON")
	public void helpHowToGroupDefinitions() {
		
		sahiTasks.helpHowToGroupDefinitions();
		        		
	}
	
	@Test (groups="HelpTestJON")
	public void helpHowToSearchBar() {
		
		sahiTasks.helpHowToSearchBar();
		        		
	}
	
	@Test (groups="HelpTestJON")
	public void helpCollapseExpandProduct() {
		
		sahiTasks.collapseExpandProduct();
		        		
	}
	
	@Test (groups="HelpTestJON")
	public void helpCollapseExpandDocumentation() {
		
		sahiTasks.collapseExpandDocumentation();
		        		
	}
	
	@Test (groups="HelpTest")
	public void helpCollapseExpandTutorial() {
		
		sahiTasks.collapseExpandTutorial();
		        		
	}

}
