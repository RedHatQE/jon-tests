package com.redhat.qe.jon.rest.tests;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


import com.redhat.qe.Assert;
import com.redhat.qe.jon.rest.tasks.RestClient;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * @since Dec 20, 2011
 */
public class RestTest extends RestClient{
	private static Logger _logger = Logger.getLogger(RestTest.class.getName());

	@BeforeClass
	public void loadRequiredValues(){
		SERVER_URI = System.getenv().get("SERVER_URL");

	}

	@Parameters({ "rest.username", "rest.password", "test.type" })
	@Test (groups="restClientJava")
	public void loginTest(String rhqUser, String rhqPassword, String testType){
		webResource = this.getWebResource(SERVER_URI+URI_PREFIX, rhqUser, rhqPassword);
		HashMap<String, Object> result = this.getReponse(webResource, URIs.STATUS.getUri()+".json", null);
		if(testType.equalsIgnoreCase("positive")){
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Login validation check Positive, Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		}else{
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 401, "Login validation check Negative, Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		}

	}

	@Test (groups="restClientJava")
	public void listStatus() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.STATUS.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			
		JSONObject jsonObject = this.getJSONObject(""+result.get(RESPONSE_CONTENT));
		jsonObject = (JSONObject) jsonObject.get(STATUS_VALUES);
		
		Assert.assertTrue(jsonObject != null, "Result set should not be NULL");
		System.setProperty("rhq.build.version", jsonObject.get(SERVER_VERSION)+" ("+jsonObject.get(BUILD_NUMBER)+")");
		_logger.info("Status Result: \n"+this.getKeyValue(jsonObject, new StringBuffer()));		
	}
	
	@Test (groups="restClientJava")
	public void unauthorizedLoginTest(){
		webResource = this.getWebResource(SERVER_URI+URI_PREFIX);
		HashMap<String, Object> result = this.getReponse(webResource, URIs.STATUS.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 401, "Unauthorized Login validation check, Negative, Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
	}

	@Test (groups="restClientJava")
	public void visitURIplatforms() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		_logger.fine("Number of Resource(s): "+jsonArray.size());
		Assert.assertTrue(jsonArray.size()>0, "Number of Platform(s) [>0] : "+jsonArray.size());
		JSONObject jsonObject;
		for(int i=0; i<jsonArray.size();i++){
			jsonObject = (JSONObject) jsonArray.get(i);
			this.printKeyValue(jsonObject);
		}		
	}

	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void validateChildren(@Optional String parentId) throws ParseException{
		HashMap<String, Object> result;
		if(parentId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			this.printKeyValue(jsonObject);
			parentId = ""+jsonObject.get(RESOURCE_ID);
		}

		result =  this.getReponse(webResource, URIs.CHILDREN_RESOURCE.getUri().replace("@@id@@", parentId)+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		_logger.fine("Number of Children: "+jsonArray.size());
		JSONObject jsonObject;
		for(int i=0; i<jsonArray.size();i++){
			jsonObject = (JSONObject) jsonArray.get(i);
			this.printKeyValue(jsonObject);
			Assert.assertEquals(parentId, ""+jsonObject.get(PARENT_ID), "Parent ID validation");
		}
	}


	/**
	 * recursively checks all resources of platform and prints our AS server Id
	 * @param parentId
	 * @throws ParseException
	 */
	
	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void verifyASServerPresence(@Optional String parentId) throws ParseException{
		HashMap<String, Object> result;
		String childResourceId = "";
		if(parentId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			this.printKeyValue(jsonObject);
			parentId = ""+jsonObject.get(RESOURCE_ID);
		}

		result =  this.getReponse(webResource, URIs.CHILDREN_RESOURCE.getUri().replace("@@id@@", parentId)+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		_logger.fine("Number of Children: "+jsonArray.size());
		JSONObject jsonObject;
		for(int i=0; i<jsonArray.size();i++){
			jsonObject = (JSONObject) jsonArray.get(i);
			this.printKeyValue(jsonObject);
			Assert.assertEquals(parentId, ""+jsonObject.get(PARENT_ID), "Parent ID validation");
			if(jsonObject.get("typeName").toString().contains("JBossAS Server")){
				 childResourceId =  ""+jsonObject.get("resourceId");
				_logger.log(Level.INFO, "AS Server resourceId is ..."+ childResourceId);	
			}
			
		}
		
	}
	
	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void checkPlatformOperations(@Optional String parentId) throws ParseException{
		HashMap<String, Object> result;
		String childResourceId = "";
		if(parentId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
		//	this.printKeyValue(jsonObject);
			parentId = ""+jsonObject.get(RESOURCE_ID);
	}

		//call get operation for platform
		_logger.log(Level.INFO,"REQUEST IS ---" + webResource + "  URI -- " + URIs.OPERATION_DEFINITION.getUri() );
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("resourceId", parentId);
		result = this.getReponse(webResource, URIs.OPERATION_DEFINITIONS.getUri(), queryParams);
		_logger.log(Level.INFO,"RESULT IS ---" + result );
		
		//get first operation ID and Name for Platform
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		JSONObject jsonObject;
		jsonObject = (JSONObject) jsonArray.get(0);
		this.printKeyValue(jsonObject);
		String  operationId = ""+jsonObject.get(ID);
		_logger.log(Level.INFO,"operationId IS ---" + operationId );
		
		String  operationName = ""+jsonObject.get(NAME);
		_logger.log(Level.INFO,"operationName IS ---" + operationName );
		
		//call operation/definition with post
		MultivaluedMap queryParam = new MultivaluedMapImpl();
		queryParam.add("resourceId", parentId);
		_logger.log(Level.INFO, "Query Param IS   ---  "+queryParam);
		result = this.postResponse(webResource, URIs.OPERATION_DEFINITION.getUri().replace("@@id@@", operationId), queryParam);
		_logger.log(Level.INFO, "reponse code is  ---  "+result.get(RESPONSE_STATUS_CODE));
	}
	
	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void validateSchedules() throws ParseException{
		HashMap<String, Object> result;
		
		//get platform
		result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		JSONObject jsonObject;
		jsonObject = (JSONObject) jsonArray.get(0);
	//	this.printKeyValue(jsonObject);
		String  resourceId = ""+jsonObject.get(RESOURCE_ID);
		//get schedules
		result = this.getReponse(webResource, URIs.SCHEDULES.getUri().replace("@@id@@", resourceId)+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
	}
	
	
	@Test (groups="restClientJava")
	public void validateMetricDataDefault() throws ParseException{
		HashMap<String, Object> result;
		
		//get platform
		result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		JSONObject jsonObject;
		jsonObject = (JSONObject) jsonArray.get(0);
	//	this.printKeyValue(jsonObject);
		String  resourceId = ""+jsonObject.get(RESOURCE_ID);
		//get schedules
		result = this.getReponse(webResource, URIs.SCHEDULES.getUri().replace("@@id@@", resourceId)+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
		
		//get second schedule
		jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		jsonObject = (JSONObject) jsonArray.get(1);
		this.printKeyValue(jsonObject);
		String  scheduleId = ""+jsonObject.get(SCHEDULE_ID);
		
		result = this.getReponse(webResource, URIs.METRIC_DATA.getUri().replace("@@id@@", scheduleId), null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
	 
	}

	
	@Test (groups="restClientJava")
	public void validateMetricData(@Optional String parentId) throws ParseException{
		HashMap<String, Object> result;
		
		// get platform
		result = this.getReponse(webResource, URIs.PLATFORMS.getUri() + ".json", null);
		JSONArray jsonArray = this.getJSONArray(""	+ result.get(RESPONSE_CONTENT));
		JSONObject jsonObject;
		jsonObject = (JSONObject) jsonArray.get(0);
		//this.printKeyValue(jsonObject);
		String resourceId = "" + jsonObject.get(RESOURCE_ID);
		// get schedules
		result = this.getReponse(webResource, URIs.SCHEDULES.getUri().replace("@@id@@", resourceId)	+ ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

		// get second schedule
		jsonArray = this.getJSONArray("" + result.get(RESPONSE_CONTENT));
		jsonObject = (JSONObject) jsonArray.get(1);
		this.printKeyValue(jsonObject);
		String scheduleId = "" + jsonObject.get(SCHEDULE_ID);

		// create query param for start end times, dataPoints and hideEmpty
		// params
		MultivaluedMap queryParam = new MultivaluedMapImpl();
		boolean hideEmpty = false;
		queryParam.add("hideEmpty", ""+hideEmpty);
		queryParam.add("dataPoints", "60");

		result = this.getReponse(webResource, URIs.METRIC_DATA.getUri()
				.replace("@@id@@", scheduleId), queryParam);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}
	@Test (groups="restClientJava")
	public void visitAlerts() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ALERT.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
		
	}
		
}