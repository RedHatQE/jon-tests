package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class SchedulingForResourcesTest extends SahiTestScript {
	
	private static Logger _logger = Logger.getLogger(SchedulingForResourcesTest.class.getName());
	public static String RESOURCE_NAME 				= "resourceName";
	public static String METRIC_NAME 				= "metricName";
	public static String DESCRIPTION 				= "description";
	public static String COLLECTION_INTERVAL		= "collectionInterval";
	public static String TABLE_DIV_CONTENT = "Agent Home Directory";

	private static LinkedList<HashMap<String, String>> metricDetails = null;
	private static String lastResource = null;
	
	private static LinkedList<HashMap<String, String>> readMetricTable(String resourceName){
		if(resourceName == null){
			_logger.log(Level.FINE, "Metric table details already available");
			return metricDetails;
		}else{
			if(lastResource == null){
				_logger.log(Level.FINE, "Reading Metric table details...");
				metricDetails = sahiTasks.getMetricTableDetails(resourceName, TABLE_DIV_CONTENT);
				lastResource = resourceName;
				return metricDetails;
			}else if(lastResource.equals(resourceName)){
				_logger.log(Level.FINE, "Metric table details already available");
				return metricDetails;
			}else{
				_logger.log(Level.FINE, "Reading Metric table details...");
				metricDetails = sahiTasks.getMetricTableDetails(resourceName, TABLE_DIV_CONTENT);
				lastResource = resourceName;
				return metricDetails;
			}
		}
	}
	
	@Test (groups="metricCollectionResourceTest", dataProvider="metricTestData")
	public void enableMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), metricDetail.get(DESCRIPTION), readMetricTable(metricDetail.get(RESOURCE_NAME)), false, metricDetail.get(COLLECTION_INTERVAL), true, TABLE_DIV_CONTENT), "Enable Metric validation");
	}
	
	@Test (groups="metricTest", dataProvider="metricTestData")
	public void disableMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), metricDetail.get(DESCRIPTION), readMetricTable(metricDetail.get(RESOURCE_NAME)), false, metricDetail.get(COLLECTION_INTERVAL), false, TABLE_DIV_CONTENT), "Disable Metric validation");
	}
	
	@Test (groups="metricTest", dataProvider="metricTestData")
	public void updateMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), metricDetail.get(DESCRIPTION), readMetricTable(metricDetail.get(RESOURCE_NAME)), true, metricDetail.get(COLLECTION_INTERVAL), true, TABLE_DIV_CONTENT), "Update Metric collection intervals");
	}
	
	
	@SuppressWarnings("unchecked")
	@DataProvider(name="metricTestData")
	public Object[][] getmetricsData(){
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
				
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(METRIC_NAME, "Agent-Server Clock Difference");
		map.put(DESCRIPTION, "Number of milliseconds the agent's clock differs from its server's clock");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Avg Execution Time Commands Received Successfully");
		map.put(DESCRIPTION, "Average time it took to process incoming commands that are ultimately successful");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();

		map.put(METRIC_NAME, "Avg Execution Time Commands Sent Successfully");
		map.put(DESCRIPTION, "Average time it took to send commands that are ultimately successful");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();

		map.put(METRIC_NAME, "JVM Active Threads");
		map.put(DESCRIPTION, "The number of active threads currently running in the agent JVM");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "JVM Free Memory");
		map.put(DESCRIPTION, "The amount of free memory the agent JVM has in its heap");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "JVM Total Memory");
		map.put(DESCRIPTION, "The amount of total memory the agent JVM has in its heap");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Number Of Active Commands Being Sent");
		map.put(DESCRIPTION, "The number of messages this agent is currently sending");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Number of Agent Restarts");
		map.put(DESCRIPTION, "Number of times the agent was restarted during the lifetime of its Java Virtual Machine");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Number of Commands In Queue");
		map.put(DESCRIPTION, "Number of messages currently queued waiting to be sent to the RHQ Server");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Number of Commands Received but Failed per Minute");
		map.put(DESCRIPTION, "Number of messages this agent has received from the RHQ Server but failed to process");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Number of Commands Received Successfully per Minute");
		map.put(DESCRIPTION, "Number of messages this agent has received from the RHQ Server and succesfully processed");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Number of Commands Spooled To Disk");
		map.put(DESCRIPTION, "Number of messages spooled to disk waiting to be sent to the RHQ Server");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		map.put(METRIC_NAME, "Number of Commands Successfully Sent per Minute");
		map.put(DESCRIPTION, "Number of messages this agent has sent to the RHQ Server successfully");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		return TestNGUtils.convertListTo2dArray(data);
		
	}
}
