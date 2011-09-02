package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.junit.Assert;
import org.testng.annotations.Test;





	public class PluginsTest extends SahiTestScript {
		
		//exact list of req'd plugins being provided by development 
		
		
		@Test (groups="PluginsTest")
		public void AntBundle() {
			
			Assert.assertTrue(sahiTasks.AntBundlePlugin());	
		}
		
		
		
		@Test (groups="PluginsTest")
		public void Augeas() {
			
			Assert.assertTrue(sahiTasks.AbstractAugeasPlugin());       		
		}
		
		@Test (groups="PluginsTest")
		public void ApacheHTTP() {
			
			Assert.assertTrue(sahiTasks.ApacheHTTPPlugin());   		
		}
		
		@Test (groups="PluginsTest")
		public void Database() {
			
			Assert.assertTrue(sahiTasks.AbstractDatabasePlugin());
		}
		
		@Test (groups="PluginsTest")
		public void IIS() {
			
			Assert.assertTrue(sahiTasks.IISPlugin());  		
		}
		
		@Test (groups="PluginsTest")
		public void JMX() {
			
			Assert.assertTrue(sahiTasks.GenericJMXPlugin());
		}
		
		@Test (groups="PluginsTest")
		public void OS() {
			
			Assert.assertTrue(sahiTasks.OperatingSystemPlugin());   		
		}
		
		
		@Test (groups="PluginsTest")
		public void PostgreSQL() {
			
			Assert.assertTrue(sahiTasks.PostGreSQLPlugin());
		}
		
		@Test (groups="PluginsTest")
		public void RHQAgent() {
			
			Assert.assertTrue(sahiTasks.RHQAgentPlugin());
		}
		
		@Test (groups="PluginsTest")
		public void Script() {
			
			Assert.assertTrue(sahiTasks.ScriptPlugin());
		}
		
	   
		
	}
	




