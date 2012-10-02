package com.redhat.qe.jon.rest.groovy.tests;

import org.testng.annotations.*
import org.testng.TestNG
import org.testng.TestListenerAdapter
import com.redhat.qe.jon.rest.groovy.RestClientTest
import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC


/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 12.12.2011
 */

class LoginTest extends RestClientTest {

	@Test(expectedExceptions=groovyx.net.http.HttpResponseException.class)
	void unauthorized() {
		client.get( path : 'status')
	}

	@Test(dependsOnMethods='unauthorized',expectedExceptions=groovyx.net.http.HttpResponseException.class)
	void invalidCredentials() {
		client.auth.basic 'myUserName', 'myPassword'
		client.get( path : 'status')
	}

	@Test(dependsOnMethods='invalidCredentials')
	void successfull() {
		login()
		client.get( path : 'status')
	}
}
