package com.redhat.qe.jon.rest.tests;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.rest.tasks.RestClient;

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
		HashMap<String, Object> result = this.getReponse(webResource, URIs.STATUS.getUri()+".json");
		if(testType.equalsIgnoreCase("positive")){
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Login validation check Positive, Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		}else{
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 401, "Login validation check Negative, Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
		}

	}

	@Test (groups="restClientJava")
	public void unauthorizedLoginTest(){
		webResource = this.getWebResource(SERVER_URI+URI_PREFIX);
		HashMap<String, Object> result = this.getReponse(webResource, URIs.STATUS.getUri()+".json");
		Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 401, "Unauthorized Login validation check, Negative, Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
	}

	@Test (groups="restClientJava")
	public void visitURIplatforms() throws ParseException{
		HashMap<String, Object> result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json");
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
			result = this.getReponse(webResource, URIs.PLATFORMS.getUri()+".json");
			Assert.assertEquals(result.get(RESPONSE_STATUS_CODE), 200, "Response Code: "+result.get(RESPONSE_STATUS_CODE)+", Response Message: "+result.get(RESPONSE_STATUS_MESSAGE));
			JSONArray jsonArray = this.getJSONArray(""+result.get(RESPONSE_CONTENT));
			_logger.fine("Number of Resource(s): "+jsonArray.size());
			Assert.assertTrue(jsonArray.size()>0, "Number of platform(s) [>0] : "+jsonArray.size());
			JSONObject jsonObject;
			jsonObject = (JSONObject) jsonArray.get(0);
			this.printKeyValue(jsonObject);
			parentId = ""+jsonObject.get(RESOURCE_ID);
		}

		result =  this.getReponse(webResource, URIs.CHILDREN_RESOURCE.getUri().replace("@@id@@", parentId)+".json");
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

}