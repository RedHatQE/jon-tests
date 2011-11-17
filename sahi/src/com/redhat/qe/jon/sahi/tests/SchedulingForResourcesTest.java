package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class SchedulingForResourcesTest extends SahiTestScript {
	
	private static Logger _logger = Logger.getLogger(SchedulingForResourcesTest.class.getName());
	public static String RESOURCE_NAME 				= "resourceName";
	public static String METRIC_NAME 				= "metricName";
	public static String COLLECTION_INTERVAL		= "collectionInterval";

	private static LinkedList<HashMap<String, String>> metricDetails = null;
	private static String lastResource = null;
	private static int tableOffset = 0;
	
	private static LinkedList<HashMap<String, String>> readMetricTable(String resourceName){
		if(resourceName == null){
			_logger.log(Level.FINE, "Metric table details already available");
			return metricDetails;
		}else{
			if(lastResource == null){
				_logger.log(Level.FINE, "Reading Metric table details...");
				tableOffset = sahiTasks.getMetricTableOffset(resourceName);
				metricDetails = sahiTasks.getMetricTableDetails(null, tableOffset);
				lastResource = resourceName;
				return metricDetails;
			}else if(lastResource.equals(resourceName)){
				_logger.log(Level.FINE, "Metric table details already available");
				return metricDetails;
			}else{
				_logger.log(Level.FINE, "Reading Metric table details...");
				tableOffset = sahiTasks.getMetricTableOffset(resourceName);
				metricDetails = sahiTasks.getMetricTableDetails(null, tableOffset);
				lastResource = resourceName;
				return metricDetails;
			}
		}
	}
	
	@Test (groups="metricCollectionResourceTest", dataProvider="metricTestData",dependsOnMethods={"disableMetric"})
	public void enableMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), readMetricTable(metricDetail.get(RESOURCE_NAME)), false, metricDetail.get(COLLECTION_INTERVAL), true, tableOffset), "Enable Metric validation");
	}
	
	@Test (groups="metricTest", dataProvider="metricTestData")
	public void disableMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), readMetricTable(metricDetail.get(RESOURCE_NAME)), false, metricDetail.get(COLLECTION_INTERVAL), false, tableOffset), "Disable Metric validation");
	}
	
	@Test (groups="metricTest", dataProvider="metricTestData")
	public void updateMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), readMetricTable(metricDetail.get(RESOURCE_NAME)), true, metricDetail.get(COLLECTION_INTERVAL), true, tableOffset), "Update Metric collection intervals");
	}
	
	
	@SuppressWarnings("unchecked")
	@DataProvider(name="metricTestData")
	public Object[][] getmetricsData(){
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
				
		map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(METRIC_NAME, "JVM Total Memory");
		map.put(COLLECTION_INTERVAL, "10 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		//map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(METRIC_NAME, "JVM Free Memory");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
		//map.put(RESOURCE_NAME, "Servers=RHQ Agent");
		map.put(METRIC_NAME, "JVM Active Threads");
		map.put(COLLECTION_INTERVAL, "5 minutes");
		data.add((HashMap<String, String>) map.clone());
		map.clear();
				
		return TestNGUtils.convertListTo2dArray(data);
		
	}
	

}
