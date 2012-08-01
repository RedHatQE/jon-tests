package com.redhat.qe.jon.sahi.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Nov 02, 2011
 */
public class RHQGuiInstallationTest  extends SahiTestScript{
	private static Logger _logger = Logger.getLogger(RHQGuiInstallationTest.class.getName());
	
	public static String DB_TYPE						= "dbType";
	public static String DB_DETAILS 					= "dbDetails";
	public static String DB_USER_PASSWORD				= "dbUserPassword";
	public static String DB_EXISTS_SELECTION			= "dbExistsSelection";
	public static String REGISTERED_SERVER_SELECTION	= "reisteredServerSelection";
	public static String SERVER_DETAILS 				= "serverDetails";
	public static String EMBEDDED_AGENT_ENABLED			= "embeddedAgentEnabled";
	
	@Parameters({"db.type", "db.details", "db.user.password", "db.exists.selection", "registered.server.selection", "server.details", "embedded.agent.enabled"})
	@Test (groups="RHQGUIInstallationTest")
	public void rhqGUIinstallation(String dbType, String dbDetails, @Optional String dbUserPassword, @Optional String dbExistsSelection, @Optional String registeredServerSelection, @Optional String serverDetails, String isEmbeddedAgentEnabled){
		Assert.assertTrue(sahiTasks.guiInstallationRHQ(dbType, dbDetails, dbUserPassword, dbExistsSelection, registeredServerSelection, serverDetails, isEmbeddedAgentEnabled), "RHQ/JON GUI installation check");
	}
	
	/*
	@Test (groups="RHQGUIInstallationTest", dataProvider="RHQGUIInstallationData")
	public void rhqGUIinstallation(HashMap<String, String> guiInstallationDetail) throws InterruptedException, IOException{
		Assert.assertTrue(sahiTasks.guiInstallationRHQ(guiInstallationDetail.get(DB_TYPE), guiInstallationDetail.get(DB_DETAILS), guiInstallationDetail.get(DB_USER_PASSWORD), guiInstallationDetail.get(DB_EXISTS_SELECTION), guiInstallationDetail.get(REGISTERED_SERVER_SELECTION), guiInstallationDetail.get(SERVER_DETAILS), guiInstallationDetail.get(EMBEDDED_AGENT_ENABLED)), "RHQ/JON GUI installation check");
	}
	*/
	@SuppressWarnings("unchecked")
	@DataProvider(name="RHQGUIInstallationData")
	public Object[][] getDriftCreationData(){
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
				
		map.put(DB_TYPE, "PostgreSQL"); //Available Options: PostgreSQL, Oracle, MS SQL Server, H2 (embedded)
		map.put(DB_DETAILS, "propForm:databaseconnectionurl=jdbc:postgresql://127.0.0.1:5432/rhqfreshdb, propForm:databasedriverclass=org.postgresql.Driver, propForm:databaseusername=rhqadmin"); //Available Boxes: propForm:databaseconnectionurl, propForm:databasedriverclass, propForm:databasexadatasourceclass, propForm:databaseusername
		map.put(DB_USER_PASSWORD, "rhqadmin");
		map.put(DB_EXISTS_SELECTION, "Overwrite (lose existing data)"); //Available Options: Keep (maintain existing data), Overwrite (lose existing data), Skip (leave database as-is)
		//map.put(REGISTERED_SERVER_SELECTION, "mercury.lab.eng.pnq.redhat.com");//Available Options: New Server, <server name>
		//map.put(SERVER_DETAILS, "propForm:haservername=localhost, propForm:haendpointaddress=localhost1, propForm:haendpointport=123, propForm:haendpointsecureport=1234, 8=1.2.3.4,9=localhost123,10=jkandasa@redhat.com");//Available Boxes: propForm:haservername, propForm:haendpointaddress, propForm:haendpointport, propForm:haendpointsecureport, 8,9,10 [8-Server Bind Address, 9-Email SMTP host name, 10 - email from address]
		map.put(EMBEDDED_AGENT_ENABLED, "false");
		
		data.add((HashMap<String, String>) map.clone());
		map.clear();
		
				
		return TestNGUtils.convertListTo2dArray(data);
		
	}
}
