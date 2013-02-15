package com.redhat.qe.jon.clitest.tests.configuration;

import java.io.IOException;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;


public class  ConfigurationCliTest extends CliTest {
	
	/**
	 * 
	 * @param config
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void updateAgentConfiguration(String config) throws IOException, CliTasksException {
				
		runJSfile(null, "rhqadmin", "rhqadmin", "resourceConfiguration/testUpdateConfiguration.js", config ,null, null, "rhqapi.js", null, null);
	}
	
	/**
	 * 
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void getAllConfigurationProperties() throws IOException, CliTasksException {
				
		runJSfile(null, "rhqadmin", "rhqadmin", "resourceConfiguration/getAllConfigurationproperties.js", null ,null, null, "rhqapi.js", null, null);
	}
	
	/**
	 * 
	 * @param configs
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void updateResourceConfiguration(String config) throws IOException, CliTasksException {
		
		runJSfile(null, "rhqadmin", "rhqadmin", "resourceConfiguration/updateResourceConfiguration.js", config ,null, null, "rhqapi.js", null, null);
	}
	

}
