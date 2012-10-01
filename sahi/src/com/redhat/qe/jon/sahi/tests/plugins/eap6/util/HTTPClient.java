package com.redhat.qe.jon.sahi.tests.plugins.eap6.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import com.redhat.qe.Assert;

public class HTTPClient {
	private static final Logger log = Logger.getLogger(HTTPClient.class.getName());

	private final String serverAddress;
	
	public HTTPClient(String host, int port) {		
		this("http://"+host+":"+String.valueOf(port));
	}
	
	public HTTPClient(String serverAddress) {
		this.serverAddress = serverAddress;
		log.info("Creating HTTPClient that will connect to ["+getServerAddress()+"]");
	}
	/**
	 * returns true if server is reachable and responds 200 or 401 (unauthorized)
	 * @return true if server is reachable and responds 200 or 401 (unauthorized)
	 */
	public boolean isRunning() {
		log.fine("Check whether "+getServerAddress()+" is running");
		HttpURLConnection connection = null;
		try {
			URL u = new URL(getServerAddress());
			connection = (HttpURLConnection) u.openConnection();
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK || connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED;
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
	 * gets server's http URL, where it is available
	 * @return server's http URL
	 */
	public String getServerAddress() {
		return serverAddress;
	}
	/**
	 * asserts deployment content
	 * @param deployment name (like xxx.war, xxx.ear), note: this will try to reach server:port/xxx deployment
	 * @param contains string that should be present on deployment's home page
	 * @param message assert message
	 */
	public void assertDeploymentContent(String deployment,String contains,String message) {
		String context = deployment.replaceFirst("\\..*", "");
		String url = getServerAddress()+"/"+context;
		log.fine("Check whether "+context+" is available and returns content that contains : "+contains);
		HttpURLConnection connection = null;
		try {
			URL u = new URL(url);
			connection = (HttpURLConnection) u.openConnection();
			Assert.assertTrue(connection.getResponseCode() == HttpURLConnection.HTTP_OK, "Deployment "+deployment+" is reachable on EAP via HTTP request");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			for (String line; (line = reader.readLine()) != null;) {
	            if (line.contains(contains)) {
	            	Assert.assertTrue(true, message);
	            	return;
	            }
	        }
			Assert.assertTrue(false,message);
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		}catch (ConnectException e) {
			Assert.assertTrue(false,message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	/**
	 * returns true when given deployment is reachable/available on server
	 * @param deployment name (like xxx.war, xxx.ear), note: this will try to reach server:port/xxx deployment
	 * @return true when given deployment is reachable/available on server
	 */
	public boolean isDeploymentAvailable(String deployment) {
		String context = deployment.replaceFirst("\\..*", "");
		String url = getServerAddress()+"/"+context;
		log.fine("Check whether "+context+" is available");
		HttpURLConnection connection = null;
		try {
			URL u = new URL(url);
			connection = (HttpURLConnection) u.openConnection();
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		}catch (ConnectException e) {
			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
