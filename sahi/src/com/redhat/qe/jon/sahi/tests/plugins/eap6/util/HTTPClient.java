package com.redhat.qe.jon.sahi.tests.plugins.eap6.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.redhat.qe.auto.testng.Assert;

public class HTTPClient {
	
	private final String host;
	private final int port;
	
	public HTTPClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	/**
	 * returns true if server is reachable and responds 200
	 * @return
	 */
	public boolean isRunning() {
		String url = "http://"+host+":"+String.valueOf(port);
		HttpURLConnection connection = null;
		try {
			URL u = new URL(url);
			connection = (HttpURLConnection) u.openConnection();
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
	 * asserts deployment content
	 * @param deployment name (like xxx.war, xxx.ear), note: this will try to reach server:port/xxx deployment
	 * @param contains string that should be present on deployment's home page
	 * @param message assert message
	 */
	public void assertDeploymentContent(String deployment,String contains,String message) {
		String context = deployment.replaceFirst("\\..*", "");
		String url = "http://"+host+":"+String.valueOf(port)+"/"+context;
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
}
