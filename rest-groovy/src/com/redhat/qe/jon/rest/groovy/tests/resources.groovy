package com.redhat.qe.jon.rest.groovy.tests;

import org.testng.annotations.*
import org.testng.TestNG
import org.testng.TestListenerAdapter
import com.redhat.qe.Assert
import com.redhat.qe.jon.rest.groovy.RestClientTest
import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC

/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 13.12.2011
 */

class ResourceTest extends RestClientTest {

	@BeforeClass
	def setUp() {
		login()
	}
	@Test
	void platforms() {
		acceptHeaderTest('resource/platforms')
	}

	void visitResource(href) {
		println "VisitResource href=${href}"
		client.get( path : href ) { resp, json ->
			println "Visited ${json.resourceId} : ${json.resourceName}"
			json.links.each { link ->
				// find link to resource's children
				if (link.get('rel') == 'children') {
					// list children
					client.get( path : link.get('href')) { resp2, json2 ->
						for (child in json2) {
							println "Found child ${child.resourceId}"
							// find each child link & recurse
							child.links.each { l ->
								if (l.get('rel') == 'self') {
									visitResource(l.get('href'))
								}
							}
						}
					}
				}
			}
		}
	}
	@Test
	void nonexistingResource() {
		client.handler.'404' = { resp ->
			Assert.assertTrue resp.status == 404, 'Response status is 404'
			Assert.assertTrue resp.contentType == 'application/xml', 'reponse contentType is XML'
		}
		client.get(path : 'resource/0.xml')
		client.handler.'404' = {resp ->
			Assert.assertTrue resp.status == 404, 'Response status is 404'
			Assert.assertTrue resp.contentType == 'application/json', 'reponse contentType is JSON'
		}
		client.get(path : 'resource/0.json')
		client.handler.remove('404')
	}
	@Test
	void visitAllResources() {
		client.get( path : 'resource/platforms.json' ) { resp, json ->
			json.each {
				it.links.each { link ->
					if (link.get('rel') == 'self') {
						visitResource(link.get('href'))
					}
				}
			}
		}
	}
}
