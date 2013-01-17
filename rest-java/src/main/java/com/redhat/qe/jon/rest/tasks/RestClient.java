package com.redhat.qe.jon.rest.tasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.redhat.qe.jon.common.TestScript;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * @author jkandasa (Jeeva Kandasamy), ahovsepy
 * @since Dec 20, 2011
 */

public class RestClient extends TestScript {
	private static Logger _logger = Logger
			.getLogger(RestClient.class.getName());

	protected static Client client = null;
	protected static WebResource webResource = null;
	protected static String RESPONSE_STATUS_CODE = "reposonse.status.code";
	protected static String RESPONSE_STATUS_MESSAGE = "response.status.message";
	protected static String RESPONSE_DATE = "reposnse.date";
	protected static String RESPONSE_CONTENT_LENGTH = "reponse.content.length";
	protected static String RESPONSE_EXPIRES = "response.expires";
	protected static String RESPONSE_CONTENT_TYPE = "response.content.type";
	protected static String RESPONSE_CONTENT = "response.content";
	protected static String RESPONSE_OTHER = "response.others";
	protected static String RESPONSE_X_POWERED_BY = "response.x.powered.by";
	protected static String RESPONSE_SERVER = "response.server";

	protected static String LINKS = "links";
	protected static String RESOURCE_ID = "resourceId";
	protected static String SCHEDULE_ID = "scheduleId";
	protected static String PARENT_ID = "parentId";
	protected static String ID = "id";
	protected static String NAME = "name";
	protected static String RESOURCE_NAME = "resourceName";

	// Status.json page keys
	protected static String STATUS_VALUES = "values";
	protected static String SERVER_VERSION = "SERVER_VERSION";
	protected static String BUILD_NUMBER = "BuildNumber";

	protected static String SERVER_URI;
	protected static String URI_PREFIX = "/rest/1";

	public enum URIs {
		ROOT_URI("/"),
		INDEX("/index"),
		STATUS("/status"), 
		GROUP("/group"), 
		ALERT("/alert"), 
		ALERT_DEFINATION("/alert/definition"), 
		ALERT_DEFINATIONS("/alert/definitions"),
		ALERT_COUNT("/alert/count"),
		ALERT_SENDERS("/alert/senders"),
		ALERT_SENDER_NAME("/alert/sender/@@senderName@@"),
		EVENT_SOURCES("/event/@@id@@/sources"),
		EVENT_DEFINITIONS("/event/@@id@@/definitions"),
		USER_FAVORITES_RESOURCE("/user/favorites/resource"), 
		USER_FAVORITES_GROUP("/user/favorites/group"),
		PLATFORMS("/resource/platforms"), 
		PLATFORM_BY_ID("/resource/@@id@@"), 
		RESOURCE("/resource"),
		RESOURCE_ALERTS("/resource/@@id@@/alerts"),
		RESOURCE_AVAILABILITY("/resource/@@id@@/availability"),
		RESOURCE_AVAILABILITY_HISTORY("/resource/@@id@@/availability/history"),
		RESOURCE_SCHEDULES("/resource/@@id@@/schedules"),
		OPERATION_DEFINITION("/operation/definition/@@id@@"), 
		OPERATION_DEFINITIONS("/operation/definitions"), 
		METRIC_DATA("/metric/data/@@id@@"),
		METRIC_SCHEDULE("/metric/schedule/@@id@@"),
		SCHEDULES("/resource/@@id@@/schedules"),
		CHILDREN_RESOURCE("/resource/@@id@@/children");

		String uri;

		URIs(String uri) {
			this.uri = uri;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getFullUri() {
			return (URI_PREFIX + uri);
		}
	}

	public WebResource getWebResource(String url, String userName,
			String passWord) {
		_logger.info("URI: " + url);
		if (webResource == null) {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(userName, passWord));
			webResource = client.resource(url);
			return webResource;
		} else {
			client.addFilter(new HTTPBasicAuthFilter(userName, passWord));
			webResource = client.resource(url);
			return webResource;
		}
	}

	public WebResource getWebResource(String url) {
		_logger.info("URI: " + url);
		if (webResource == null) {
			client = Client.create();
			webResource = client.resource(url);
			return webResource;
		} else {
			webResource = client.resource(url);
			return webResource;
		}
	}

	public HashMap<String, Object> getReponse(WebResource webResource,
			String url, MultivaluedMap queryParams) {
		_logger.info("URI: " + url);
		ClientResponse clientResponse ;
		
		if(queryParams != null)
			 clientResponse = webResource.path(url).queryParams(queryParams).get(ClientResponse.class); 
		else clientResponse = webResource.path(url).get(ClientResponse.class);
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put(RESPONSE_STATUS_CODE, clientResponse.getStatus());
		response.put(RESPONSE_STATUS_MESSAGE,
				clientResponse.getClientResponseStatus());
		response.put(RESPONSE_CONTENT_LENGTH,
				clientResponse.getHeaders().get("Content-Length"));
		response.put(RESPONSE_CONTENT_TYPE,
				clientResponse.getHeaders().get("Content-Type"));
		response.put(RESPONSE_DATE, clientResponse.getHeaders().get("Date"));
		response.put(RESPONSE_EXPIRES,
				clientResponse.getHeaders().get("Expires"));
		response.put(RESPONSE_SERVER, clientResponse.getHeaders().get("Server"));
		response.put(RESPONSE_X_POWERED_BY,
				clientResponse.getHeaders().get("X-Powered-By"));
		response.put(RESPONSE_CONTENT, clientResponse.getEntity(String.class));
		_logger.fine("Response: " + response);
		return response;
	}

	
	public HashMap<String, Object> postResponse(WebResource webResource,
			String uri, MultivaluedMap queryParam) {
		ClientResponse clientResponse ;
		
if(queryParam != null)
		 clientResponse = webResource.path(uri).queryParams(queryParam).post(ClientResponse.class);
 else 
	     clientResponse = webResource.path(uri).post(ClientResponse.class);

		//_logger.log(Level.INFO, webResource.getURI().getQuery());
	
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put(RESPONSE_STATUS_CODE, clientResponse.getStatus());
		response.put(RESPONSE_STATUS_MESSAGE,
				clientResponse.getClientResponseStatus());
		response.put(RESPONSE_CONTENT_LENGTH,
				clientResponse.getHeaders().get("Content-Length"));
		response.put(RESPONSE_CONTENT_TYPE,
				clientResponse.getHeaders().get("Content-Type"));
		response.put(RESPONSE_CONTENT, clientResponse.getEntity(String.class));
		_logger.fine("Response: " + response);
		return response;

	}

	public JSONObject getJSONObject(String content) throws ParseException {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(content);
	}

	public JSONArray getJSONArray(String content) throws ParseException {
		JSONParser parser = new JSONParser();
		return (JSONArray) parser.parse(content);
	}

	@SuppressWarnings("rawtypes")
	public StringBuffer getKeyValue(JSONObject map, StringBuffer keyValue) {
		if (keyValue == null) {
			keyValue = new StringBuffer();
		}
		Set keys = map.keySet();
		Iterator itkey = keys.iterator();
		while (itkey.hasNext()) {
			String key = "" + itkey.next();
			try {
				if (((JSONArray) map.get(key)).size() > 1) {
					for (Object obj : (JSONArray) map.get(key)) {
						getKeyValue((JSONObject) obj, keyValue);
					}
				} else {
					keyValue.append(key).append("=").append(map.get(key))
							.append("\n");
				}
			} catch (Exception ex) {
				if (ex.getMessage() == null) {
					_logger.log(Level.WARNING,
							"Exception --> " + ex.getMessage(), ex);
				} else if (ex.getMessage().contains(
						"cannot be cast to org.json.simple.JSONArray")) {
					keyValue.append(key).append("=").append(map.get(key))
							.append("\n");
				} else {
					_logger.log(Level.SEVERE,
							"Exception --> " + ex.getMessage(), ex);
				}
			}
		}
		return keyValue;
	}

	public void printKeyValue(JSONObject map) {
		_logger.info("JSON Map: \n------------------------------------\n"
				+ getKeyValue(map, null)
				+ "------------------------------------");
	}
}