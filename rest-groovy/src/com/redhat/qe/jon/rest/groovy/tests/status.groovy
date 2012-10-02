package com.redhat.qe.jon.rest.groovy.tests;

import org.testng.annotations.*
import org.testng.TestNG
import com.redhat.qe.jon.rest.groovy.RestClientTest
import org.testng.TestListenerAdapter
import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC

/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 20.12.2011
 */

class StatusTest extends RestClientTest {

	@BeforeClass
	def setUp() {
		login()
	}
	@Test
	void acceptHeader() {
		acceptHeaderTest('status')
	}
	@Test
	void rhqVersion() {
		client.get( path : 'status.json' ) { resp, json ->
			def version = json.values['SERVER_VERSION']+ ' (' +json.values['BuildNumber']+')'
			println 'Detected version: '+version
			System.setProperty('rhq.build.version',version)
		}
	}
}
