package com.redhat.qe.jon.clitest.tests.configuration;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.*;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import org.testng.annotations.Test;

;

public class ResourceConfiguration extends ConfigurationCliTest {

	@BeforeClass()
	public void beforeClass() throws IOException, CliTasksException {
		// run getAllConfigurationProperties
//		getAllConfigurationProperties();
	}

	private static String[] readDataFromFile() {

		String strLine = null;
		String[] strList = new String[0];
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream("/tmp/filename.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {

				strList = strLine.split(",");
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return strList;
	}

	@DataProvider
	public Object[][] createStartupConfigurations() {

		String[] configs = readDataFromFile();

		if (configs != null) {
			Object[][] output = new Object[configs.length][];
			for (int i = 0; i < configs.length; i++) {
				output[i] = new Object[] { configs[i] };
			}
			return output;
		}

		return null;
	}

	@Test(dataProvider = "createStartupConfigurations", description = "This test puts configuration prperty names and values into admin configuration")
	public void updateResourceConfigurationTest(String config)
			throws IOException, CliTasksException {
		updateResourceConfiguration(config);
	}

	@AfterClass
	public void teardown() {
		// we start AS with all defaults (as it was before)
		log.info("Starting server with default (none) parameters");
		// sshClient.restart("domain.sh");
	}
}
