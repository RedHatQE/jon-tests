package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.junit.Assert;
import org.testng.annotations.Test;





	public class PluginsTest extends SahiTestScript {
		
		//exact list of req'd plugins being provided by development 
		
		
		@Test (groups="PluginsTest")
		public void AntBundle() {
			
			sahiTasks.AntBundlePlugin();	
		}
		
		
		
		@Test (groups="PluginsTest")
		public void Augeas() {
			
			sahiTasks.AbstractAugeasPlugin();       		
		}
		
		@Test (groups="PluginsTest")
		public void ApacheHTTP() {
			
			sahiTasks.ApacheHTTPPlugin();   		
		}
		
		@Test (groups="PluginsTest")
		public void Database() {
			
			sahiTasks.AbstractDatabasePlugin();
		}
		
		@Test (groups="PluginsTest")
		public void IIS() {
			
			sahiTasks.IISPlugin();  		
		}
		
		@Test (groups="PluginsTest")
		public void JMX() {
			
			sahiTasks.GenericJMXPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void OS() {
			
			sahiTasks.OperatingSystemPlugin();   		
		}
		
		
		@Test (groups="PluginsTest")
		public void PostgreSQL() {
			
			sahiTasks.PostGreSQLPlugin();  
		}
		
		@Test (groups="PluginsTest")
		public void RHQAgent() {
			
			sahiTasks.RHQAgentPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void Script() {
			
			sahiTasks.ScriptPlugin();
		}
		
	   
		
	}
	




