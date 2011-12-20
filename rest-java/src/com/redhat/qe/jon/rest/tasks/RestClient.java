package com.redhat.qe.jon.rest.tasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * @since Dec 20, 2011
 */



public class RestClient {
	private static Logger _logger = Logger.getLogger(RestClient.class.getName());
	
	protected static Client client = null;
	protected static WebResource webResource = null;
	protected static String RESPONSE_STATUS_CODE= "reposonse.status.code";
	protected static String RESPONSE_STATUS_MESSAGE= "response.status.message";
	protected static String RESPONSE_DATE= "reposnse.date";
	protected static String RESPONSE_CONTENT_LENGTH= "reponse.content.length";
	protected static String RESPONSE_EXPIRES= "response.expires";
	protected static String RESPONSE_CONTENT_TYPE= "response.content.type";
	protected static String RESPONSE_CONTENT= "response.content";
	protected static String RESPONSE_OTHER= "response.others";
	protected static String RESPONSE_X_POWERED_BY= "response.x.powered.by";
	protected static String RESPONSE_SERVER = "response.server";
	
	protected static String SERVER_URI ;
	protected static String URI_PREFIX = "/rest/1"; 
	
	public enum URIs
	{
		STATUS("/status"),
		GROUP("/group"),
		ALERT("/alert"),
		ALERT_DEFINATION("alert/definition"),
		USER_FAVORITES_RESOURCE("user/favorites/resource"),
		PLATFORMS("/resource/platforms");			
		
		String uri;
		URIs(String uri)
		{
			this.uri=uri;
		}
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getFullUri() {
			return (URI_PREFIX+uri);
		}
	}
	
	public WebResource getWebResource(String url, String userName, String passWord){
		_logger.info("URI: "+url);
		if(webResource == null){
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(userName, passWord)); 
			webResource = client.resource(url);
			return webResource;
		}else{
			client.addFilter(new HTTPBasicAuthFilter(userName, passWord)); 
			webResource = client.resource(url);
			return webResource;
		}
	}	

	public HashMap<String, Object> getReponse(WebResource webResource, String url){
		_logger.info("URI: "+url);
        ClientResponse clientResponse = webResource.path(url).get(ClientResponse.class );
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put(RESPONSE_STATUS_CODE, clientResponse.getStatus());
        response.put(RESPONSE_STATUS_MESSAGE, clientResponse.getClientResponseStatus());
        response.put(RESPONSE_CONTENT_LENGTH, clientResponse.getHeaders().get("Content-Length"));
        response.put(RESPONSE_CONTENT_TYPE, clientResponse.getHeaders().get("Content-Type"));
        response.put(RESPONSE_DATE, clientResponse.getHeaders().get("Date"));
        response.put(RESPONSE_EXPIRES, clientResponse.getHeaders().get("Expires"));
        response.put(RESPONSE_SERVER, clientResponse.getHeaders().get("Server"));
        response.put(RESPONSE_X_POWERED_BY, clientResponse.getHeaders().get("X-Powered-By"));
        response.put(RESPONSE_CONTENT, clientResponse.getEntity(String.class));
        _logger.fine("Response: "+response);
        return response ;
	}
	
	public JSONObject getJSONObject(String content) throws ParseException{
		JSONParser parser=new JSONParser();
		return (JSONObject)parser.parse(content);
	}
	
	public JSONArray getJSONArray(String content) throws ParseException{
		JSONParser parser=new JSONParser();
		return (JSONArray)parser.parse(content);
	}
	
	@SuppressWarnings("rawtypes")
	public void printKeyValue(JSONObject map){	
		StringBuffer keyValue = new StringBuffer(); 
		Set keys = map.keySet();
		Iterator itkey = keys.iterator();
	    while (itkey.hasNext()) {
	    	String key = ""+itkey.next();
	    	keyValue.append(key).append("=").append(map.get(key)).append("\n");
	    }
	    _logger.info("JSON Map: \n------------------------------------\n"+keyValue+"------------------------------------");
	}
}