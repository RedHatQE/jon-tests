package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class SchedulingForResourcesTest extends SahiTestScript {
	
	public static String RESOURCE_NAME 				= "resourceName";
	public static String METRIC_NAME 				= "metricName";
	public static String COLLECTION_INTERVAL		= "collectionInterval";

	
	@Test (groups="metricCollectionResourceTest", dataProvider="metricTestData",dependsOnMethods={"disableMetric"})
	public void enableMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), false, metricDetail.get(COLLECTION_INTERVAL), true), "Enable Metric validation");
	}
	
	@Test (groups="metricTest", dataProvider="metricTestData")
	public void disableMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), false, metricDetail.get(COLLECTION_INTERVAL), false), "Disable Metric validation");
	}
	
	@Test (groups="metricTest", dataProvider="metricTestData")
	public void updateMetric(HashMap<String, String> metricDetail){
		Assert.assertTrue(sahiTasks.enableDisableUpdateMetric(metricDetail.get(RESOURCE_NAME), metricDetail.get(METRIC_NAME), true, metricDetail.get(COLLECTION_INTERVAL), true), "Update Metric collection intervals");
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
