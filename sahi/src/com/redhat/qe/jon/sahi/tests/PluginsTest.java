package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.junit.Assert;
import org.testng.annotations.Test;





	public class PluginsTest extends SahiTestScript {
		
		//exact list of req'd plugins being provided by development 
		
		//////////////////////////////
		// Agent Plugins
		/////////////////////////////
		
		
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
		
		/////////////////////////
		// Server Plugins 
		/////////////////////////
		
		@Test (groups="PluginsTest")
		public void AntBundleProcessor() {
			
			sahiTasks.AntBundleProcessorPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void PerspectiveCore() {
			
			sahiTasks.PerspectiveCorePlugin();
		}
		
		@Test (groups="PluginsTest")
		public void DiskContent() {
			
			 sahiTasks.DiskContentPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void FileTemplateBundleProcessor() {
			
			 sahiTasks.FileTemplateBundleProcessorPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void JBossCSPContent() {
			
			 sahiTasks.JBossCSPContentPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void URLContent() {
			
			  sahiTasks.URLContentPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void YumContent() {
			
			 sahiTasks.YumContentPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void AlertCLI() {
			
			 sahiTasks.AlertCLIPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void AlertEmail() {
			
			 sahiTasks.AlertEmailPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void AlertIRC() {
			
			  sahiTasks.AlertIRCPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void AlertMicroblog() {
			
			 sahiTasks.AlertMicroBlogPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void Mobicents() {
			
			 sahiTasks.AlertMobicentsPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void AlertOperations() {
			
			sahiTasks.AlertOperationsPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void AlertRoles() {
			
			sahiTasks.AlertRolesPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void AlertSNMP() {
			
			 sahiTasks.AlertSNMPPlugin();
		}
		
		
		
		@Test (groups="PluginsTest")
		public void AlertSubject() {
			
			 sahiTasks.AlertSubjectPlugin();
		}
		
		@Test (groups="PluginsTest")
		public void DriftJPA() {
			
			 sahiTasks.DriftJPAPlugin();
		}
	    
		@Test (groups="PluginsTest")
		public void PackageTypeCLI() {
			
			 sahiTasks.PackageTypeCLIPlugin();
		}
		
	}
	




