package com.redhat.qe.jon.clitest.tests.configuration;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;


public class ResourceConfiguration extends ConfigurationCliTest {
	
	@BeforeClass()
	public void beforeClass() throws IOException, CliTasksException {
		// run getAllConfigurationProperties
		//getAllConfigurationProperties();
	}

	private static List<String>  readDataFromFile() {
		String strLine = null;
		List<String> strList = new ArrayList<String>();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream("/tmp/resourceProperties.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {

				strList.add(strLine);
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

		List<String> configStringList = readDataFromFile();
		List<ConfigValue> configs = new ArrayList<ConfigValue>();
		
		if (configStringList != null) {
			for (int i = 0; i < configStringList.size(); i++) {
				String[] splited = configStringList.get(i).split(" ");
				configs.add(new ConfigValue(splited[0],splited[1],splited[2],splited[3]));
			}
			
		}

		return getDataProviderArray(configs);

	}

	@Test(dataProvider = "createStartupConfigurations", description = "This test puts configuration prperty names and values into admin configuration")
	public void updateResourceConfigurationTest(ConfigValue config)
			throws IOException, CliTasksException {
		updateResourceConfiguration(config);
	}
}
