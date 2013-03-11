package com.redhat.qe.jon.common.util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

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
	protected WebResource webResource = null;
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
	
	protected static String LINKS = "links";
	protected static String RESOURCE_ID = "resourceId";
	protected static String PARENT_ID = "parentId";
	
	//Status.json page keys
	protected static String STATUS_VALUES 	= "values";
	protected static String SERVER_VERSION 	= "SERVER_VERSION";
	protected static String BUILD_NUMBER 	= "BuildNumber";
	
	
	protected static String SERVER_URI ;
	protected static String URI_PREFIX = "/rest/1"; 
	
	private static RestClient instance;
	
	public enum URIs
	{
		STATUS("/status"),		
		GROUP("/group"),
		ALERT("/alert"),
		ALERT_DEFINATION("alert/definition"),
		USER_FAVORITES_RESOURCE("user/favorites/resource"),
		PLATFORMS("/resource/platforms"),
		CHILDREN_RESOURCE("/resource/@@id@@/children");			
		
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
	private RestClient() {
	    this("rhqadmin","rhqadmin");
	}
	
	public RestClient(String username, String password) {
	    SERVER_URI = System.getProperty("jon.server.url",null);
	    if (SERVER_URI==null) {
		String jonHost = System.getProperty("jon.server.host",null);
		if (jonHost!=null) {
		    SERVER_URI="http://"+jonHost+":7080";
		}        		
	    }
	    _logger.fine("Creating REST Client instance "+SERVER_URI);	    
	    _logger.fine("Detecting REST endpoint...");
	    String auth = DatatypeConverter.printBase64Binary("rhqadmin:rhqadmin".getBytes());
	    if (checkEndpoint(SERVER_URI+"/rest/1",auth)) {
		URI_PREFIX= "/rest/1";
	    }
	    else if (checkEndpoint(SERVER_URI+"/rest",auth)) {
		URI_PREFIX= "/rest";
	    }
	    this.webResource = getWebResource(RestClient.SERVER_URI+URI_PREFIX, username, password);
	    
	}
	/**
	 * returns default (singleton) RestClient instance
	 * @return default RestClient instance
	 */
	public static RestClient getDefault() {
	    if (instance == null) {
		instance = new RestClient();
	    }
	    return instance;
	}
	
	private  boolean checkEndpoint(String endpoint, String auth) {
		HttpURLConnection connection = null;
		_logger.fine("Checking endpoint "+endpoint);		
		try {
			URL u = new URL(endpoint+"/status.json");			
			connection = (HttpURLConnection) u.openConnection();
			connection.setRequestProperty("Authorization", "Basic "+auth);
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		} catch (ConnectException e) {
			return false;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	/**
	 * 
	 * @return list of platform names imported to inventory
	 * @throws Exception
	 */
        public static List<String> getPlatformNames() throws Exception {
        	List<String> ret = new ArrayList<String>();
        	RestClient self = RestClient.getDefault();
        	Map<String, Object> result = self.getResponse(URIs.PLATFORMS.getUri() + ".json");
        	JSONArray jsonArray = null;
        	try {
        	    jsonArray = self.getJSONArray((String) result.get("response.content"));
        	} catch (ParseException e) {
        	    e.printStackTrace();
        	    return ret;
        	}
        	for (int i = 0; i < jsonArray.size(); i++) {
        	    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
        	    String platform = jsonObject.get("resourceName").toString();
        	    ret.add(platform);
        	}
        	return ret;
        }
	
	public static String detectServerInstallDir() {
	    try {
        	    RestClient self = RestClient.getDefault();
        	    Map<String, Object> result = self.getResponse(URIs.STATUS.getUri()+".json");
        	    JSONObject jsonObject = self.getJSONObject(""+result.get(RESPONSE_CONTENT));
        	    jsonObject = (JSONObject) jsonObject.get(STATUS_VALUES);
        	    return (String) jsonObject.get("SERVER_INSTALL_DIR");
	    }
	    catch (Exception ex) {
		_logger.severe("Unable to detect server install dir via REST: "+ex.getMessage());
		return null;
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
	
	public WebResource getWebResource(String url){
		_logger.info("URI: "+url);
		if(webResource == null){
			client = Client.create();
			webResource = client.resource(url);
			return webResource;
		}else{
			webResource = client.resource(url);
			return webResource;
		}
	}
	public Map<String,Object> getResponse(String url) {
	    return getResponse(this.webResource, url);
	}

	public HashMap<String, Object> getResponse(WebResource webResource, String url){
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
        //_logger.fine("Response: "+response);
        return response ;
	}
	
	public JSONObject getJSONObject(String content) throws ParseException{
		JSONParser parser=new JSONParser();
		return (JSONObject)parser.parse(content);
	}
	
	public JSONArray getJSONArray(String content) throws ParseException{
		JSONParser parser=new JSONParser();
        _logger.finest("Parsing content to JSONArray: " + content);
		return (JSONArray)parser.parse(content);
	}
	
	@SuppressWarnings("rawtypes")
	public StringBuffer getKeyValue(JSONObject map, StringBuffer keyValue){
		if(keyValue  == null){
			keyValue = new StringBuffer(); 
		}
		Set keys = map.keySet();
		Iterator itkey = keys.iterator();
	    while (itkey.hasNext()) {
	    	String key = ""+itkey.next();
	    	try{
	    		if(((JSONArray)map.get(key)).size() > 1){
		    		for(Object obj : (JSONArray)map.get(key)){
		    			getKeyValue((JSONObject)obj, keyValue);
		    		}
		    	}else{
		    		keyValue.append(key).append("=").append(map.get(key)).append("\n");	
		    	}
	    	}catch(Exception ex){
	    		if(ex.getMessage() == null){
	    			_logger.log(Level.WARNING, "Exception --> "+ex.getMessage(), ex);
	    		}else if(ex.getMessage().contains("cannot be cast to org.json.simple.JSONArray")){
	    			keyValue.append(key).append("=").append(map.get(key)).append("\n");
	    		}else{
	    			_logger.log(Level.SEVERE, "Exception --> "+ex.getMessage(), ex);
	    		}
	    	}	    	
	    }
	    return keyValue;
	}
	public void printKeyValue(JSONObject map){
		_logger.info("JSON Map: \n------------------------------------\n"+getKeyValue(map, null)+"------------------------------------");
	}
}