package com.redhat.qe.jon.sahi.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;



/**
 * @author jkandasa (Jeeva Kandasamy)
 * Sep 16, 2011
 */
public class DriftManagementTest extends SahiTestScript {
	private static Logger _logger = Logger.getLogger(DriftManagementTest.class.getName());
	private static long driftCreationTime;
	private static final int driftDelayTime = 1000*60*2;

	public static String RESOURCE_NAME 						= "resourceName";
	public static String DRIFT_NAME 						= "driftName";
	public static String DRIFT_TEMPLATE 					= "driftTemplate";
	public static String DRIFT_RADIO_BUTTONS				= "driftRadioButtons";
	public static String DRIFT_TEXT_BOXES 					= "driftTextBoxes";
	public static String DRIFT_BASE_DIR 					= "driftBaseDir";
	public static String DRIFT_FILE_INCLUDES				= "driftFileIncludes";
	public static String DRIFT_FILE_EXCLUDES				= "driftFileExcludes";
	public static String DRIFT_DIR_INCLUDES					= "driftDirIncludes";
	public static String DRIFT_DIR_EXCLUDES					= "driftDirExcludes";
	
	public static String DRIFT_ACTION_FILE_INCLUDE_ADD		= "driftActionFileIncludeAdd";
	public static String DRIFT_ACTION_FILE_EXCLUDE_ADD		= "driftActionFileExcludeAdd";
	
	public static String DRIFT_ACTION_FILE_INCLUDE_CHANGE	= "driftActionFileIncludeChange";
	public static String DRIFT_ACTION_FILE_EXCLUDE_CHANGE	= "driftActionFileExcludeChange";
	
	public static String DRIFT_ACTION_FILE_INCLUDE_REMOVE	= "driftActionFileIncludeRemove";
	public static String DRIFT_ACTION_FILE_EXCLUDE_REMOVE	= "driftActionFileExcludeRemove";
	
	
	

	@Test (groups="driftTest", dataProvider="driftCreationData")
	public void createDrift(HashMap<String, String> driftDetail) throws InterruptedException, IOException{
		Assert.assertTrue(sahiTasks.addDrift(driftDetail.get(DRIFT_BASE_DIR), driftDetail.get(RESOURCE_NAME), driftDetail.get(DRIFT_TEMPLATE), driftDetail.get(DRIFT_NAME), driftDetail.get(DRIFT_TEXT_BOXES), driftDetail.get(DRIFT_RADIO_BUTTONS), driftDetail.get(DRIFT_FILE_INCLUDES), driftDetail.get(DRIFT_FILE_EXCLUDES)), "Resource [Drift Name]: "+driftDetail.get(RESOURCE_NAME)+"["+driftDetail.get(DRIFT_NAME)+"]");
		driftCreationTime  = new Date().getTime();
	}
	
	@Test (groups="driftTest", dataProvider="driftCreationData", dependsOnMethods={"createDrift"})
	public void driftActionFileAdd(HashMap<String, String> driftDetail) throws InterruptedException, IOException{
		Assert.assertTrue(sahiTasks.addChangeRemoveDriftFile(driftDetail.get(RESOURCE_NAME), driftDetail.get(DRIFT_NAME), driftDetail.get(DRIFT_BASE_DIR), driftDetail.get(DRIFT_ACTION_FILE_INCLUDE_ADD), driftDetail.get(DRIFT_ACTION_FILE_EXCLUDE_ADD), "added"), "File added validation check");
	}
	
	@Test (groups="driftTest", dataProvider="driftCreationData", dependsOnMethods={"createDrift", "driftActionFileAdd"})
	public void driftActionFileChange(HashMap<String, String> driftDetail) throws InterruptedException, IOException{
		Assert.assertTrue(sahiTasks.addChangeRemoveDriftFile(driftDetail.get(RESOURCE_NAME), driftDetail.get(DRIFT_NAME), driftDetail.get(DRIFT_BASE_DIR), driftDetail.get(DRIFT_ACTION_FILE_INCLUDE_CHANGE), driftDetail.get(DRIFT_ACTION_FILE_EXCLUDE_CHANGE), "changed"), "File changed validation check");
	}
	
	@Test (groups="driftTest", dataProvider="driftCreationData", dependsOnMethods={"createDrift", "driftActionFileAdd", "driftActionFileChange"})
	public void driftActionFileRemove(HashMap<String, String> driftDetail) throws InterruptedException, IOException{
		Assert.assertTrue(sahiTasks.addChangeRemoveDriftFile(driftDetail.get(RESOURCE_NAME), driftDetail.get(DRIFT_NAME), driftDetail.get(DRIFT_BASE_DIR), driftDetail.get(DRIFT_ACTION_FILE_INCLUDE_CHANGE), driftDetail.get(DRIFT_ACTION_FILE_EXCLUDE_CHANGE), "removed"), "File removed validation check");
	}
	
	@Test (groups="driftTest", dataProvider="driftCreationData", dependsOnMethods={"createDrift", "driftActionFileAdd", "driftActionFileChange", "driftActionFileRemove"})
	public void trashDrift(HashMap<String, String> driftDetail) throws InterruptedException, IOException{
		Assert.assertFalse(sahiTasks.deleteDrift(driftDetail.get(RESOURCE_NAME), driftDetail.get(DRIFT_NAME), driftDetail.get(DRIFT_BASE_DIR)), "Drift Removed validation check");
	}

	
	@SuppressWarnings("unchecked")
	@DataProvider(name="driftCreationData")
	public Object[][] getDriftCreationData(){
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
				
		String agentName = System.getenv().get("AGENT_NAME");
		map.put(RESOURCE_NAME, "Platforms="+agentName);
		map.put(DRIFT_TEMPLATE, "Template-File System --> Template-File System");
		map.put(DRIFT_NAME, "File SystemDrift - monitor changes in file");
		map.put(DRIFT_BASE_DIR, "/tmp/automationDriftManagementDir/");
		map.put(DRIFT_TEXT_BOXES, "interval=60,valueName="+map.get(DRIFT_BASE_DIR));
		map.put(DRIFT_RADIO_BUTTONS, "enabled,normal,fileSystem");
		//map.put(DRIFT_DIR_INCLUDES, "includeDir");
		//map.put(DRIFT_DIR_EXCLUDES, "excludeDir");
		map.put(DRIFT_FILE_INCLUDES, "includeDir");
		map.put(DRIFT_FILE_EXCLUDES, "excludeDir");
		map.put(DRIFT_ACTION_FILE_INCLUDE_ADD, "includeDir/folder1/file1.txt=Line #1 on file1,includeDir/folder2/file2.txt=Line #1 on File2");
		map.put(DRIFT_ACTION_FILE_EXCLUDE_ADD, "excludeDir/folder3/file3.txt=Line #1 on file3,excludeDir/folder4/file4.txt=Line #1 on File4");
		map.put(DRIFT_ACTION_FILE_INCLUDE_CHANGE, "includeDir/folder1/file1.txt=Line #2 on file1,includeDir/folder2/file2.txt=Line #2 on File2");
		map.put(DRIFT_ACTION_FILE_EXCLUDE_CHANGE, "excludeDir/folder3/file3.txt=Line #2 on file3,excludeDir/folder4/file4.txt=Line #2 on File2");
		map.put(DRIFT_ACTION_FILE_INCLUDE_REMOVE, "includeDir/folder1/file1.txt,includeDir/folder2/file2.txt");
		map.put(DRIFT_ACTION_FILE_EXCLUDE_REMOVE, "excludeDir/folder3/file3.txt,excludeDir/folder4/file4.txt");
		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
				
		return TestNGUtils.convertListTo2dArray(data);
		
	}
}