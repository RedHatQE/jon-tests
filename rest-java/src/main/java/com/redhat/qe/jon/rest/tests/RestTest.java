package com.redhat.qe.jon.rest.tests;

//import org.rhq.core.domain.resource.group.Group;
//import org.rhq.core.domain.resource.group.GroupDefinition;


import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;
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
 * @author jkandasa (Jeeva Kandasamy), ahovsepy 
 * @since Dec 20, 2011
 */
public class RestTest extends RestClient{
	private static Logger _logger = Logger.getLogger(RestTest.class.getName());
	private static String platformId=null;
	private static String platformName = null;
	
	@BeforeClass
	public void loadRequiredValues(){
		SERVER_URI = System.getenv().get("SERVER_URL");

	}
	@Parameters({ "rest.username", "rest.password", "test.type" })
	@Test (groups="restClientJava")
	public void checkNewRestApi(String rhqUser, String rhqPassword, String testType){
		webResource = this.getWebResource(SERVER_URI+URI_PREFIX, rhqUser, rhqPassword);
		HashMap<String, Object> result = this.getReponse(webResource, URIs.STATUS.getUri()+".json", null);
		
		if (!(result.get(RESPONSE_STATUS_CODE).equals(200))) {
			RestClient.URI_PREFIX = "/rest";
			_logger.log(Level.INFO, "THE RestClient.URI_PREFIX IS --- " + RestClient.URI_PREFIX);
			
		}
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
	//	_logger.info("Status Result: \n"+this.getKeyValue(jsonObject, new StringBuffer()));		
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
		Assert.assertTrue(jsonArray.size()>0, "Number of Platform(s) [>0] : "+jsonArray.size());
	}
	@Test (groups="restClientJava")
	public void getPlatformById() throws ParseException{
		HashMap<String, Object> result;
		if(platformId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformId = ""+jsonObject.get(RESOURCE_ID);
		}
		result = this.getReponse(webResource, URIs.PLATFORM_BY_ID.getUri().replace("@@id@@", platformId)+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONObject jsonObject = this.getJSONObject(""+result.get(RESPONSE_CONTENT));
		Assert.assertTrue(jsonObject.size()>0, "Number of Platform(s) [>0] : "+jsonObject.size());
	}
	
	@Test (groups="restClientJava")
	public void getResourceByPlatformName() throws ParseException{
		HashMap<String, Object> result;
		if(platformName == null){
			_logger.log(Level.INFO, "There is no paltform NAME, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformName = ""+jsonObject.get(RESOURCE_NAME);
			_logger.log(Level.INFO, "There is no paltform NAME ..."+platformName);
		}
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("q", platformName);
		
		result = this.getReponse(webResource, URIs.RESOURCE.getUri()+".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		Assert.assertTrue(jsonArray.size()>0, "Number of resource(s) [>0] : "+jsonArray.size());
	}
	
	
	@Test (groups="restClientJava")
	public void getResourceByPlatformNameAndCategory() throws ParseException{
		HashMap<String, Object> result;
		if(platformName == null){
			_logger.log(Level.INFO, "There is no paltform NAME, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformName = ""+jsonObject.get(RESOURCE_NAME);
			_logger.log(Level.INFO, "There is no paltform NAME ..."+platformName);
		}
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("q", platformName);
		queryParams.add("category", "platform");
		
		result = this.getReponse(webResource, URIs.RESOURCE.getUri()+".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		Assert.assertTrue(jsonArray.size()==1, "Number of resource(s) [==1] : "+jsonArray.size());
	}

	@Test (groups="restClientJava")
	public void getResourcesByCategory() throws ParseException{
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("category", "PlAtForM");
		
		HashMap<String, Object> result = this.getReponse(webResource, URIs.RESOURCE.getUri()+".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		Assert.assertTrue(jsonArray.size()>0, "Number of Resource(s) [>0] : "+jsonArray.size());
		
		queryParams = new MultivaluedMapImpl();
		queryParams.add("category", "SeRvEr");
		
		 result = this.getReponse(webResource, URIs.RESOURCE.getUri()+".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		 jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		Assert.assertTrue(jsonArray.size()>0, "Number of Resource(s) [>0] : "+jsonArray.size());
		
		queryParams = new MultivaluedMapImpl();
		queryParams.add("category", "seRVice");
		
		 result = this.getReponse(webResource, URIs.RESOURCE.getUri()+".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		 jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		Assert.assertTrue(jsonArray.size()>0, "Number of Resource(s) [>0] : "+jsonArray.size());
		
	}
	
	@Test (groups="restClientJava")
	public void getResourceByPaging() throws ParseException{
		HashMap<String, Object> result;
	
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("category", "service");
		queryParams.add("page", "1");
		queryParams.add("ps", "2");
		
		result = this.getReponse(webResource, URIs.RESOURCE.getUri()+".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		Assert.assertTrue(jsonArray.size()==2, "Number of resource(s) [==2] : "+jsonArray.size());
		
	}

	@Test (groups="restClientJava")
	public void getPlatformXml() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".xml", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[application/xml]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
	}
	
	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void validateChildren() throws ParseException{
		HashMap<String, Object> result;
		if(platformId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformId = ""+jsonObject.get(RESOURCE_ID);
		}

		result =  this.getReponse(webResource, URIs.CHILDREN_RESOURCE.getUri().replace("@@id@@", platformId)+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		_logger.fine("Number of Children: "+jsonArray.size());
		JSONObject jsonObject;
		for(int i=0; i<jsonArray.size();i++){
			jsonObject = (JSONObject) jsonArray.get(i);
			this.printKeyValue(jsonObject);
			Assert.assertEquals(platformId, ""+jsonObject.get(PARENT_ID), "Parent ID validation");
		}
	}

	@Test(groups = "restClientJava")
	public void getAlertsForResources() throws ParseException {
		HashMap<String, Object> result;
		if (platformId == null) {
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri() + ".json", null);
			Assert.assertEquals( result.get(RESPONSE_STATUS_CODE), 200, "Response Code: " + result.get(RESPONSE_STATUS_CODE) + ", Response Message: " + result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray("" + result.get(RESPONSE_CONTENT));
			Assert.assertTrue(jsonArray.size() > 0, "Number of platform(s) [>0] : " + jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformId = "" + jsonObject.get(RESOURCE_ID);
		}
		result = this.getReponse(webResource, URIs.RESOURCE_ALERTS.getUri() .replace("@@id@@", platformId) + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}
	
	
	@Test(groups = "restClientJava")
	public void getSchedulesForResources() throws ParseException {
		HashMap<String, Object> result;
		if (platformId == null) {
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri() + ".json", null);
			Assert.assertEquals( result.get(RESPONSE_STATUS_CODE), 200, "Response Code: " + result.get(RESPONSE_STATUS_CODE) + ", Response Message: " + result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray("" + result.get(RESPONSE_CONTENT));
			Assert.assertTrue(jsonArray.size() > 0, "Number of platform(s) [>0] : " + jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformId = "" + jsonObject.get(RESOURCE_ID);
		}
		result = this.getReponse(webResource, URIs.RESOURCE_ALERTS.getUri() .replace("@@id@@", platformId) + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

	@Test(groups = "restClientJava")
	public void getAvailabilityForResources() throws ParseException {
		HashMap<String, Object> result;
		if (platformId == null) {
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri() + ".json", null);
			Assert.assertEquals( result.get(RESPONSE_STATUS_CODE), 200, "Response Code: " + result.get(RESPONSE_STATUS_CODE) + ", Response Message: " + result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray("" + result.get(RESPONSE_CONTENT));
			Assert.assertTrue(jsonArray.size() > 0, "Number of platform(s) [>0] : " + jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformId = "" + jsonObject.get(RESOURCE_ID);
		}
		result = this.getReponse(webResource, URIs.RESOURCE_AVAILABILITY.getUri() .replace("@@id@@", platformId) + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

	@Test(groups = "restClientJava")
	public void getAvailabilityHistoryForResources() throws ParseException {
		HashMap<String, Object> result;
		if (platformId == null) {
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri() + ".json", null);
			Assert.assertEquals( result.get(RESPONSE_STATUS_CODE), 200, "Response Code: " + result.get(RESPONSE_STATUS_CODE) + ", Response Message: " + result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray("" + result.get(RESPONSE_CONTENT));
			Assert.assertTrue(jsonArray.size() > 0, "Number of platform(s) [>0] : " + jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			platformId = "" + jsonObject.get(RESOURCE_ID);
		}
		result = this.getReponse(webResource, URIs.RESOURCE_AVAILABILITY_HISTORY.getUri() .replace("@@id@@", platformId) + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}
	
	@Test (groups="restClientJava")
	public void getRootURI() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ROOT_URI.getUri(), null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[text/html]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		result = this.getReponse(webResource, URIs.ROOT_URI.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[application/json]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		result = this.getReponse(webResource, URIs.ROOT_URI.getUri()+".html", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[text/html]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		result = this.getReponse(webResource, URIs.ROOT_URI.getUri()+".xml", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[application/xml]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		result = this.getReponse(webResource, URIs.INDEX.getUri(), null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[text/html]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		result = this.getReponse(webResource, URIs.INDEX.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[application/json]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		result = this.getReponse(webResource, URIs.INDEX.getUri()+".html", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[text/html]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		result = this.getReponse(webResource, URIs.INDEX.getUri()+".xml", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		Assert.assertEquals(result.get(RESPONSE_CONTENT_TYPE).toString(), "[application/xml]", "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
	}
	
	
	@Test (groups="restClientJava")
	public void getJsonPWrapper() throws ParseException{
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("jsonp", "foo");
	
		HashMap<String, Object> result = this.getReponse(webResource, URIs.INDEX.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		
		HashMap<String, Object> result1 = this.getReponse(webResource, URIs.INDEX.getUri()+".json", queryParams);
		Assert.assertEquals(result1.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result1.get(RESPONSE_STATUS_CODE)+", Response Message: "+result1.get(RESPONSE_STATUS_MESSAGE));

		Assert.assertEquals("foo("+result.get(RESPONSE_CONTENT).toString()+");", result1.get(RESPONSE_CONTENT).toString());
	}
		
	/**
	 * recursively checks all resources of platform and prints our AS server Id
	 * @param platformId
	 * @throws ParseException
	 */
	
	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void verifyASServerPresence() throws ParseException{
		HashMap<String, Object> result;
		String childResourceId = "";
		if(platformId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			this.printKeyValue(jsonObject);
			platformId = ""+jsonObject.get(RESOURCE_ID);
		}

		result =  this.getReponse(webResource, URIs.CHILDREN_RESOURCE.getUri().replace("@@id@@", platformId)+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
		_logger.fine("Number of Children: "+jsonArray.size());
		JSONObject jsonObject;
		for(int i=0; i<jsonArray.size();i++){
			jsonObject = (JSONObject) jsonArray.get(i);
			this.printKeyValue(jsonObject);
			Assert.assertEquals(platformId, ""+jsonObject.get(PARENT_ID), "Parent ID validation");
			if(jsonObject.get("typeName").toString().contains("JBossAS Server")){
				 childResourceId =  ""+jsonObject.get("resourceId");
				_logger.log(Level.INFO, "AS Server resourceId is ..."+ childResourceId);	
			}
			
		}
		
	}
	
	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void checkPlatformOperations() throws ParseException{
		HashMap<String, Object> result;
		String childResourceId = "";
		if(platformId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
		//	this.printKeyValue(jsonObject);
			platformId = ""+jsonObject.get(RESOURCE_ID);
	}

		//call get operation for platform
		_logger.log(Level.INFO,"REQUEST IS ---" + webResource + "  URI -- " + URIs.OPERATION_DEFINITION.getUri() );
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("resourceId", platformId);
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
		queryParam.add("resourceId", platformId);
		_logger.log(Level.INFO, "Query Param IS   ---  "+queryParam);
		result = this.postResponse(webResource, URIs.OPERATION_DEFINITION.getUri().replace("@@id@@", operationId), queryParam);
		_logger.log(Level.INFO, "reponse code is  ---  "+result.get(RESPONSE_STATUS_CODE));
	}
	
	@Parameters({ "parent.id"})
	@Test (groups="restClientJava")
	public void validateSchedules() throws ParseException{
		HashMap<String, Object> result;
		
		if(platformId == null){
			_logger.log(Level.INFO, "There is no paltform ID, taking it...");
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json", null);
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			this.printKeyValue(jsonObject);
			platformId = ""+jsonObject.get(RESOURCE_ID);
		}
		//get schedules
		result = this.getReponse(webResource, URIs.SCHEDULES.getUri().replace("@@id@@", platformId)+".json", null);
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
	public void validateMetricData() throws ParseException{
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
	
	@Test (groups="restClientJava")
	public void testRedirectFromDefinition() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ALERT_DEFINATION.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
		
	}
	
	@Test (groups="restClientJava")
	public void getAlertDefinitions() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ALERT_DEFINATIONS.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
		
	}
	@Test (groups="restClientJava")
	public void getAlertCount() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ALERT_COUNT.getUri()+".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
		
	}

	@Test(groups = "restClientJava")
	public void getAlertSender() throws ParseException {
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ALERT_SENDERS.getUri() + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

	@Test(groups = "restClientJava")
	public void getAlertSender_DirectEmails() throws ParseException {
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ALERT_SENDER_NAME.getUri().replace("@@senderName@@", "Direct Emails") + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

	@Test(groups = "restClientJava")
	public void getAlertSender_Mobicents() throws ParseException {
		HashMap<String, Object> result = this.getReponse(webResource, URIs.ALERT_SENDER_NAME.getUri().replace("@@senderName@@", "Mobicents") + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

	@Test(groups = "restClientJava")
	public void getAlertSender_NotExisting() throws ParseException {
		HashMap<String, Object> result = this.getReponse( webResource, URIs.ALERT_SENDER_NAME.getUri().replace("@@senderName@@", "SAURON") + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 404);
	}


	@Test(groups = "restClientJava")
	public void getEventSources() throws ParseException {
		HashMap<String, Object> result;
		result = this.getReponse(webResource, URIs.EVENT_SOURCES.getUri() .replace("@@id@@", platformId) + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
	}

	@Test(groups = "restClientJava")
	public void getEventDefinitions() throws ParseException {
		HashMap<String, Object> result;
		result = this.getReponse(webResource, URIs.EVENT_DEFINITIONS.getUri().replace("@@id@@", platformId) + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
	}

	@Test(groups = "restClientJava")
	public void getGroup() throws ParseException {
		HashMap<String, Object> result = this.getReponse(webResource, URIs.GROUP.getUri() + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

	@Test(groups = "restClientJava")
	public void getGroupWithParams() throws ParseException {
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("q", "lala");

		HashMap<String, Object> result = this.getReponse(webResource, URIs.GROUP.getUri() + ".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
	}

	@Test(groups = "restClientJava")
	public void getMetricsSchedule() throws ParseException {
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("type", "metric");

		HashMap<String, Object> result = this.getReponse(webResource, URIs.SCHEDULES.getUri().replace("@@id@@", platformId) + ".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);
	}

	@Test(groups = "restClientJava")
	public void getMetricsScheduleById() throws ParseException {
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("type", "metric");

		HashMap<String, Object> result = this.getReponse(webResource, URIs.SCHEDULES.getUri().replace("@@id@@", platformId) + ".json", queryParams);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

		JSONArray jsonArray = this.getJSONArray("" + result.get(RESPONSE_CONTENT));
		JSONObject jsonObject;
		jsonObject = (JSONObject) jsonArray.get(0);
		String scheduleId = "" + jsonObject.get(SCHEDULE_ID);

		result = this.getReponse(webResource, URIs.METRIC_SCHEDULE.getUri() .replace("@@id@@", scheduleId) + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

	@Test(groups = "restClientJava")
	public void getFavoriteResources() throws ParseException {
		HashMap<String, Object> result = this.getReponse(webResource, URIs.USER_FAVORITES_RESOURCE.getUri() + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}
	
	@Test(groups = "restClientJava")
	public void getFavoriteGroups() throws ParseException {
		HashMap<String, Object> result = this.getReponse(webResource, URIs.USER_FAVORITES_GROUP.getUri() + ".json", null);
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200);

	}

}