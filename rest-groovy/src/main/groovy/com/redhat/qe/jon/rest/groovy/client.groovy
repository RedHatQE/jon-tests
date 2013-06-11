package com.redhat.qe.jon.rest.groovy;

import org.testng.annotations.*
import org.testng.TestNG
import com.redhat.qe.Assert
import com.redhat.qe.jon.common.TestScript
import org.testng.TestListenerAdapter
import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC


/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 13.12.2011
 */
class RestClientTest extends TestScript{

	protected client

	@BeforeClass
	def baseSetUp() {
		// print '[jon.server.url] = '+System.properties['jon.server.url']
		client = new RESTClient( System.properties['jon.server.url'] + '/rest/' )
		// check if JON is UP
		try {
			client.get( path : 'status.json' )
		}
		catch (org.apache.http.conn.HttpHostConnectException ex) {
			Assert.fail(ex.message)
		}
		catch (Exception ex) {
			// that is OK, should fail on Unauthorized
		}
	}

	def login() {
		def username = 'rhqadmin'
		if (System.properties['rest.username']) {
			username = System.properties['rest.username']
		}
		def password = 'rhqadmin'
		if (System.properties['rest.password']) {
			password = System.properties['rest.password']
		}
		client.auth.basic username, password
	}

	def acceptHeaderTest(resource) {
		println 'Accept header [application/xml]'
		client.get(path : resource, headers : [ Accept : 'application/xml' ]) { resp,xml ->
			Assert.assertTrue resp.status == 200, 'Response status is 200'
			Assert.assertTrue resp.contentType == 'application/xml', 'reponse contentType is XML'
			print xml.class
		}
		println 'Accept header [application/json]'
		client.get(path : resource, headers : [ Accept : 'application/json' ]) { resp,json ->
			Assert.assertTrue resp.status == 200, 'Response status is 200'
			Assert.assertTrue resp.contentType == 'application/json', 'reponse contentType is JSON'
			Assert.assertTrue json instanceof net.sf.json.JSON, 'response parsed to JSON object'
		}
	}
}

