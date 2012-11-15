package com.redhat.qe.jon.clitest.tests.coniguration;

import java.io.IOException;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;


public class  ConfigurationCliTest extends CliTest {
	
	/**
	 * 
	 * @param configs
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void updateAgentConfiguration(String config) throws IOException, CliTasksException {
				
		runJSfile(null, "rhqadmin", "rhqadmin", "resourceConfiguration/testUpdateConfiguration.js", config ,null, null, "rhqapi.js", null, null);
	}



}
